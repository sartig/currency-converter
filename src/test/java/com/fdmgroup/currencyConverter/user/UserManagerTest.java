package com.fdmgroup.currencyConverter.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fdmgroup.currencyConverter.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
class UserManagerTest {

	@Mock
	User mockUser1, mockUser2, mockUser3;

	@Mock
	Logger mockLogger;

	UserManager userManager = UserManager.getInstance();

	Transaction invalidNameTransaction = new Transaction("D", "usd", "abc", new BigDecimal("10.00"));
	Transaction insufficientBalanceTransaction = new Transaction("C", "usd", "gbp", new BigDecimal("10.00"));
	Transaction validTransaction = new Transaction("C", "hkd", "gbp", new BigDecimal("10.00"));
	Transaction zeroValueTransaction = new Transaction("D", "hkd", "nzd", BigDecimal.ZERO);
	Transaction duplicatedCurrencyTransaction = new Transaction("F", "hkd", "hkd", new BigDecimal("5.00"));

	@BeforeEach
	void setUp() throws Exception {
		userManager.setLogger(mockLogger);
		ArrayList<User> users = new ArrayList<User>();
		users.add(mockUser1);
		users.add(mockUser2);
		users.add(mockUser3);
		userManager.setUserData(users);
	}

	void setupNames() {
		when(mockUser1.getName()).thenReturn("A");
		when(mockUser2.getName()).thenReturn("B");
		when(mockUser3.getName()).thenReturn("C");

	}

	@Test
	void validateUser_WithInvalidName_ReturnsFalse() {
		setupNames();
		assertFalse(userManager.validateUser("D"));
	}

	@Test
	void validateUser_WithValidName_ReturnsFalse() {
		setupNames();
		assertTrue(userManager.validateUser("C"));
	}

	@Test
	void getUserBalance_WithInvalidName_ReturnsZero() {
		setupNames();
		assertEquals(0, userManager.getUserBalance("D", "usd").compareTo(BigDecimal.ZERO));
	}

	@Test
	void getUserBalance_WithValidName_WithNullBalance_ReturnsZero() {
		setupNames();
		when(mockUser3.getWallet()).thenReturn(new HashMap<String, Double>());
		assertEquals(0, userManager.getUserBalance("C", "usd").compareTo(BigDecimal.ZERO));
	}

	@Test
	void getUserBalance_WithValidName_WithExistingBalance_ReturnsCorrectBalance() {
		setupNames();
		HashMap<String, Double> wallet = new HashMap<String, Double>();
		wallet.put("usd", 12.0);
		when(mockUser3.getWallet()).thenReturn(wallet);
		assertEquals(0, userManager.getUserBalance("C", "usd").compareTo(new BigDecimal("12.00")));
	}

	@Test
	void executeTransaction_WithInvalidName_DoesNotCallSubtractOrAddCurrency_OnAnyUsers() {
		setupNames();
		userManager.executeTransaction(invalidNameTransaction);
		verify(mockLogger).warn("Transaction for user D: 10.00 usd to abc failed due to user not existing in database");
		verify(mockUser1, never()).addCurrency(anyString(), any());
		verify(mockUser2, never()).addCurrency(anyString(), any());
		verify(mockUser3, never()).addCurrency(anyString(), any());
		assertDoesNotThrow(() -> verify(mockUser1, never()).subtractCurrency(anyString(), any()));
		assertDoesNotThrow(() -> verify(mockUser2, never()).subtractCurrency(anyString(), any()));
		assertDoesNotThrow(() -> verify(mockUser3, never()).subtractCurrency(anyString(), any()));
	}

	@Test
	void executeTransaction_WithZeroAmount_DoesNotCallSubtractOrAddCurrency_OnAnyUsers() {
		userManager.executeTransaction(zeroValueTransaction);
		verify(mockUser1, never()).addCurrency(anyString(), any());
		verify(mockUser2, never()).addCurrency(anyString(), any());
		verify(mockUser3, never()).addCurrency(anyString(), any());
		assertDoesNotThrow(() -> verify(mockUser1, never()).subtractCurrency(anyString(), any()));
		assertDoesNotThrow(() -> verify(mockUser2, never()).subtractCurrency(anyString(), any()));
		assertDoesNotThrow(() -> verify(mockUser3, never()).subtractCurrency(anyString(), any()));
	}

	@Test
	void executeTransaction_WithDuplicatedCurreny_DoesNotCallSubtractOrAddCurrency_OnAnyUsers() {
		userManager.executeTransaction(duplicatedCurrencyTransaction);
		verify(mockUser1, never()).addCurrency(anyString(), any());
		verify(mockUser2, never()).addCurrency(anyString(), any());
		verify(mockUser3, never()).addCurrency(anyString(), any());
		assertDoesNotThrow(() -> verify(mockUser1, never()).subtractCurrency(anyString(), any()));
		assertDoesNotThrow(() -> verify(mockUser2, never()).subtractCurrency(anyString(), any()));
		assertDoesNotThrow(() -> verify(mockUser3, never()).subtractCurrency(anyString(), any()));
	}

	@Test
	void executeTransaction_WithInsufficientWallet_DoesNotCallAddCurrencyOnUser_AndLogsError_FromUserException()
			throws UserInsufficientBalance {
		setupNames();
		doThrow(new UserInsufficientBalance("mock error")).when(mockUser3).subtractCurrency("usd",
				new BigDecimal("10.00"));
		userManager.executeTransaction(insufficientBalanceTransaction);
		verify(mockLogger).error("mock error");
		verify(mockUser1, never()).addCurrency(anyString(), any());
		verify(mockUser2, never()).addCurrency(anyString(), any());
		verify(mockUser3, never()).addCurrency(anyString(), any());
		assertDoesNotThrow(() -> verify(mockUser1, never()).subtractCurrency(anyString(), any()));
		assertDoesNotThrow(() -> verify(mockUser2, never()).subtractCurrency(anyString(), any()));

	}

	@Test
	void executeTransaction_WithValidTransaction_CallsSubtractCurrency_AndAddCurrency_OnUser() {
		setupNames();
		HashMap<String, Double> mockWallet = new HashMap<String, Double>();
		mockWallet.put("hkd", 15.0);
		mockWallet.put("gbp", 1.08);
		when(mockUser3.getWallet()).thenReturn(mockWallet);
		userManager.executeTransaction(validTransaction);
		assertDoesNotThrow(() -> verify(mockUser1, never()).subtractCurrency(anyString(), any()));
		assertDoesNotThrow(() -> verify(mockUser2, never()).subtractCurrency(anyString(), any()));
		assertDoesNotThrow(() -> verify(mockUser3).subtractCurrency("hkd", new BigDecimal("10.00")));
		verify(mockUser1, never()).addCurrency(anyString(), any());
		verify(mockUser2, never()).addCurrency(anyString(), any());
		verify(mockUser3).addCurrency("gbp", new BigDecimal("1.08"));
		verify(mockLogger).info("User C: converted 10.00 hkd to 1.08 gbp");
		verify(mockLogger).info("Updated user C balance: 15.00 hkd, 1.08 gbp");
	}

}
