package com.fdmgroup.currencyConverter;

/**
 * Simple POJO class to hold information on an individual transaction
 */
public class Transaction {
	private String name;
	private String currencyFrom;
	private String currencyTo;
	private double amount;

	public Transaction(String name, String currencyFrom, String currencyTo, double amount) {
		super();
		this.name = name;
		this.currencyFrom = currencyFrom;
		this.currencyTo = currencyTo;
		this.amount = amount;
	}

	public String getName() {
		return name;
	}

	public String getCurrencyFrom() {
		return currencyFrom;
	}

	public String getCurrencyTo() {
		return currencyTo;
	}

	public double getAmount() {
		return amount;
	}
}
