package com.fdmgroup.currencyConverter.currency;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

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
	void convert_WithInvalidStartCurrency_Returns0_AndLogsError() {
		BigDecimal result = currencyConverter.convert("abc", "usd", new BigDecimal("10"));
		verify(mockLogger).error("Trying to convert invalid currency: abc");
		assertEquals(0, result.compareTo(BigDecimal.ZERO));
	}
	
	@Test
	void convert_WithInvalidEndCurrency_Returns0_AndLogsError() {
		BigDecimal result = currencyConverter.convert("usd", "abc", new BigDecimal("10"));
		verify(mockLogger).error("Trying to convert invalid currency: abc");
		assertEquals(0, result.compareTo(BigDecimal.ZERO));
	}

	@Test
	void convert_ZeroUsdToHKD_Returns0_AndLogsWarning_ButStillLogsInfo() {
		BigDecimal result = currencyConverter.convert("usd", "hkd", new BigDecimal("0"));
		verify(mockLogger).warn("Trying to convert zero usd to hkd - method call unnecessary");
		verify(mockLogger).info("Conversion of 0.00 usd to 0.00 hkd");
		assertEquals(0, result.compareTo(BigDecimal.ZERO));
	}

	@Test
	void convert_100Point009GBPToHKD_RoundsAmountToConvertDown_AndLogsWarning_AndLogsInfoWith100Instead() {
		BigDecimal expected = new BigDecimal("918.64"); // rounded from 918.641...
		BigDecimal result = currencyConverter.convert("gbp", "hkd", new BigDecimal("100.009"));
		verify(mockLogger).info("Conversion of 100.00 gbp to 918.64 hkd");
		assertEquals(0, result.compareTo(expected));
	}

	@Test
	void convert_100GBPToHKD_ReturnsTruncatedValue_AndLogsInfo() {
		BigDecimal expected = new BigDecimal("918.64"); // rounded from 918.641...
		BigDecimal result = currencyConverter.convert("gbp", "hkd", new BigDecimal("100"));
		verify(mockLogger).info("Conversion of 100.00 gbp to 918.64 hkd");
		assertEquals(0, result.compareTo(expected));
	}

	@Test
	void convert_1USDToEUR_ReturnsRoundedDownValue_AndLogsInfo() {
		BigDecimal expected = new BigDecimal("0.98"); // rounded down from 0.985...
		BigDecimal result = currencyConverter.convert("usd", "eur", new BigDecimal("1"));
		verify(mockLogger).info("Conversion of 1.00 usd to 0.98 eur");
		assertEquals(0, result.compareTo(expected));
	}

	@Test
	void convert_100GBPToGBP_Returns100_AndLogsWarning() {
		BigDecimal result = currencyConverter.convert("gbp", "gbp", new BigDecimal("100"));
		verify(mockLogger).warn("Trying to convert between identical currencies: gbp");
		assertEquals(0, result.compareTo(new BigDecimal("100")));
	}

}
