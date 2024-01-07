package com.fdmgroup.currencyConverter.currency;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class CurrencyRounderTest {

	@Test
	void roundCurrency_DoesNotRound_IntegerValues() {
		BigDecimal start = new BigDecimal("10");
		assertEquals(0, start.compareTo(CurrencyRounder.roundCurrency(start, false)));
		assertEquals(0, start.compareTo(CurrencyRounder.roundCurrency(start, true)));
	}

	@Test
	void roundCurrency_DoesNotRound_ValidValues() {
		BigDecimal start = new BigDecimal("10.03");
		assertEquals(0, start.compareTo(CurrencyRounder.roundCurrency(start, false)));
		assertEquals(0, start.compareTo(CurrencyRounder.roundCurrency(start, true)));
	}

	@Test
	void roundCurrency_DoesNotRound_ValidDoublesWithSingleDigitPrecision() {
		BigDecimal start1 = new BigDecimal("15.5");
		BigDecimal start2 = new BigDecimal("15.50");
		assertEquals(0, start1.compareTo(CurrencyRounder.roundCurrency(start1, false)));
		assertEquals(0, start1.compareTo(CurrencyRounder.roundCurrency(start1, true)));
		assertEquals(0, start2.compareTo(CurrencyRounder.roundCurrency(start2, false)));
		assertEquals(0, start2.compareTo(CurrencyRounder.roundCurrency(start2, true)));
	}

	@Test
	void roundCurrency_RoundsDown15Point011_To15Point01() {
		BigDecimal start = new BigDecimal("15.011");
		BigDecimal expected = new BigDecimal("15.01");
		assertEquals(0, expected.compareTo(CurrencyRounder.roundCurrency(start, false)));
	}

	@Test
	void roundCurrency_RoundsUp15Point011_To15Point02() {
		BigDecimal start = new BigDecimal("15.011");
		BigDecimal expected = new BigDecimal("15.02");
		assertEquals(0, expected.compareTo(CurrencyRounder.roundCurrency(start, true)));
	}

}
