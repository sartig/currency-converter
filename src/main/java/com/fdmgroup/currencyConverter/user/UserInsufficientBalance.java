package com.fdmgroup.currencyConverter.user;

/**
 * Error called when attempting to subtract an amount that exceeds the user's
 * balance of that currency.
 */
public class UserInsufficientBalance extends Exception {

	private static final long serialVersionUID = -3935008787368616719L;

	public UserInsufficientBalance(String errorMessage) {
		super(errorMessage);
	}
}
