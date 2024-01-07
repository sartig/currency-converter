package com.fdmgroup.currencyConverter.io;

import java.nio.file.FileSystems;
/**
 * Generic interface for any class that reads data from a given file path
 * @param <T> Format of data to read
 */
public interface DataReader<T> {
	static final String fileSeparator = FileSystems.getDefault().getSeparator();

	T loadDataFromFilePath(String filePath);

}
