package com.fdmgroup.currencyConverter.user;

public class UserInsufficientBalance extends Exception {

	private static final long serialVersionUID = -3935008787368616719L;

	public UserInsufficientBalance(String errorMessage) {
		super(errorMessage);
	}
}
