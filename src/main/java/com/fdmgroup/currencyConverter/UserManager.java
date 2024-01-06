package com.fdmgroup.currencyConverter;

import java.util.ArrayList;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class used for managing Users in app memory.
 * 
 * Singleton to conserve memory
 */
public class UserManager {
	private static final String FX_JSON_FILE_PATH = "src/main/resources/users.json";
	private static UserManager instance;
	private ArrayList<User> userData = new ArrayList<User>();
	private Logger logger = LogManager.getLogger(UserManager.class);

	/**
	 * Constructor is private as {@code UserManager} is a singleton. The JSON
	 * data only needs to be loaded once
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
	 * Check if a user is in the ArrayList
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
	 * 
	 * @param name
	 * @param currency
	 * @return
	 */
	public double getUserBalance(String name, String currency) {
		for (User u : userData) {
			if (!u.getName().equals(name)) {
				continue;
			}
			Map<String, Double> userWallet = u.getWallet();
			if (userWallet.get(currency) == null) {
				return 0;
			}
			return userWallet.get(currency);

		}
		return 0;
	}

	public void executeTransaction(Transaction transaction) {
		String name = transaction.getName(), currencyFrom = transaction.getCurrencyFrom(),
				currencyTo = transaction.getCurrencyTo();
		double amount = transaction.getAmount();

		for (User u : userData) {
			if (!u.getName().equals(name)) {
				continue;
			}
			try {
				u.subtractCurrency(currencyFrom, CurrencyRounder.roundCurrency(amount, false));
				double addAmount = CurrencyConverter.getInstance().convert(currencyFrom, currencyTo, amount);
				u.addCurrency(currencyTo, addAmount);
			} catch (UserInsufficientBalance e) {
				logger.error(e.getMessage());
			}
		}
	}
}
