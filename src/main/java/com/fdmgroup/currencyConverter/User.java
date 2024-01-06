package com.fdmgroup.currencyConverter;

import java.util.HashMap;
import java.util.Map;

public class User {
	private String name;
	private Map<String, Double> wallet = new HashMap<String, Double>();

	/**
	 * No-args constructor required for Jackson deserialisation.
	 */
	public User() {

	}

	/**
	 * 
	 * @param name   User name
	 * @param wallet User wallet
	 */
	public User(String name, Map<String, Double> wallet) {
		super();
		this.name = name;
		this.wallet = wallet;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Double> getWallet() {
		return wallet;
	}

	/**
	 * Adds currency to the user's wallet.
	 * 
	 * @param currencyName Currency to add
	 * @param amount       Amount to add. Will truncate to 2 decimal places.
	 */
	public void addCurrency(String currencyName, double amount) {
		if (amount == 0) {
			return;
		}
		// should only add amounts that are max 2 decimal places
		// always round down amount added
		double roundedAmount = CurrencyRounder.roundCurrency(amount, false);
		String currencylower = currencyName.toLowerCase();
		double existingAmount = wallet.get(currencylower) != null ? wallet.get(currencylower) : 0;
		wallet.put(currencylower, existingAmount + roundedAmount);
	}

	/**
	 * Removes currency from the user's wallet.
	 * 
	 * @param currencyName Currency to remove
	 * @param amount       Amount to remove. Will truncate after 2 decimal places.
	 * @throws UserInsufficientBalance If the user does not have enough of the
	 *                                 specific currency to be removed. Leaves the
	 *                                 wallet unchanged.
	 */
	public void subtractCurrency(String currencyName, double amount) throws UserInsufficientBalance {
		if (amount == 0) {
			return;
		}

		// should only subtract amounts that are max 2 decimal places
		// always round up amount subtracted
		double roundedAmount = CurrencyRounder.roundCurrency(amount, false);

		String currencyLower = currencyName.toLowerCase();
		double existingAmount = wallet.get(currencyLower) != null ? wallet.get(currencyLower) : 0;

		if (existingAmount < roundedAmount) {
			String errorMessage = String.format(
					"User %s has insufficient balance of %s. Current balance: %.2f, required balance: %.2f.", name,
					currencyName, existingAmount, roundedAmount);
			throw new UserInsufficientBalance(errorMessage);
		}

		double remainingAmount = existingAmount - roundedAmount;
		if (remainingAmount == 0) {
			wallet.remove(currencyLower);
			return;
		}
		wallet.put(currencyLower, remainingAmount);
	}

}
