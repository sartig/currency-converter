package com.fdmgroup.currencyConverter.currency;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility class to aid in rounding values to 2 decimal places
 */
public class CurrencyRounder {
	/**
	 * 
	 * @param amount        Value to round as a BigDecimal
	 * @param shouldRoundUp Uses {@link java.Math#RoundingMode} {@code CEILING} if
	 *                      true, otherwise uses {@code FLOOR}
	 * @return Returns BigDecimal with 2 decimal places
	 */
	public static BigDecimal roundCurrency(BigDecimal amount, boolean shouldRoundUp) {
		String stringAmount = amount.toPlainString();
		if (shouldRoundUp) {
			return new BigDecimal(stringAmount).setScale(2, RoundingMode.CEILING);
		}
		return new BigDecimal(stringAmount).setScale(2, RoundingMode.FLOOR);

	}
}
