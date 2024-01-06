package com.fdmgroup.currencyConverter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserManagerTest {

	@Mock
	User mockUser1, mockUser2,mockUser3;
	
	UserManager userManager = UserManager.getInstance();
	

	@BeforeEach
	void setUp() throws Exception {
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
	
	// execute transaction

}
