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
class FXDataReaderTest {

	@Mock
	Logger mockLogger;

	@BeforeEach
	void setUp() throws Exception {
		FXDataReader.setLogger(mockLogger);
	}

	@Test
	void loadJson_WithInvalidFilePath_ReturnsNull_AndLogsError() {
		HashMap<String, Currency> result = FXDataReader.loadJsonFromFilepath("invalidpath.file");
		verify(mockLogger).error("Unable to read JSON file at path invalidpath.file");
		assertEquals(null, result);
	}

	@Test
	void loadJson_WithFilePathToEmptyJson_ReturnsEmptyHashMap_AndLogsWarning() {
		String filePath = "src/test/resources/fx_rates_empty.json";
		HashMap<String, Currency> result = FXDataReader.loadJsonFromFilepath(filePath);
		String properPath = filePath.replace("/", FileSystems.getDefault().getSeparator());
		verify(mockLogger).warn("Parsed FX JSON file at path " + properPath + " as empty");
		HashMap<String, Currency> expected = new HashMap<String, Currency>();
		assertEquals(expected, result);
	}

	@Test
	void loadJson_WithFilePathToJsonWithIncorrectFormat_ReturnsNull_AndLogsError() {
		String filePath = "src/test/resources/fx_rates_wrong.json";
		HashMap<String, Currency> result = FXDataReader.loadJsonFromFilepath(filePath);
		String properPath = filePath.replace("/", FileSystems.getDefault().getSeparator());
		verify(mockLogger)
				.error("Unable to parse JSON file data at path " + properPath + " to HashMap<String, Currency>");
		assertEquals(null, result);

	}

	@Test
	void loadJson_WithFilePathToJsonWithTwoCurrencies_ReturnsHashMapOfSizeTwo_AndLogsInfo() {
		String filePath = "src/test/resources/fx_rates_two.json";
		HashMap<String, Currency> result = FXDataReader.loadJsonFromFilepath(filePath);
		String properPath = filePath.replace("/", FileSystems.getDefault().getSeparator());
		verify(mockLogger).info("Successfully parsed JSON file data at path " + properPath + ": 2 items");
		assertEquals(2, result.size());
		assertTrue(result.containsKey("eur"));
	}

}
