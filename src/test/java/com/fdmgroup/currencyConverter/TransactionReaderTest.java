package com.fdmgroup.currencyConverter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import java.nio.file.FileSystems;
import java.util.Queue;

import org.apache.logging.log4j.core.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionReaderTest {

	TransactionReader transactionReader = new TransactionReader();

	@Mock
	Logger mockLogger;

	@BeforeEach
	void setUp() throws Exception {
		transactionReader.setLogger(mockLogger);
	}

	@Test
	void loadData_WithInvalidFilePath_ReturnsEmptyQueue_AndLogsError() {
		Queue<Transaction> result = transactionReader.readTransactions("invalidFile.txt");
		verify(mockLogger).error("Unable to read file at path invalidFile.txt");
		assertTrue(result.isEmpty());
	}

	@Test
	void loadData_WithFilePathToEmptyFile_ReturnsEmptyQueue_AndLogsWarning() {
		String filePath = "src/test/resources/transactions_empty.txt";
		String properFilePath = filePath.replace("/", FileSystems.getDefault().getSeparator());
		Queue<Transaction> result = transactionReader.readTransactions(filePath);
		verify(mockLogger).warn("Parsing file at " + properFilePath + " resulted in zero transactions");
		assertTrue(result.isEmpty());
	}

	@Test
	void loadData_WithFilePathToFileWithIncorrectFormat_ReturnsEmptyQueue_AndLogsErrorAndWarning() {
		String filePath = "src/test/resources/transactions_incorrectFormat.txt";
		String properFilePath = filePath.replace("/", FileSystems.getDefault().getSeparator());
		Queue<Transaction> result = transactionReader.readTransactions(filePath);
		verify(mockLogger).error("Unable to parse line 1: 'Bob cad usd 100 extra' - skipping to next line");
		verify(mockLogger).warn("Parsing file at " + properFilePath + " resulted in zero transactions");
		assertTrue(result.isEmpty());
	}

	@Test
	void loadData_WithFilePathToFileWithNonNumericAmountValue_ReturnsEmptyQueue_AndLogsError() {
		String filePath = "src/test/resources/transactions_stringAmount.txt";
		String properFilePath = filePath.replace("/", FileSystems.getDefault().getSeparator());
		Queue<Transaction> result = transactionReader.readTransactions(filePath);
		verify(mockLogger).error("Unable to parse line 1: amount 'five' as a double - skipping to next line");
		verify(mockLogger).warn("Parsing file at " + properFilePath + " resulted in zero transactions");
		assertTrue(result.isEmpty());

	}

	@Test
	void loadData_WithFilePathToFileWithTwoValidTransactions_ReturnsCorrectQueue_AndLogsInfo() {
		String filePath = "src/test/resources/transactions_two.txt";
		String properFilePath = filePath.replace("/", FileSystems.getDefault().getSeparator());
		Queue<Transaction> result = transactionReader.readTransactions(filePath);
		verify(mockLogger).info("Successfully parsed 2 transactions from file " + properFilePath);
		Transaction first = result.peek();
		assertEquals(2, result.size());
		assertEquals("Bob", first.getName());
		assertEquals(100, first.getAmount());
	}

	@Test
	void loadData_WithFilePathToFileWithTwoValidTransactionsAndOneInvalidTransaction_ReturnsCorrectQueue_AndLogsInfoAndErrors() {
		String filePath = "src/test/resources/transactions_two_and_wrong.txt";
		String properFilePath = filePath.replace("/", FileSystems.getDefault().getSeparator());
		Queue<Transaction> result = transactionReader.readTransactions(filePath);
		verify(mockLogger).error("Unable to parse line 3: amount 'heh' as a double - skipping to next line");
		verify(mockLogger).info("Successfully parsed 2 transactions from file " + properFilePath);
		Transaction first = result.peek();
		assertEquals(2, result.size());
		assertEquals("Chad", first.getName());
		assertEquals(200.59, first.getAmount());
	}

}
