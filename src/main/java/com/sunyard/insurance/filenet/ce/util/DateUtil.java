package com.sunyard.insurance.filenet.ce.util;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * <B>Title:</B> DateTimeUtil
 * <p>
 * <B>Description:</B> Date and time's management.
 * 
 * @author lzm
 * 
 */
public class DateUtil {
	
	public static String getCurDateTime(String pattern) {
		return getCurDateTime(pattern, null);
	}

	public static String getCurDateTime(String pattern, String zone) {
		DateFormat df = getDateFormat(pattern);
		if (zone != null && !zone.equals("")) {
			df.setTimeZone(TimeZone.getTimeZone(zone));
		}
		return df.format(new Date());
	}

	public static String formatDateTime(String dateTime, String oldPattern,
			String newPattern) {
		DateFormat df = null;
		df = getDateFormat(oldPattern);
		Date date = df.parse(dateTime, new ParsePosition(0));
		df = getDateFormat(newPattern);
		return df.format(date);
	}

	public static String formatDateTime(String pattern, Date date) {
		return getDateFormat(pattern).format(date);
	}

	public static Calendar getCalendar(String dateTime, String pattern) {
		Calendar calendar = Calendar.getInstance();
		DateFormat df = getDateFormat(pattern);
		Date date = df.parse(dateTime, new ParsePosition(0));
		calendar.setTime(date);
		return calendar;
	}

	public static Calendar getCalendar(int field, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(field, amount);
		return calendar;
	}

	public static String getDateTime(long dateTime, String pattern) {
		DateFormat df = getDateFormat(pattern);
		Date date = new Date(dateTime);
		return df.format(date);
	}

	/*
	 * Get pattern.
	 */
	private static DateFormat getDateFormat(String pattern) {
		return new SimpleDateFormat(pattern);
	}
	
}
