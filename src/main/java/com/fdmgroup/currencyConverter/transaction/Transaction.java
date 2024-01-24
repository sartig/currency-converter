package com.fdmgroup.currencyConverter.transaction;

import java.math.BigDecimal;

/**
 * Simple POJO class to hold information on an individual transaction
 */
public record Transaction(String name, String currencyFrom, String currencyTo, BigDecimal amount) {
}
