package com.fdmgroup.currencyConverter.currency;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fdmgroup.currencyConverter.io.FXJsonDataReader;

/**
 * Class used for calculating conversion data between currencies.
 * 
 * Singleton to conserve memory
 */
public class CurrencyConverter {
	private static final String FX_JSON_FILE_PATH = "src/main/resources/fx_rates.json";
	private static CurrencyConverter instance;
	private HashMap<String, Currency> currencyData = new HashMap<String, Currency>();
	private static Logger logger = LogManager.getLogger(CurrencyConverter.class);

	/**
	 * Constructor is private as {@code CurrencyConverter} is a singleton. The JSON
	 * data only needs to be loaded once
	 */
	private CurrencyConverter() {
		// load json and store into hashmap
		FXJsonDataReader fxJsonDataReader = new FXJsonDataReader();
		currencyData = fxJsonDataReader.loadDataFromFilePath(FX_JSON_FILE_PATH);
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
	 * Private method to validate currencies with additional option whether to log.
	 * Used in
	 * {@code convert(String startCurrency, String endCurrency, double amount)} to
	 * perform validation of currencies with a different error log.
	 * 
	 * @param shouldLog
	 * @param currencies
	 * @return
	 */
	private boolean validate(boolean shouldLog, String... currencies) {
		for (String s : currencies) {
			if (!currencyData.containsKey(s.toLowerCase()) && !"usd".equalsIgnoreCase(s)) {
				if (shouldLog) {
					logger.warn("Currency " + s + " not valid");
				}
				return false;
			}
		}
		if (shouldLog) {
			logger.info("Currency list " + Arrays.toString(currencies) + " validated");
		}
		return true;
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
		return validate(true, currencies);
	}

	/**
	 * Calculates conversion between currencies. USD is the anchor currency so its
	 * multipliers are always 1. Results are rounded down to 2 decimal places to
	 * prevent creating additional value.
	 * 
	 * If this method logs an error then it is likely that
	 * {@code validate(String... currencies} was not called
	 * 
	 * @param startCurrency Starting currency as three lowercase letter code
	 * @param endCurrency   Destination currency as three lowercase letter code
	 * @param amount        Amount of starting currency to convert. Rounded down if
	 *                      more than 2 decimal places given.
	 * @return
	 */
	public BigDecimal convert(String startCurrency, String endCurrency, BigDecimal amount) {
		if (startCurrency.equalsIgnoreCase(endCurrency)) {
			logger.warn("Trying to convert between identical currencies: " + startCurrency);
			return amount;
		}

		if (!validate(false, startCurrency)) {
			// if called, means validate() method was not called prior
			logger.error("Trying to convert invalid currency: " + startCurrency);
			return BigDecimal.ZERO;
		}
		if (!validate(false, endCurrency)) {
			// if called, means validate() method was not called prior
			logger.error("Trying to convert invalid currency: " + endCurrency);
			return BigDecimal.ZERO;
		}

		// give warning, but still log as a successful conversion
		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			logger.warn(
					"Trying to convert zero " + startCurrency + " to " + endCurrency + " - method call unnecessary");
			logger.info(String.format("Conversion of %.2f %s to %.2f %s", 0.0, startCurrency, 0.0, endCurrency));
			return amount;
		}

		double toUsd = "usd".equalsIgnoreCase(startCurrency) ? 1 : currencyData.get(startCurrency).getInverseRate();
		double fromUsd = "usd".equalsIgnoreCase(endCurrency) ? 1 : currencyData.get(endCurrency).getRate();

		// prevent input being greater precision than allowed
		BigDecimal startAmount = CurrencyRounder.roundCurrency(amount, false);
		String toUsdString = Double.toString(toUsd);
		String fromUsdString = Double.toString(fromUsd);
		BigDecimal result = startAmount.multiply(new BigDecimal(toUsdString)).multiply(new BigDecimal(fromUsdString));
		// round down final result to prevent the "creation" of extra currency
		BigDecimal finalResult = CurrencyRounder.roundCurrency(result, false);

		// String.format ensures consistent display of currency amounts
		logger.info(String.format("Conversion of %.2f %s to %.2f %s", startAmount.doubleValue(), startCurrency, finalResult.doubleValue(),
				endCurrency));
		return finalResult;
	}
}
