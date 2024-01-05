package com.fdmgroup.currencyConverter;

import java.util.Arrays;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class used for calculating conversion data between currencies.
 * 
 * Singleton to conserve memory
 */
public class CurrencyConverter {
	private static final String fxJsonFilePath = "src/main/resources/fx_rates.json";
	private static CurrencyConverter instance;
	private HashMap<String, Currency> currencyData = new HashMap<String, Currency>();
	private static Logger logger = LogManager.getLogger(CurrencyConverter.class);

	/**
	 * Constructor is private as {@code CurrencyConverter} is a singleton. The JSON
	 * data only needs to be loaded once
	 */
	private CurrencyConverter() {
		// load json and store into hashmap
		currencyData = FXDataReader.loadJsonFromFilepath(fxJsonFilePath);
	}

	/**
	 * Used with Mockito to verify logging.
	 * 
	 * @param logger Mock logger
	 */
	public void setLogger(Logger logger) {
		CurrencyConverter.logger = logger;
	}

	/**
	 * Singleton method
	 * 
	 * @return Returns CurrencyConverter singleton
	 */
	public static CurrencyConverter getInstance() {
		if (instance == null) {
			instance = new CurrencyConverter();
		}
		return instance;
	}

	/**
	 * Method to validate whether all provided currencies have an entry in the
	 * HashMap. USD is a special case as it is a valid currency but not stored in
	 * the HashMap.
	 * 
	 * @param currencies Selection of currencies to validate
	 * @return Returns true only if all currencies given are valid
	 */
	public boolean validate(String... currencies) {
		for (String s : currencies) {
			if (!currencyData.containsKey(s.toLowerCase()) && !"usd".equalsIgnoreCase(s)) {
				logger.warn("Currency " + s + " not valid");
				return false;
			}
		}
		logger.info("Currency list " + Arrays.toString(currencies) + " validated");
		return true;
	}

	/**
	 * Calculates conversion between currencies. USD is the anchor currency so its
	 * multipliers are always 1.
	 * 
	 * @param startCurrency Starting currency as three lowercase letter code
	 * @param endCurrency   Destination currency as three lowercase letter code
	 * @param amount        Amount of starting currency to convert
	 * @return
	 */
	public double convert(String startCurrency, String endCurrency, double amount) {
		if (startCurrency.equalsIgnoreCase(endCurrency)) {
			logger.warn("Trying to convert between identical currencies: " + startCurrency);
			return amount;
		}
		double toUsd = "usd".equalsIgnoreCase(startCurrency) ? 1 : currencyData.get(startCurrency).getInverseRate();
		double fromUsd = "usd".equalsIgnoreCase(endCurrency) ? 1 : currencyData.get(endCurrency).getRate();

		double result = amount * toUsd * fromUsd;
		String logString = String.format("Conversion of %.2f %s to %.2f %s", amount, startCurrency, result,
				endCurrency);
		logger.info(logString);
		return result;
	}
}
