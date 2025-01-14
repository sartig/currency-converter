package com.fdmgroup.currencyConverter.transaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileSystems;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to parse transaction list file into a Queue of transactions, thus
 * ensuring transactions are executed in the order specified.
 */
public class TransactionReader {
	private Logger logger = LogManager.getLogger(TransactionReader.class);

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	/**
	 * Method to read the transactions into memory.
	 * <p>
	 * The transactions file is expected to store each transaction on a new line and
	 * be formatted as four space-separated-values:
	 * <p>
	 * [User name] [currency from code] [currency to code] [amount]
	 * <p>
	 * The currency codes are expected to be lowercase three-letter representations.
	 * The amount is expected to be numeric.
	 * 
	 * @param filePath Path to file containing transactions
	 * @return Returns transactions in the same order as in the file
	 */
	public Queue<Transaction> readTransactions(String filePath) {
		String properFilePath = filePath.replace("/", FileSystems.getDefault().getSeparator());
		Queue<Transaction> transactionQueue = new LinkedList<>();
		try (FileReader fr = new FileReader(properFilePath); BufferedReader br = new BufferedReader(fr)) {
			String line;
			int lineNumber = 0;
			while ((line = br.readLine()) != null) {
				lineNumber++;
				String[] dataMembers = line.split(" ");
				if (dataMembers.length != 4) {
					logger.error("Unable to parse line " + lineNumber + ": '" + line + "' - skipping to next line");
					continue;
				}
				String name = dataMembers[0];
				String currencyFrom = dataMembers[1];
				String currencyTo = dataMembers[2];

				try {
					BigDecimal amount = new BigDecimal(dataMembers[3]);
					transactionQueue.add(new Transaction(name, currencyFrom, currencyTo, amount));

				} catch (NumberFormatException nfe) {
					logger.error("Unable to parse line " + lineNumber + ": amount '" + dataMembers[3]
							+ "' as a BigDecimal - skipping to next line");
				}
			}
		} catch (IOException e) {
			logger.error("Unable to read file at path " + properFilePath);
		}
		if (transactionQueue.isEmpty()) {
			logger.warn("Parsing file at " + properFilePath + " resulted in zero transactions");
		} else {
			logger.info("Successfully parsed " + transactionQueue.size() + " transactions from file " + properFilePath);
		}
		return transactionQueue;
	}
}
