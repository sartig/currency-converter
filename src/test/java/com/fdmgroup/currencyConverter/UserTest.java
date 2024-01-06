package com.fdmgroup.currencyConverter;

import static org.junit.jupiter.api.Assertions.*;

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
		HashMap<String, Double> expected = new HashMap<String, Double>();
		assertEquals(expected, user.getWallet());
	}

	@Test
	void addCurrency_With0HKD_DoesNotUpdateWallet() {
		user.addCurrency("hkd", 0);
		HashMap<String, Double> expected = new HashMap<String, Double>();
		assertEquals(expected, user.getWallet());
	}

	@Test
	void addCurrency_With10USD_UpdatesWalletWithCorrectValues() {
		user.addCurrency("usd", 10);
		HashMap<String, Double> expected = new HashMap<String, Double>();
		expected.put("usd", 10.0);
		assertEquals(expected, user.getWallet());
	}

	@Test
	void addCurrency_With10USDMultipleTimes_UpdatesWalletWithCorrectValues() {
		user.addCurrency("usd", 10);
		user.addCurrency("usd", 10);
		user.addCurrency("usd", 10);
	}

	@Test
	void addCurrency_With10USD_InDifferentCases_UpdatesSameKeyValuePairInWallet_WithCorrectValues() {
		user.addCurrency("usd", 10);
		user.addCurrency("Usd", 10);
		user.addCurrency("USD", 10);
		HashMap<String, Double> expected = new HashMap<String, Double>();
		expected.put("usd", 30.0);
		assertEquals(expected, user.getWallet());
	}

	@Test
	void subtractCurrency_ThrowsUserInsufficientBalanceError_WhenSubtractingCurrencyNotInWallet() {
		Exception e = assertThrows(UserInsufficientBalance.class, () -> user.subtractCurrency("usd", 10));
		String expectedMessage = "User Tester has insufficient balance of usd. Current balance: 0.00, required balance: 10.00.";
		assertEquals(expectedMessage, e.getMessage());
	}

	@Test
	void subtractCurrency_ThrowsUserInsufficientBalanceError_WhenSubtractingMoreCurrencyThanInWallet() {
		user.addCurrency("usd", 10);
		Exception e = assertThrows(UserInsufficientBalance.class, () -> user.subtractCurrency("usd", 100));
		String expectedMessage = "User Tester has insufficient balance of usd. Current balance: 10.00, required balance: 100.00.";
		assertEquals(expectedMessage, e.getMessage());
	}

	@Test
	void subtractCurrency_With10USD_DoesNotThrowError_AndUpdatesWalletwithCorrectValues() {
		user.addCurrency("usd", 30);
		assertDoesNotThrow(() -> user.subtractCurrency("usd", 10));
		HashMap<String, Double> expected = new HashMap<String, Double>();
		expected.put("usd", 20.0);
		assertEquals(expected, user.getWallet());
	}

	@Test
	void subtractCurrency_With10USD_InDifferentCases_DoesNotThrowError_AndUpdatesSameKeyValuePairInWallet_WithCorrectValues() {
		user.addCurrency("usd", 30);
		assertDoesNotThrow(() -> user.subtractCurrency("usd", 5));
		assertDoesNotThrow(() -> user.subtractCurrency("USD", 5));
		assertDoesNotThrow(() -> user.subtractCurrency("Usd", 5));
		assertDoesNotThrow(() -> user.subtractCurrency("usD", 5));
		HashMap<String, Double> expected = new HashMap<String, Double>();
		expected.put("usd", 10.0);
		assertEquals(expected, user.getWallet());
	}

	@Test
	void subtractCurrency_WithSameAmountAsInWallet_DoesNotThrowError_AndRemovesEntryFromWallet() {
		user.addCurrency("usd", 30);
		assertDoesNotThrow(() -> user.subtractCurrency("usd", 30));
		HashMap<String, Double> expected = new HashMap<String, Double>();
		assertEquals(expected, user.getWallet());
	}

}
