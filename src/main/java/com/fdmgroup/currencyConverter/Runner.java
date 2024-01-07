package com.fdmgroup.currencyConverter;

public class Runner {

	private final static String transactionFilePath = "src/main/resources/transactions.txt";
	private final static String userDataFilePath = "src/main/resources/users2.json";

	public static void main(String[] args) {
		TransactionProcessor transactionProcessor = new TransactionProcessor();
		transactionProcessor.processAllTransactions(transactionFilePath, userDataFilePath);
		
	}

}
