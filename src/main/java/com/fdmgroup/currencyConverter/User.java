package com.fdmgroup.currencyConverter;

import java.math.BigDecimal;
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
	public void addCurrency(String currencyName, BigDecimal amount) {
		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}
		// should only add amounts that are max 2 decimal places
		// always round down amount added
		String currencylower = currencyName.toLowerCase();
		BigDecimal existingAmount = wallet.get(currencylower) != null
				? new BigDecimal(Double.toString(wallet.get(currencylower)))
				: BigDecimal.ZERO;
		BigDecimal finalAmount = CurrencyRounder.roundCurrency(existingAmount.add(amount), false);
		wallet.put(currencylower, finalAmount.doubleValue());
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
	public void subtractCurrency(String currencyName, BigDecimal amount) throws UserInsufficientBalance {
		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		String currencyLower = currencyName.toLowerCase();
		BigDecimal existingAmount = wallet.get(currencyLower) != null
				? new BigDecimal(Double.toString(wallet.get(currencyLower)))
						: BigDecimal.ZERO;
		BigDecimal subtractAmount = CurrencyRounder.roundCurrency(amount, false);
		if (existingAmount.compareTo(subtractAmount) < 0) {
			String errorMessage = String.format(
					"User %s has insufficient balance of %s. Current balance: %.2f, required balance: %.2f.", name,
					currencyName, existingAmount.doubleValue(), subtractAmount.doubleValue());
			throw new UserInsufficientBalance(errorMessage);
		}

		BigDecimal remainingAmount = existingAmount.subtract(subtractAmount);
		remainingAmount = CurrencyRounder.roundCurrency(remainingAmount, false);
		if (remainingAmount.compareTo(BigDecimal.ZERO) == 0) {
			wallet.remove(currencyLower);
			return;
		}
		wallet.put(currencyLower, remainingAmount.doubleValue());
	}

}
