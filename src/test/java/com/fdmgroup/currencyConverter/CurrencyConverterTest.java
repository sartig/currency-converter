package com.fdmgroup.currencyConverter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CurrencyConverterTest {

	@Mock
	Logger mockLogger;

	CurrencyConverter currencyConverter = CurrencyConverter.getInstance();

	@BeforeEach
	void setUp() throws Exception {
		currencyConverter.setLogger(mockLogger);
	}

	@Test
	void validate_WithSingleInvalidCurrency_ReturnsFalse_AndLogsWarning() {
		assertFalse(currencyConverter.validate("xyz"));
		verify(mockLogger).warn("Currency xyz not valid");
	}

	@Test
	void validate_WithSingleValidCurrency_ReturnsTrue_AndLogsInfo() {
		assertTrue(currencyConverter.validate("gbp"));
		verify(mockLogger).info("Currency list [gbp] validated");
	}

	@Test
	void validate_WithUSD_ReturnsTrue_AndLogsInfo() {
		assertTrue(currencyConverter.validate("usd"));
		verify(mockLogger).info("Currency list [usd] validated");
	}

	@Test
	void validate_WithOneValidAndOneInvalidCurrency_ReturnsFalse_AndLogsWarning() {
		assertFalse(currencyConverter.validate("gbp", "eur", "abc"));
		verify(mockLogger).warn("Currency abc not valid");
	}

	@Test
	void validate_WithTwoValidCurrencies_ReturnsTrue_AndLogsInfo() {
		assertTrue(currencyConverter.validate("gbp", "eur"));
		verify(mockLogger).info("Currency list [gbp, eur] validated");
	}

	@Test
	void convert_100GBPToHKD_ReturnsCorrectValue_AndLogsInfo() {
		double expected = 918.6414814467308; // without rounding
//		double expectedRounded = 918.64; // normal rounding
		double result = currencyConverter.convert("gbp", "hkd", 100);
		verify(mockLogger).info("Conversion of 100.00 gbp to 918.64 hkd"); // applies to both (truncated)
		assertEquals(result, expected);
	}

	@Test
	void convert_100GBPToGBP_Returns100_AndLogsInfo() {
		double result = currencyConverter.convert("gbp", "gbp", 100);
		verify(mockLogger).warn("Trying to convert between identical currencies: gbp");
		assertEquals(result, 100);
	}

}
