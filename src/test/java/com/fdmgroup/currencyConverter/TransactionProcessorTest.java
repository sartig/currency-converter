package com.fdmgroup.currencyConverter;

import static org.mockito.Mockito.*;

import java.util.LinkedList;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionProcessorTest {
	@Mock
	UserManager mockUserManager;
	@Mock
	UserJsonDataWriter mockUserJsonDataWriter;
	@Mock
	Logger mockLogger;
	@Mock
	CurrencyConverter mockCurrencyConverter;
	@Mock
	TransactionReader mockTransactionReader;

	Transaction transactionWithSameCurrency = new Transaction("Dupe", "usd", "usd", 19),
			transactionWithZeroAmount = new Transaction("Zero", "gbp", "hkd", 0),
			transactionWithInvalidCurrency = new Transaction("Invalid", "zzz", "aaa", 103),
			transactionWithBadName = new Transaction("Gone", "hkd", "usd", 100),
			transactionWithBadWallet = new Transaction("Poor", "hkd", "usd", 1000),
			transactionWithValid = new Transaction("Valid", "usd", "gbp", 10);

	TransactionProcessor transactionProcessor;

	@BeforeEach
	void setUp() throws Exception {
		transactionProcessor = new TransactionProcessor(mockUserManager, mockUserJsonDataWriter, mockLogger,
				mockCurrencyConverter, mockTransactionReader);
	}

	@Test
	void processAllTransactions_WithNullQueue_LogsWarning_AndDoesNotUpdateUserJson() {
		when(mockTransactionReader.readTransactions("dummy")).thenReturn(null);

		transactionProcessor.processAllTransactions("dummy", "dummy");

		verify(mockLogger).warn("Transaction list is null");
		verifyNoInteractions(mockUserJsonDataWriter);
	}

	@Test
	void processAllTransactions_WithEmptyQueue_LogsWarning_AndDoesNotUpdateUserJson() {
		when(mockTransactionReader.readTransactions("dummy")).thenReturn(new LinkedList<Transaction>());

		transactionProcessor.processAllTransactions("dummy", "dummy");

		verify(mockLogger).warn("Transaction list is empty");
		verifyNoInteractions(mockUserJsonDataWriter);
	}

	@Test
	void processAllTransactions_WithQueueWithOneItem_ThatHasZeroAmount_LogsWarning_AndDoesNotUpdateUserJson() {
		LinkedList<Transaction> input = new LinkedList<Transaction>();
		input.add(transactionWithZeroAmount);
		when(mockTransactionReader.readTransactions("dummy")).thenReturn(input);

		transactionProcessor.processAllTransactions("dummy", "dummy");

		verify(mockLogger).warn("Transaction request Zero: 0.00 gbp to hkd is invalid due to zero-value amount");
		verifyNoInteractions(mockUserJsonDataWriter);
	}

	@Test
	void processAllTransactions_WithQueueWithOneItem_ThatHasDuplicatedCurrency_LogsWarning_AndDoesNotUpdateUserJson() {
		LinkedList<Transaction> input = new LinkedList<Transaction>();
		input.add(transactionWithSameCurrency);
		when(mockTransactionReader.readTransactions("dummy")).thenReturn(input);

		transactionProcessor.processAllTransactions("dummy", "dummy");

		verify(mockLogger).warn("Transaction request Dupe: 19.00 usd to usd is invalid due to identical currencies");
		verifyNoInteractions(mockUserJsonDataWriter);
	}

	@Test
	void processAllTransactions_WithQueueWithOneItem_ThatHasInvalidCurrency_LogsWarning_AndDoesNotUpdateUserJson() {
		LinkedList<Transaction> input = new LinkedList<Transaction>();
		input.add(transactionWithInvalidCurrency);
		when(mockTransactionReader.readTransactions("dummy")).thenReturn(input);
		when(mockCurrencyConverter.validate("zzz")).thenReturn(false);

		transactionProcessor.processAllTransactions("dummy", "dummy");

		verify(mockLogger)
				.warn("Transaction request Invalid: 103.00 zzz to aaa is invalid due to invalid currency zzz");
		verifyNoInteractions(mockUserJsonDataWriter);
	}

	@Test
	void processAllTransactions_WithQueueWithOneItem_ThatHasInvalidUser_LogsWarning_AndDoesNotUpdateUserJson() {
		LinkedList<Transaction> input = new LinkedList<Transaction>();
		input.add(transactionWithBadName);
		when(mockTransactionReader.readTransactions("dummy")).thenReturn(input);
		when(mockCurrencyConverter.validate("hkd")).thenReturn(true);
		when(mockCurrencyConverter.validate("usd")).thenReturn(true);
		when(mockUserManager.validateUser("Gone")).thenReturn(false);

		transactionProcessor.processAllTransactions("dummy", "dummy");

		verify(mockLogger)
				.warn("Transaction request Gone: 100.00 hkd to usd is invalid due to user not existing in list");
		verifyNoInteractions(mockUserJsonDataWriter);
	}

	@Test
	void processAllTransactions_WithQueueWithOneItem_ThatHasNotEnoughInWallet_LogsWarning_AndDoesNotUpdateUserJson() {
		LinkedList<Transaction> input = new LinkedList<Transaction>();
		input.add(transactionWithBadWallet);
		when(mockTransactionReader.readTransactions("dummy")).thenReturn(input);
		when(mockCurrencyConverter.validate("hkd")).thenReturn(true);
		when(mockCurrencyConverter.validate("usd")).thenReturn(true);
		when(mockUserManager.validateUser("Poor")).thenReturn(true);
		when(mockUserManager.getUserBalance("Poor", "hkd")).thenReturn(50.0);

		transactionProcessor.processAllTransactions("dummy", "dummy");

		verify(mockLogger).warn(
				"Transaction request Poor: 1000.00 hkd to usd is invalid due to insufficient balance in user wallet (50.00)");
		verifyNoInteractions(mockUserJsonDataWriter);
	}

	@Test
	void processAllTransactions_WithQueueWithOneValidItem_LogsInfo_AndUpdatesUserJson() {
		LinkedList<Transaction> input = new LinkedList<Transaction>();
		input.add(transactionWithValid);
		when(mockTransactionReader.readTransactions("dummy")).thenReturn(input);
		when(mockCurrencyConverter.validate("usd")).thenReturn(true);
		when(mockCurrencyConverter.validate("gbp")).thenReturn(true);
		when(mockUserManager.validateUser("Valid")).thenReturn(true);
		when(mockUserManager.getUserBalance("Valid", "usd")).thenReturn(40.0);
		when(mockUserManager.getUserData()).thenReturn(null);

		transactionProcessor.processAllTransactions("dummy", "dummy2");

		verify(mockLogger).info("Transaction request Valid: 10.00 usd to gbp is valid");
		verify(mockLogger).info("Processed 1 of 1 transactions");
		verify(mockUserManager).executeTransaction(transactionWithValid);
		verify(mockUserJsonDataWriter).writeDataToFilePath("dummy2", null);
	}

	@Test
	void processAllTransactions_WithQueueWithOneValidItem_AndOneInvalidItem_LogsAppropriateInfo_AndUpdatesUserJson() {
		LinkedList<Transaction> input = new LinkedList<Transaction>();
		input.add(transactionWithValid);
		input.add(transactionWithZeroAmount);
		when(mockTransactionReader.readTransactions("dummy")).thenReturn(input);
		when(mockCurrencyConverter.validate("usd")).thenReturn(true);
		when(mockCurrencyConverter.validate("gbp")).thenReturn(true);
		when(mockUserManager.validateUser("Valid")).thenReturn(true);
		when(mockUserManager.getUserBalance("Valid", "usd")).thenReturn(40.0);
		when(mockUserManager.getUserData()).thenReturn(null);

		transactionProcessor.processAllTransactions("dummy", "dummy2");

		verify(mockLogger).warn("Transaction request Zero: 0.00 gbp to hkd is invalid due to zero-value amount");
		verify(mockLogger).info("Transaction request Valid: 10.00 usd to gbp is valid");
		verify(mockLogger).info("Processed 1 of 2 transactions");
		verify(mockUserManager).executeTransaction(transactionWithValid);
		verify(mockUserJsonDataWriter).writeDataToFilePath("dummy2", null);
	}

	@Test
	void processAllTransactions_WithQueueWithTwoValidItems_LogsInfo_AndUpdatesUserJsonTwice() {
		LinkedList<Transaction> input = new LinkedList<Transaction>();
		input.add(transactionWithValid);
		input.add(transactionWithValid);
		when(mockTransactionReader.readTransactions("dummy")).thenReturn(input);
		when(mockCurrencyConverter.validate("usd")).thenReturn(true);
		when(mockCurrencyConverter.validate("gbp")).thenReturn(true);
		when(mockUserManager.validateUser("Valid")).thenReturn(true);
		when(mockUserManager.getUserBalance("Valid", "usd")).thenReturn(40.0);
		when(mockUserManager.getUserData()).thenReturn(null);

		transactionProcessor.processAllTransactions("dummy", "dummy2");

		verify(mockLogger, times(2)).info("Transaction request Valid: 10.00 usd to gbp is valid");
		verify(mockLogger).info("Processed 2 of 2 transactions");
		verify(mockUserManager, times(2)).executeTransaction(transactionWithValid);
		verify(mockUserJsonDataWriter, times(2)).writeDataToFilePath("dummy2", null);
	}
}
