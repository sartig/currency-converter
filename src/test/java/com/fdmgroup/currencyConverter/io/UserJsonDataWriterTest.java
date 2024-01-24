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
	void write_WithInvalidFilePath_LogsError() throws IOException {
		String filePath = "src/test/resources/invalidFileName?.json";
		String properFilePath = filePath.replace("/", FileSystems.getDefault().getSeparator());
		userJsonDataWriter.writeDataToFilePath(properFilePath, users);
		verify(logger).error("Unable to write User data to " + properFilePath);
	}
	
	@Test
	void write_WithEmptyArrayList_CreatesEmptyFile() throws IOException {
		String filePath = "src/test/resources/writeEmptyUsers.json";
		String properFilePath = filePath.replace("/", FileSystems.getDefault().getSeparator());
		userJsonDataWriter.writeDataToFilePath(properFilePath, users);
		File file = new File(properFilePath);
		assertTrue(file.exists());
		ArrayList<User> readUser = objectMapper.readValue(file, new TypeReference<ArrayList<User>>() {
		});
		assertEquals(users, readUser);
		verify(logger).info("Successfully wrote User data to " + properFilePath);
	}

	@Test
	void write_WithOneUserWithEmptyWalletInArrayList_CreatesCorrectFile() throws IOException {
		User user = new User("A", new HashMap<String, Double>());
		users.add(user);
		String filePath = "src/test/resources/writeSingleUser.json";
		String properFilePath = filePath.replace("/", FileSystems.getDefault().getSeparator());

		userJsonDataWriter.writeDataToFilePath(properFilePath, users);
		File file = new File(properFilePath);
		assertTrue(file.exists());
		ArrayList<User> readUser = objectMapper.readValue(file, new TypeReference<ArrayList<User>>() {
		});
		assertEquals(1, readUser.size());
		assertEquals("A", readUser.get(0).getName());
		assertTrue(readUser.get(0).getWallet().isEmpty());
		verify(logger).info("Successfully wrote User data to " + properFilePath);

	}

	@Test
	void write_OnExistingFilePath_OverwritesFileData() throws IOException {
		User user = new User("A", new HashMap<String, Double>());
		users.add(user);
		String filePath = "src/test/resources/overwriteExistingUser.json";
		String properFilePath = filePath.replace("/", FileSystems.getDefault().getSeparator());
		File file = new File(properFilePath);
		ArrayList<String> fillerData = new ArrayList<String>();
		fillerData.add("One");
		fillerData.add("Two");
		objectMapper.writeValue(file, fillerData);

		ArrayList<String> readUser = objectMapper.readValue(file, new TypeReference<ArrayList<String>>() {
		});
		assertEquals(2, readUser.size());

		userJsonDataWriter.writeDataToFilePath(properFilePath, users);

		ArrayList<User> readUser1 = objectMapper.readValue(file, new TypeReference<ArrayList<User>>() {
		});
		assertEquals(1, readUser1.size());
		assertEquals("A", readUser1.get(0).getName());
		verify(logger).info("Successfully wrote User data to " + properFilePath);

	}

}
