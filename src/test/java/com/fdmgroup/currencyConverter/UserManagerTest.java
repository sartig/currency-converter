package com.fdmgroup.currencyConverter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserManagerTest {

	@Mock
	User mockUser1, mockUser2, mockUser3;

	@Mock
	Logger mockLogger;

	UserManager userManager = UserManager.getInstance();

	Transaction invalidNameTransaction = new Transaction("D", "usd", "abc", 10);
	Transaction insufficientBalanceTransaction = new Transaction("C", "usd", "gbp", 10);
	Transaction validTransaction = new Transaction("C", "hkd", "gbp", 10);

	@BeforeEach
	void setUp() throws Exception {
		userManager.setLogger(mockLogger);
		ArrayList<User> users = new ArrayList<User>();
		users.add(mockUser1);
		users.add(mockUser2);
		users.add(mockUser3);
		userManager.setUserData(users);
		when(mockUser1.getName()).thenReturn("A");
		when(mockUser2.getName()).thenReturn("B");
		when(mockUser3.getName()).thenReturn("C");
	}

	@Test
	void validateUser_WithInvalidName_ReturnsFalse() {
		assertFalse(userManager.validateUser("D"));
	}

	@Test
	void validateUser_WithValidName_ReturnsFalse() {
		assertTrue(userManager.validateUser("C"));
	}

	@Test
	void getUserBalance_WithInvalidName_ReturnsZero() {
		assertEquals(0, userManager.getUserBalance("D", "usd"));
	}

	@Test
	void getUserBalance_WithValidName_WithNullBalance_ReturnsZero() {
		when(mockUser3.getWallet()).thenReturn(new HashMap<String, Double>());
		assertEquals(0, userManager.getUserBalance("C", "usd"));
	}

	@Test
	void getUserBalance_WithValidName_WithExistingBalance_ReturnsCorrectBalance() {
		HashMap<String, Double> wallet = new HashMap<String, Double>();
		wallet.put("usd", 12.0);
		when(mockUser3.getWallet()).thenReturn(wallet);
		assertEquals(12, userManager.getUserBalance("C", "usd"));
	}

	@Test
	void executeTransaction_WithInvalidName_DoesNotCallSubtractOrAddCurrency_OnAnyUsers() {
		userManager.executeTransaction(invalidNameTransaction);
		verify(mockLogger).warn("Transaction for user D: 10.00 usd to abc failed due to user not existing in database");
		verify(mockUser1, never()).addCurrency(anyString(), anyDouble());
		verify(mockUser2, never()).addCurrency(anyString(), anyDouble());
		verify(mockUser3, never()).addCurrency(anyString(), anyDouble());
		assertDoesNotThrow(() -> verify(mockUser1, never()).subtractCurrency(anyString(), anyDouble()));
		assertDoesNotThrow(() -> verify(mockUser2, never()).subtractCurrency(anyString(), anyDouble()));
		assertDoesNotThrow(() -> verify(mockUser3, never()).subtractCurrency(anyString(), anyDouble()));
	}

	@Test
	void executeTransaction_WithInsufficientWallet_DoesNotCallAddCurrencyOnUser_AndLogsError_FromUserException() {
		try {
			doThrow(new UserInsufficientBalance("mock error")).when(mockUser3).subtractCurrency("usd", 10);
			userManager.executeTransaction(insufficientBalanceTransaction);
			verify(mockLogger).error("mock error");
			verify(mockUser1, never()).addCurrency(anyString(), anyDouble());
			verify(mockUser2, never()).addCurrency(anyString(), anyDouble());
			verify(mockUser3, never()).addCurrency(anyString(), anyDouble());
			assertDoesNotThrow(() -> verify(mockUser1, never()).subtractCurrency(anyString(), anyDouble()));
			assertDoesNotThrow(() -> verify(mockUser2, never()).subtractCurrency(anyString(), anyDouble()));
		} catch (UserInsufficientBalance e) {
			fail("error thrown");
		}
	}

	@Test
	void executeTransaction_WithValidTransaction_CallsSubtractCurrency_AndAddCurrency_OnUser() {
		userManager.executeTransaction(validTransaction);
		assertDoesNotThrow(() -> verify(mockUser1, never()).subtractCurrency(anyString(), anyDouble()));
		assertDoesNotThrow(() -> verify(mockUser2, never()).subtractCurrency(anyString(), anyDouble()));
		assertDoesNotThrow(() -> verify(mockUser3).subtractCurrency("hkd", 10));
		verify(mockUser1, never()).addCurrency(anyString(), anyDouble());
		verify(mockUser2, never()).addCurrency(anyString(), anyDouble());
		verify(mockUser3).addCurrency("gbp", 1.08);
	}

}
