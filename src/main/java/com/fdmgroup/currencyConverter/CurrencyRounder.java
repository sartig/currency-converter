package com.fdmgroup.currencyConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyRounder {
	public static double roundCurrency(double amount, boolean shouldRoundUp) {
		double testValue = (double) ((int) (amount * 100)) / 100;
		if (testValue == amount) {
			return amount;
		}
		if (shouldRoundUp) {
			return new BigDecimal(amount).setScale(2, RoundingMode.UP).doubleValue();
		}
		return new BigDecimal(amount).setScale(2, RoundingMode.DOWN).doubleValue();
	}
}
