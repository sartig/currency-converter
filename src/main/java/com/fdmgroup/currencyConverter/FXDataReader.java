package com.fdmgroup.currencyConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 */
public class FXDataReader {
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static Logger logger = LogManager.getLogger(FXDataReader.class);
	private static final String fileSeparator = FileSystems.getDefault().getSeparator();

	/**
	 * Used with Mockito to verify logging
	 * @param logger Mock logger
	 */
	public static void setLogger(Logger logger) {
		FXDataReader.logger = logger;
	}
	
	/**
	 * Loads JSON containing currency data. Expects a JSON formatted as {@code HashMap<String, Currency}.
	 * @param filePath Path to JSON file
	 * @return Returns null if there are issues reading the JSON, otherwise returns the parsed file.
	 */
	public static HashMap<String, Currency> loadJsonFromFilepath(String filePath) {
		String properPath = filePath.replace("/", fileSeparator);
		File file = new File(properPath);

		HashMap<String, Currency> fxData = null;
		try {
			fxData = objectMapper.readValue(file, new TypeReference<HashMap<String, Currency>>() {
			});
		} catch (DatabindException dbe) {
			logger.error("Unable to parse JSON file data at path " + properPath + " to HashMap<String, Currency>");
			return null;
		} catch (IOException ioe) {
			logger.error("Unable to read JSON file at path " + properPath);
			return null;
		}

		if (fxData.isEmpty()) {
			logger.warn("Parsed FX JSON file at path " + properPath + " as empty");
		}
		
		logger.info("Successfully parsed JSON file data at path " + properPath + ": " + fxData.size() + " items");

		return fxData;
	}
}
