package com.fdmgroup.currencyConverter.currency;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyRounder {

	public static BigDecimal roundCurrency(BigDecimal amount, boolean shouldRoundUp) {
		String stringAmount = amount.toPlainString();
		if (shouldRoundUp) {
			return new BigDecimal(stringAmount).setScale(2, RoundingMode.CEILING);
		}
		return new BigDecimal(stringAmount).setScale(2, RoundingMode.FLOOR);

	}
}
