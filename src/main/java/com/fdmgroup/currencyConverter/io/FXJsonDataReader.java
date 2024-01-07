package com.fdmgroup.currencyConverter.io;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.StandardLevel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fdmgroup.currencyConverter.currency.Currency;

/**
 * Class for specifically loading FX_rates JSON file, formatted as a
 * {@code HashMap<String, Currency>}
 * 
 * @see com.fdmgroup.currencyConverter.currency.Currency
 */
public class FXJsonDataReader extends JsonDataReader<HashMap<String, Currency>> {
	private static Logger logger = LogManager.getLogger(FXJsonDataReader.class);

	/**
	 * Used with Mockito to verify logging
	 * 
	 * @param logger Mock logger
	 */
	public void setLogger(Logger logger) {
		FXJsonDataReader.logger = logger;
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
	 * {@code HashMap<String, Currency>}.
	 * 
	 * @param filePath Path to JSON file
	 * @return Returns null if there are issues reading the JSON, otherwise returns
	 *         the parsed file.
	 */
	@Override
	public HashMap<String, Currency> loadDataFromFilePath(String filePath) {
		String properPath = filePath.replace("/", fileSeparator);
		File file = new File(properPath);

		HashMap<String, Currency> data = null;
		try {
			data = objectMapper.readValue(file, new TypeReference<HashMap<String, Currency>>() {
			});
		} catch (DatabindException dbe) {

			log("Unable to parse JSON file data at path " + properPath + " to HashMap<String, Currency>",
					StandardLevel.ERROR);
			return null;
		} catch (IOException ioe) {
			log("Unable to read JSON file at path " + properPath, StandardLevel.ERROR);
			return null;
		}

		if (data.isEmpty()) {
			log("Parsed FX JSON file at path " + properPath + " as empty", StandardLevel.WARN);
		}

		String successMessage = "Successfully parsed JSON file data at path " + properPath + ": " + data.size()
				+ " items";

		log(successMessage, StandardLevel.INFO);

		return data;
	}
}
