package com.fdmgroup.currencyConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.StandardLevel;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UserJsonDataWriter {
	private static Logger logger = LogManager.getLogger(UserJsonDataWriter.class);
	static final String fileSeparator = FileSystems.getDefault().getSeparator();
	private static final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Used with Mockito to verify logging
	 * 
	 * @param logger Mock logger
	 */
	public void setLogger(Logger logger) {
		UserJsonDataWriter.logger = logger;
	}

	private void log(String message, StandardLevel level) {
		switch (level) {
		case INFO:
			logger.info(message);
			break;
		case ERROR:
			logger.error(message);
			break;
		case DEBUG:
			logger.debug(message);
			break;
		default:
			break;
		}
	}

	public void writeDataToFilePath(String filePath, ArrayList<User> userData) {
		String properPath = filePath.replace("/", fileSeparator);
		File file = new File(properPath);
		try {
			objectMapper.writeValue(file, userData);
			log("Successfully wrote User data to " + properPath, StandardLevel.INFO);
		} catch (IOException e) {
			log("Unable to write User data to " + properPath, StandardLevel.ERROR);
		}
	}

}
