package com.fdmgroup.currencyConverter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CurrencyRounderTest {

	@Test
	void roundCurrency_DoesNotRound_IntegerValues() {
		assertEquals(10, CurrencyRounder.roundCurrency(10, false));
		assertEquals(10, CurrencyRounder.roundCurrency(10, true));
	}
	
	@Test
	void roundCurrency_DoesNotRound_ValidDoubles() {
		assertEquals(15.03, CurrencyRounder.roundCurrency(15.03, false));
		assertEquals(15.03, CurrencyRounder.roundCurrency(15.03, true));
	}
	
	@Test
	void roundCurrency_RoundsDown15Point011_To15Point01() {
		assertEquals(15.01, CurrencyRounder.roundCurrency(15.011, false));
	}
	
	@Test
	void roundCurrency_RoundsUp15Point011_To15Point02() {
		assertEquals(15.02, CurrencyRounder.roundCurrency(15.011, true));
	}

}
