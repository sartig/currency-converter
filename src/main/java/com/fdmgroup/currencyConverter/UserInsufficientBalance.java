package com.fdmgroup.currencyConverter;

public class UserInsufficientBalance extends Exception {
	public UserInsufficientBalance(String errorMessage) {
		super(errorMessage);
	}
}
