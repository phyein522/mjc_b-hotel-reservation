package com.mjc.hotel.common;

public class Utils {
	public static String getRamdomString(int length) {
		String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder sb = new StringBuilder();
		for ( int i = 0; i < length; i++) {
			sb.append(str.charAt((int)(Math.random() * str.length())));
		}
		return sb.toString();
	}
}
