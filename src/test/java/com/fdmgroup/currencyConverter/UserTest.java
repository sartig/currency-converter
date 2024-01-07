package com.fdmgroup.currencyConverter;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {

	User user = new User();

	@BeforeEach
	void setUp() throws Exception {
		user.setName("Tester");
	}

	@Test
	void getWallet_ReturnsEmptyMap_WhenCalled_OnNewUser() {
		HashMap<String, BigDecimal> expected = new HashMap<String, BigDecimal>();
		assertEquals(expected, user.getWallet());
	}

	@Test
	void addCurrency_With0HKD_DoesNotUpdateWallet() {
		user.addCurrency("hkd", new BigDecimal("0"));
		HashMap<String, BigDecimal> expected = new HashMap<String, BigDecimal>();
		assertEquals(expected, user.getWallet());
	}

	@Test
	void addCurrency_With10USD_UpdatesWalletWithCorrectValues() {
		user.addCurrency("usd", new BigDecimal("10"));
		HashMap<String, BigDecimal> expected = new HashMap<String, BigDecimal>();
		expected.put("usd", new BigDecimal("10.00"));
		assertEquals(expected, user.getWallet());
	}

	@Test
	void addCurrency_With10USD_InDifferentCases_UpdatesSameKeyValuePairInWallet_WithCorrectValues() {
		user.addCurrency("usd", new BigDecimal("10"));
		user.addCurrency("usd", new BigDecimal("10"));
		user.addCurrency("usd", new BigDecimal("10"));
		HashMap<String, BigDecimal> expected = new HashMap<String, BigDecimal>();
		expected.put("usd", new BigDecimal("30.00"));
		assertEquals(expected, user.getWallet());
	}

	@Test
	void subtractCurrency_ThrowsUserInsufficientBalanceError_WhenSubtractingCurrencyNotInWallet() {
		Exception e = assertThrows(UserInsufficientBalance.class,
				() -> user.subtractCurrency("usd", new BigDecimal("10")));
		String expectedMessage = "User Tester has insufficient balance of usd. Current balance: 0.00, required balance: 10.00.";
		assertEquals(expectedMessage, e.getMessage());
	}

	@Test
	void subtractCurrency_ThrowsUserInsufficientBalanceError_WhenSubtractingMoreCurrencyThanInWallet() {
		user.addCurrency("usd", new BigDecimal("10"));
		Exception e = assertThrows(UserInsufficientBalance.class,
				() -> user.subtractCurrency("usd", new BigDecimal("100")));
		String expectedMessage = "User Tester has insufficient balance of usd. Current balance: 10.00, required balance: 100.00.";
		assertEquals(expectedMessage, e.getMessage());
	}

	@Test
	void subtractCurrency_With30Point0USD_DoesNotThrowError_AndUpdatesWalletwithCorrectValues() {
		user.addCurrency("usd", new BigDecimal("176.98"));
		assertDoesNotThrow(() -> user.subtractCurrency("usd", new BigDecimal("30.00")));
		HashMap<String, BigDecimal> expected = new HashMap<String, BigDecimal>();
		expected.put("usd", new BigDecimal("146.98"));
		assertEquals(expected, user.getWallet());
	}

	@Test
	void subtractCurrency_With10USD_InDifferentCases_DoesNotThrowError_AndUpdatesSameKeyValuePairInWallet_WithCorrectValues() {
		user.addCurrency("usd", new BigDecimal("30"));
		assertDoesNotThrow(() -> user.subtractCurrency("usd", new BigDecimal("5")));
		assertDoesNotThrow(() -> user.subtractCurrency("usd", new BigDecimal("5")));
		assertDoesNotThrow(() -> user.subtractCurrency("usd", new BigDecimal("5")));
		assertDoesNotThrow(() -> user.subtractCurrency("usd", new BigDecimal("5")));
		HashMap<String, BigDecimal> expected = new HashMap<String, BigDecimal>();
		expected.put("usd", new BigDecimal("10.00"));
		assertEquals(expected, user.getWallet());
	}

	@Test
	void subtractCurrency_WithSameAmountAsInWallet_DoesNotThrowError_AndRemovesEntryFromWallet() {
		user.addCurrency("usd", new BigDecimal("30"));
		assertDoesNotThrow(() -> user.subtractCurrency("usd", new BigDecimal("30")));
		HashMap<String, BigDecimal> expected = new HashMap<String, BigDecimal>();
		assertEquals(expected, user.getWallet());
	}

}
