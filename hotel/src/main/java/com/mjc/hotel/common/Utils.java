package com.mjc.hotel.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Utils {
	public static String getRamdomString(int length) {
		String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder sb = new StringBuilder();
		for ( int i = 0; i < length; i++) {
			sb.append(str.charAt((int)(Math.random() * str.length())));
		}
		return sb.toString();
	}

	/**
	 * get String format "yyyy-MM-dd" or "yy/MM/dd" or "dd/MM/yyyy" from LocalDate
	 * @param localDate : 날짜
	 * @param format : 포맷 "yyyy-MM-dd"
	 * @return
	 */
	public static String getStringFormatFromLocalDate(LocalDate localDate, String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		String formattedDate = localDate.format(formatter);
		return formattedDate;
	}
}
