package com.fdmgroup.currencyConverter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.file.FileSystems;
import java.util.HashMap;

import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FXJsonDataReaderTest {

	@Mock
	Logger mockLogger;

	FXJsonDataReader fxJsonDataReader = new FXJsonDataReader();

	@BeforeEach
	void setUp() throws Exception {
		fxJsonDataReader.setLogger(mockLogger);
	}

	@Test
	void loadData_WithInvalidFilePath_ReturnsNull_AndLogsError() {
		HashMap<String, Currency> result = fxJsonDataReader.loadDataFromFilePath("invalidpath.file");
		verify(mockLogger).error("Unable to read JSON file at path invalidpath.file");
		assertEquals(null, result);
	}

	@Test
	void loadData_WithFilePathToEmptyJson_ReturnsEmptyHashMap_AndLogsWarning() {
		String filePath = "src/test/resources/fx_empty.json";
		HashMap<String, Currency> result = fxJsonDataReader.loadDataFromFilePath(filePath);
		String properPath = filePath.replace("/", FileSystems.getDefault().getSeparator());
		verify(mockLogger).warn("Parsed FX JSON file at path " + properPath + " as empty");
		HashMap<String, Currency> expected = new HashMap<String, Currency>();
		assertEquals(expected, result);
	}

	@Test
	void loadData_WithFilePathToJsonWithIncorrectFormat_ReturnsNull_AndLogsError() {
		String filePath = "src/test/resources/users_empty.json";
		HashMap<String, Currency> result = fxJsonDataReader.loadDataFromFilePath(filePath);
		String properPath = filePath.replace("/", FileSystems.getDefault().getSeparator());
		verify(mockLogger)
				.error("Unable to parse JSON file data at path " + properPath + " to HashMap<String, Currency>");
		assertEquals(null, result);

	}

	@Test
	void loadData_WithFilePathToJsonWithTwoCurrencies_ReturnsCorrectHashMap_AndLogsInfo() {
		String filePath = "src/test/resources/fx_rates_two.json";
		HashMap<String, Currency> result = fxJsonDataReader.loadDataFromFilePath(filePath);
		String properPath = filePath.replace("/", FileSystems.getDefault().getSeparator());
		verify(mockLogger).info("Successfully parsed JSON file data at path " + properPath + ": 2 items");
		assertEquals(2, result.size());
		assertTrue(result.containsKey("eur"));
		assertTrue(result.containsKey("gbp"));
		assertEquals("Euro", result.get("eur").getName());
	}

}
