package com.fdmgroup.currencyConverter;

import java.nio.file.FileSystems;

public interface DataReader<T> {
	static final String fileSeparator = FileSystems.getDefault().getSeparator();

	T loadDataFromFilePath(String filePath);

}
