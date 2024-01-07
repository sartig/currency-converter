package com.fdmgroup.currencyConverter.transaction;

import java.math.BigDecimal;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fdmgroup.currencyConverter.currency.CurrencyConverter;
import com.fdmgroup.currencyConverter.io.UserJsonDataWriter;
import com.fdmgroup.currencyConverter.user.UserManager;

public class TransactionProcessor {
	private UserManager userManager;
	private UserJsonDataWriter userJsonDataWriter;
	private Logger logger;
	private CurrencyConverter currencyConverter;
	private TransactionReader transactionReader;

	/**
	 * No-args constructor for normal use
	 */
	public TransactionProcessor() {
		userManager = UserManager.getInstance();
		userJsonDataWriter = new UserJsonDataWriter();
		logger = LogManager.getLogger(TransactionProcessor.class);
		currencyConverter = CurrencyConverter.getInstance();
		transactionReader = new TransactionReader();
	}

	/**
	 * Constructor with all params for testing with Mockito
	 * 
	 * @param userManager
	 * @param userJsonDataWriter
	 * @param logger
	 * @param currencyConverter
	 * @param transactionReader
	 */
	public TransactionProcessor(UserManager userManager, UserJsonDataWriter userJsonDataWriter, Logger logger,
			CurrencyConverter currencyConverter, TransactionReader transactionReader) {
		this.userManager = userManager;
		this.userJsonDataWriter = userJsonDataWriter;
		this.logger = logger;
		this.currencyConverter = currencyConverter;
		this.transactionReader = transactionReader;
	}

	public void processAllTransactions(String transactionFilePath, String userDataFilePath) {

		Queue<Transaction> transactions = transactionReader.readTransactions(transactionFilePath);
		if (transactions == null) {
			logger.warn("com.fdmgroup.currencyConverter.transaction list is null");
			return;
		}
		int transactionNum = transactions.size();
		if (transactionNum == 0) {
			logger.warn("com.fdmgroup.currencyConverter.transaction list is empty");
			return;
		}
		int validTransactions = 0;
		while (!transactions.isEmpty()) {
			Transaction currentTransaction = transactions.poll();
			if (validateTransaction(currentTransaction)) {
				validTransactions++;
				userManager.executeTransaction(currentTransaction);
				// update after each com.fdmgroup.currencyConverter.transaction so file is more closely synced to progress
				updateUserFile(userDataFilePath);
			}
		}
		logger.info("Processed " + validTransactions + " of " + transactionNum + " transactions");

	}

	private boolean validateTransaction(Transaction transaction) {
		String name = transaction.getName(), currencyFrom = transaction.getCurrencyFrom(),
				currencyTo = transaction.getCurrencyTo();
		BigDecimal amount = transaction.getAmount();

		String logBase = String.format("com.fdmgroup.currencyConverter.transaction request %s: %.2f %s to %s ", name, amount, currencyFrom,
				currencyTo);
		String invalidLog = logBase + "is invalid due to ";
		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			logger.warn(invalidLog + "zero-value amount");
			return false;
		}
		if (currencyFrom.toLowerCase().equals(currencyTo.toLowerCase())) {
			logger.warn(invalidLog + "identical currencies");
			return false;
		}
		if (!currencyConverter.validate(currencyFrom)) {
			logger.warn(invalidLog + "invalid currency " + currencyFrom);
			return false;
		}
		if (!currencyConverter.validate(currencyTo)) {
			logger.warn(invalidLog + "invalid currency " + currencyTo);
			return false;
		}
		// at higher use number of users will be higher than number of currencies
		// so do the slower check later
		if (!userManager.validateUser(name)) {
			logger.warn(invalidLog + "user not existing in list");
			return false;
		}
		BigDecimal userAmount = userManager.getUserBalance(name, currencyFrom);
		if (userAmount.compareTo(amount) == -1) {
			logger.warn(invalidLog + String.format("insufficient balance in user wallet (%.2f)", userAmount));
			return false;
		}
		logger.info(logBase + "is valid");
		return true;
	}

	private void updateUserFile(String userDataFilePath) {
		userJsonDataWriter.writeDataToFilePath(userDataFilePath, userManager.getUserData());
	}

}
