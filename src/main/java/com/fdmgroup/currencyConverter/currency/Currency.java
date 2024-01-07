package com.fdmgroup.currencyConverter.currency;

/**
 * Simple POJO class to store data on currencies
 */
public class Currency {
	private String code;
	private String alphaCode;
	private String numericCode;
	private String name;
	private double rate;
	private String date;
	private double inverseRate;

	/**
	 * Zero-args constructor required for Jackson to parse Currency class properly
	 */
	public Currency() {
		
	}
	
	/**
	 * Constructor
	 * @param code
	 * @param alphaCode
	 * @param numericCode
	 * @param name
	 * @param rate
	 * @param date
	 * @param inverseRate
	 */
	public Currency(String code, String alphaCode, String numericCode, String name, double rate, String date,
			double inverseRate) {
		super();
		this.code = code;
		this.alphaCode = alphaCode;
		this.numericCode = numericCode;
		this.name = name;
		this.rate = rate;
		this.date = date;
		this.inverseRate = inverseRate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAlphaCode() {
		return alphaCode;
	}

	public void setAlphaCode(String alphaCode) {
		this.alphaCode = alphaCode;
	}

	public String getNumericCode() {
		return numericCode;
	}

	public void setNumericCode(String numericCode) {
		this.numericCode = numericCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getInverseRate() {
		return inverseRate;
	}

	public void setInverseRate(double inverseRate) {
		this.inverseRate = inverseRate;
	}

}
