package com.fdmgroup.currencyConverter.user;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fdmgroup.currencyConverter.currency.CurrencyConverter;
import com.fdmgroup.currencyConverter.currency.CurrencyRounder;
import com.fdmgroup.currencyConverter.io.UserJsonDataReader;
import com.fdmgroup.currencyConverter.transaction.Transaction;

/**
 * Class used for managing all Users. Is a singleton to conserve memory
 */
public class UserManager {
	private static final String FX_JSON_FILE_PATH = "src/main/resources/users.json";
	private static UserManager instance;
	private ArrayList<User> userData;
	private Logger logger = LogManager.getLogger(UserManager.class);

	/**
	 * Constructor is private as {@code UserManager} is a singleton. The JSON data
	 * only needs to be loaded once
	 */
	private UserManager() {
		// load json and store into ArrayList
		UserJsonDataReader userJsonDataReader = new UserJsonDataReader();
		userData = userJsonDataReader.loadDataFromFilePath(FX_JSON_FILE_PATH);
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	/**
	 * Singleton method
	 * 
	 * @return Returns UserManager singleton
	 */
	public static UserManager getInstance() {
		if (instance == null) {
			instance = new UserManager();
		}
		return instance;
	}

	public ArrayList<User> getUserData() {
		return userData;
	}

	public void setUserData(ArrayList<User> userData) {
		this.userData = userData;
	}

	/**
	 * Check if a user is in the ArrayList. Is case-sensitive
	 * 
	 * @param name User name
	 * @return Returns true if the user is in the list, false if it is not
	 */
	public boolean validateUser(String name) {
		for (User u : userData) {
			if (u.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method to get a user's balance of a specific currency
	 * 
	 * @param name     User name
	 * @param currency Currency as a lowercase three-letter code
	 * @return Currency amount; returns 0 if currency does not exist in the wallet
	 */
	public BigDecimal getUserBalance(String name, String currency) {
		for (User u : userData) {
			if (!u.getName().equals(name)) {
				continue;
			}
			Map<String, Double> userWallet = u.getWallet();
			if (userWallet.get(currency) == null) {
				return BigDecimal.ZERO;
			}
			return new BigDecimal(Double.toString(userWallet.get(currency)));

		}
		return BigDecimal.ZERO;
	}

	/**
	 * Method to execute a transaction, converting some amount of one of a user's
	 * currencies to another. Will not execute the transaction if the user does not
	 * have the amount of currency required
	 * 
	 * @param transaction Transaction data
	 */
	public void executeTransaction(Transaction transaction) {
		String name = transaction.name(), currencyFrom = transaction.currencyFrom(),
				currencyTo = transaction.currencyTo();
		BigDecimal amount = transaction.amount();

		if (amount.compareTo(BigDecimal.ZERO) == 0 || currencyFrom.equalsIgnoreCase(currencyTo)) {
			return;
		}

		for (User u : userData) {
			if (!u.getName().equals(name)) {
				continue;
			}
			try {
				BigDecimal subtractAmount = CurrencyRounder.roundCurrency(amount, false);
				u.subtractCurrency(currencyFrom, subtractAmount);
				BigDecimal addAmount = CurrencyConverter.getInstance().convert(currencyFrom, currencyTo, amount);
				u.addCurrency(currencyTo, addAmount);
				logger.info(String.format("User %s: converted %.2f %s to %.2f %s", u.getName(),
						subtractAmount.doubleValue(), currencyFrom, addAmount.doubleValue(), currencyTo));
				logger.info(String.format("Updated user %s balance: %.2f %s, %.2f %s", u.getName(),
                        u.getWallet().get(currencyFrom), currencyFrom,
                        u.getWallet().get(currencyTo), currencyTo));
				return;
			} catch (UserInsufficientBalance e) {
				logger.error(e.getMessage());
			}
		}
		
		logger.warn(String.format(
				"Transaction for user %s: %.2f %s to %s failed due to user not existing in database",
				name, amount, currencyFrom, currencyTo));
	}
}
