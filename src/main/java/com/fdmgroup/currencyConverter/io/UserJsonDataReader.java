package com.fdmgroup.currencyConverter.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.StandardLevel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fdmgroup.currencyConverter.user.User;

/**
 * Class for specifically loading User JSON file, formatted as a
 * {@code ArrayList<User>}
 * 
 * @see com.fdmgroup.currencyConverter.user.User
 */
public class UserJsonDataReader extends JsonDataReader<ArrayList<User>> {
	private static Logger logger = LogManager.getLogger(UserJsonDataReader.class);

	/**
	 * Used with Mockito to verify logging
	 * 
	 * @param logger Mock logger
	 */
	@Override
	public void setLogger(Logger logger) {
		UserJsonDataReader.logger = logger;
	}

	/**
	 * Utility method for logging messages at various levels. Currently implemented
	 * levels are {@code INFO, WARN, ERROR}.
	 */
	@Override
	void log(String message, StandardLevel level) {
		switch (level) {
		case INFO:
			logger.info(message);
			break;
		case WARN:
			logger.warn(message);
			break;
		case ERROR:
			logger.error(message);
			break;
		default:
			break;
		}
	}
	
	/**
	 * Loads JSON containing data. Expects a JSON formatted as a
	 * {@code ArrayList<User>}.
	 * 
	 * @param filePath Path to JSON file
	 * @return Returns null if there are issues reading the JSON, otherwise returns
	 *         the parsed file.
	 */
	@Override
	public ArrayList<User> loadDataFromFilePath(String filePath) {
		String properPath = filePath.replace("/", fileSeparator);
		File file = new File(properPath);

		ArrayList<User> data;
		try {
			data = objectMapper.readValue(file, new TypeReference<>() {
            });
		} catch (DatabindException dbe) {

			log("Unable to parse JSON file data at path " + properPath + " to ArrayList<User>", StandardLevel.ERROR);
			return null;
		} catch (IOException ioe) {
			log("Unable to read JSON file at path " + properPath, StandardLevel.ERROR);
			return null;
		}

		if (data.isEmpty()) {
			log("Parsed User JSON file at path " + properPath + " as empty", StandardLevel.WARN);
		}

		String successMessage = "Successfully parsed JSON file data at path " + properPath + ": " + data.size()
				+ " items";

		log(successMessage, StandardLevel.INFO);

		return data;
	}
}
