package com.fdmgroup.currencyConverter.io;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdmgroup.currencyConverter.user.User;

@ExtendWith(MockitoExtension.class)
class UserJsonDataWriterTest {
	@Mock
	private Logger logger;
	
	private ObjectMapper objectMapper;
	private UserJsonDataWriter userJsonDataWriter;
	private ArrayList<User> users;

	@BeforeEach
	void setUp() throws Exception {
		objectMapper = new ObjectMapper();
		userJsonDataWriter = new UserJsonDataWriter();
		userJsonDataWriter.setLogger(logger);
		users = new ArrayList<User>();
	}

	@Test
	void write_WithEmptyArrayList_CreatesEmptyFile() {
		String filePath = "src/test/resources/writeEmptyUsers.json";
		String properFilePath = filePath.replace("/", FileSystems.getDefault().getSeparator());
		userJsonDataWriter.writeDataToFilePath(properFilePath, users);
		File file = new File(properFilePath);
		assertTrue(file.exists());
		try {
			ArrayList<User> readUser = objectMapper.readValue(file, new TypeReference<ArrayList<User>>() {
			});
			assertEquals(users, readUser);
			verify(logger).info("Successfully wrote User data to " +  properFilePath);
		} catch (IOException e) {
			fail("IOException");
		}
	}

	@Test
	void write_WithOneUserWithEmptyWalletInArrayList_CreatesCorrectFile() {
		User user = new User("A", new HashMap<String, Double>());
		users.add(user);
		String filePath = "src/test/resources/writeSingleUser.json";
		String properFilePath = filePath.replace("/", FileSystems.getDefault().getSeparator());
		
		userJsonDataWriter.writeDataToFilePath(properFilePath, users);
		File file = new File(properFilePath);
		assertTrue(file.exists());
		try {
			ArrayList<User> readUser = objectMapper.readValue(file, new TypeReference<ArrayList<User>>() {
			});
			assertEquals(1, readUser.size());
			assertEquals("A", readUser.get(0).getName());
			assertTrue(readUser.get(0).getWallet().isEmpty());
			verify(logger).info("Successfully wrote User data to " +  properFilePath);
		} catch (IOException e) {
			fail("IOException");
		}
	}

	@Test
	void write_OnExistingFilePath_OverwritesFileData() {
		User user = new User("A", new HashMap<String, Double>());
		users.add(user);
		String filePath = "src/test/resources/overwriteExistingUser.json";
		String properFilePath = filePath.replace("/", FileSystems.getDefault().getSeparator());
		File file = new File(properFilePath);
		try {
			ArrayList<String> fillerData = new ArrayList<String>();
			fillerData.add("One");
			fillerData.add("Two");
			objectMapper.writeValue(file, fillerData);
		} catch (IOException e) {
			fail("initial setup IOException");
		}
		try {
			ArrayList<String> readUser = objectMapper.readValue(file, new TypeReference<ArrayList<String>>() {
			});
			assertEquals(2, readUser.size());
		} catch (IOException e) {
			fail("initial verification IOException");
		}
		userJsonDataWriter.writeDataToFilePath(properFilePath, users);
		try {
			ArrayList<User> readUser = objectMapper.readValue(file, new TypeReference<ArrayList<User>>() {
			});
			assertEquals(1, readUser.size());
			assertEquals("A", readUser.get(0).getName());
			verify(logger).info("Successfully wrote User data to " +  properFilePath);
		} catch (IOException e) {
			fail("verification IOException");
		}

	}

}
