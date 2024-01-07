package com.fdmgroup.currencyConverter.io;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.StandardLevel;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JsonDataReader<T> implements DataReader<T> {
	protected static final ObjectMapper objectMapper = new ObjectMapper();

	abstract void log(String message, StandardLevel level);

	abstract void setLogger(Logger logger);

	public abstract T loadDataFromFilePath(String filePath);

	/**
	 * Loads JSON containing data. Expects a JSON formatted as some collection.
	 * 
	 * @param filePath Path to JSON file
	 * @return Returns null if there are issues reading the JSON, otherwise returns
	 *         the parsed file.
	 */
	/*
	 * @Override public T loadDataFromFilePath(String filePath, String dataFormat) {
	 * String properPath = filePath.replace("/", fileSeparator); File file = new
	 * File(properPath);
	 * 
	 * T data = null; try { data = objectMapper.readValue(file, new
	 * TypeReference<T>() { }); } catch (DatabindException dbe) {
	 * 
	 * log("Unable to parse JSON file data at path " + properPath + " to " +
	 * dataFormat, "error"); return null; } catch (IOException ioe) {
	 * log("Unable to read JSON file at path " + properPath, "error"); return null;
	 * }
	 * 
	 * int size = -1; if (data instanceof Collection<?>) { Collection<?>
	 * dataAsCollection = (Collection<?>) data; size = dataAsCollection.size(); if
	 * (dataAsCollection.isEmpty()) { log("Parsed User JSON file at path " +
	 * properPath + " as empty", "warn"); } } else if (data instanceof Map<?, ?>) {
	 * Map<?, ?> dataAsMap = (Map<?, ?>) data; size = dataAsMap.size();
	 * System.out.println(data.getClass().toString());
	 * System.out.println(dataAsMap.get("gbp").getClass().toString());
	 * System.out.println(((Map<?, ?>)
	 * dataAsMap.get("gbp")).get("name").getClass().toString()); if
	 * (dataAsMap.isEmpty()) { log("Parsed User JSON file at path " + properPath +
	 * " as empty", "warn"); } }
	 * 
	 * String successMessage = "Successfully parsed JSON file data at path " +
	 * properPath; if (size >= 0) { successMessage += ": " + size + " items"; }
	 * log(successMessage, "info");
	 * 
	 * return data; }
	 */

}
