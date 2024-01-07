package com.fdmgroup.currencyConverter.io;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.StandardLevel;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Parent class for loading JSON data via Jackson.
 */
public abstract class JsonDataReader<T> implements DataReader<T> {
	protected static final ObjectMapper objectMapper = new ObjectMapper();

	abstract void log(String message, StandardLevel level);

	abstract void setLogger(Logger logger);

	/**
	 * Loads JSON containing data. Expects a JSON formatted as {@code T}
	 * 
	 * @param filePath Path to JSON file
	 * @return Returns null if there are issues reading the JSON, otherwise returns
	 *         the parsed file as an object or collection {@code T}
	 */
	public abstract T loadDataFromFilePath(String filePath);

}
