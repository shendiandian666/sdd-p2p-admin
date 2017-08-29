package com.sdd;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class Test {

	public static void main(String[] args) {
		System.out.println(moneyCurrencyFormat("10000.00"));
	}
	
	private final static NumberFormat CURRENCY_FORMAT = NumberFormat.getNumberInstance(Locale.CHINA); //建立货币格式化引用

	public static String moneyCurrencyFormat(String money) {
		//CURRENCY_FORMAT.setMinimumFractionDigits(0);
        money = CURRENCY_FORMAT.format(new BigDecimal(money));
	    return money;
	}

}
