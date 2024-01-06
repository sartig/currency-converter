package com.fdmgroup.currencyConverter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import java.nio.file.FileSystems;
import java.util.ArrayList;

import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserJsonDataReaderTest {

	@Mock
	Logger mockLogger;

	UserJsonDataReader userJsonDataReader = new UserJsonDataReader();

	@BeforeEach
	void setUp() throws Exception {
		userJsonDataReader.setLogger(mockLogger);
	}

	@Test
	void loadData_WithInvalidFilePath_ReturnsNull_AndLogsError() {
		ArrayList<User> result = userJsonDataReader.loadDataFromFilePath("invalidpath.file");
		verify(mockLogger).error("Unable to read JSON file at path invalidpath.file");
		assertEquals(null, result);
	}

	@Test
	void loadData_WithFilePathToEmptyJson_ReturnsEmptyArraylist_AndLogsWarning() {
		String filePath = "src/test/resources/users_empty.json";
		ArrayList<User> result = userJsonDataReader.loadDataFromFilePath(filePath);
		String properPath = filePath.replace("/", FileSystems.getDefault().getSeparator());
		verify(mockLogger).warn("Parsed User JSON file at path " + properPath + " as empty");
		ArrayList<User> expected = new ArrayList<User>();
		assertEquals(expected, result);
	}

	@Test
	void loadData_WithFilePathToJsonWithIncorrectFormat_ReturnsNull_AndLogsError() {
		String filePath = "src/test/resources/fx_empty.json";
		ArrayList<User> result = userJsonDataReader.loadDataFromFilePath(filePath);
		String properPath = filePath.replace("/", FileSystems.getDefault().getSeparator());
		verify(mockLogger).error("Unable to parse JSON file data at path " + properPath + " to ArrayList<User>");
		assertEquals(null, result);

	}

	@Test
	void loadData_WithFilePathToJsonWithTwoCurrencies_ReturnsCorrectArrayList_AndLogsInfo() {
		String filePath = "src/test/resources/users_two.json";
		ArrayList<User> result = userJsonDataReader.loadDataFromFilePath(filePath);
		String properPath = filePath.replace("/", FileSystems.getDefault().getSeparator());
		verify(mockLogger).info("Successfully parsed JSON file data at path " + properPath + ": 2 items");
		assertEquals(2, result.size());
		assertEquals("One", result.get(0).getName());
		assertEquals(508, result.get(1).getWallet().get("cad"));
	}

}
