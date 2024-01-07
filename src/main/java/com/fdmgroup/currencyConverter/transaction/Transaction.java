package com.fdmgroup.currencyConverter.transaction;

import java.math.BigDecimal;

/**
 * Simple POJO class to hold information on an individual com.fdmgroup.currencyConverter.transaction
 */
public class Transaction {
	private String name;
	private String currencyFrom;
	private String currencyTo;
	private BigDecimal amount;

	public Transaction(String name, String currencyFrom, String currencyTo, BigDecimal amount) {
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

	public BigDecimal getAmount() {
		return amount;
	}
}
