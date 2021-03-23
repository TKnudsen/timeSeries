package com.github.TKnudsen.timeseries.operations.tools;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import com.github.TKnudsen.timeseries.data.primitives.TimeQuantization;

/**
 * <p>
 * Title: DateTools
 * </p>
 * 
 * <p>
 * Description: Provides little helpers when dealing with Date objects.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.0
 */
public class DateTools {

	/**
	 * Retrieves whether or not a date is within a leap year.
	 * 
	 * @param y
	 * @return
	 */
	public static boolean isLeapYear(int y) {
		return ((y % 4 == 0) && ((y % 100 != 0) || (y % 400 == 0)));
	}

	/**
	 * Creates a Date object with the given arguments. Please consider argument
	 * constraints.
	 * 
	 * @param year  the actual year. no -1900 needed.
	 * @param month from 0-11
	 * @param day   from 1-31
	 * 
	 * @return
	 */
	public static Date createDate(int year, int month, int day) {
		return createDate(year, month, day, 0, 0, 0, 0);
	}

	/**
	 * Creates a Date object with the given arguments. Please consider argument
	 * constraints.
	 * 
	 * @param year   the actual year. no -1900 needed.
	 * @param month  from 0-11
	 * @param day    from 1-31
	 * @param hour   from 0-23
	 * @param minute from 0-59
	 * @param second from 0-59
	 * @return
	 */
	public static Date createDate(int year, int month, int day, int hour, int minute, int second, int milliSecond) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.MILLISECOND, milliSecond);

		return cal.getTime();
	}

	/**
	 * 
	 * @param date
	 * @param Calendar with respect to the Calendar Enum. Example: Calendar.DATE
	 *                 refers to the date within the month[1-31]. Example:
	 *                 DateTools.addDateOrTime(new Date(), Calendar.DATE, -10);
	 * @param value    number of days +-
	 * @return new adapted date
	 */
	public static Date addDateOrTime(Date date, int calendar, int value) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(calendar, value);
		return cal.getTime();
	}

	/**
	 * Rounds down a date object with respect to a given time quantization.
	 * Attention: weeks is an anomaly in the calendar an is not supported.
	 * 
	 * @param date
	 * @param timeQuantization
	 * @return
	 */
	public static Date roundDown(Date date, TimeQuantization timeQuantization) {
		if (timeQuantization.equals(TimeQuantization.WEEKS))
			throw new IllegalArgumentException("rounding weeks not supported");

		Calendar val = Calendar.getInstance();
		val.setTime(date);

		val.set(Calendar.MILLISECOND, 0);
		if (timeQuantization.equals(TimeQuantization.MILLISECONDS))
			return val.getTime();

		if (timeQuantization.equals(TimeQuantization.SECONDS))
			return val.getTime();

		val.set(Calendar.SECOND, 0);

		if (timeQuantization.equals(TimeQuantization.MINUTES))
			return val.getTime();

		val.set(Calendar.MINUTE, 0);

		if (timeQuantization.equals(TimeQuantization.HOURS))
			return val.getTime();

		val.set(Calendar.HOUR, 0);

		if (timeQuantization.equals(TimeQuantization.DAYS))
			return val.getTime();

		val.set(Calendar.DAY_OF_MONTH, 1);

		if (timeQuantization.equals(TimeQuantization.MONTHS))
			return val.getTime();

		val.set(Calendar.MONTH, 0);

		if (timeQuantization.equals(TimeQuantization.YEARS))
			return val.getTime();

		return val.getTime();
	}

	/**
	 * Rounds up a date object with respect to a given time quantization. Attention:
	 * weeks is an anomaly in the calendar an is not supported.
	 * 
	 * @param date
	 * @param timeQuantization
	 * @return
	 */
	public static Date roundUp(Date date, TimeQuantization timeQuantization) {
		if (timeQuantization.equals(TimeQuantization.WEEKS))
			throw new IllegalArgumentException("rounding weeks not supported");

		Calendar val = Calendar.getInstance();
		val.setTime(roundDown(date, timeQuantization));

		if (timeQuantization.equals(TimeQuantization.MILLISECONDS))
			val.set(Calendar.MILLISECOND, val.get(Calendar.MILLISECOND) + 1);
		else if (timeQuantization.equals(TimeQuantization.SECONDS))
			val.set(Calendar.SECOND, val.get(Calendar.SECOND + 1) + 1);
		else if (timeQuantization.equals(TimeQuantization.MINUTES))
			val.set(Calendar.MINUTE, val.get(Calendar.MINUTE) + 1);
		else if (timeQuantization.equals(TimeQuantization.HOURS))
			val.set(Calendar.HOUR, val.get(Calendar.HOUR) + 1);
		else if (timeQuantization.equals(TimeQuantization.DAYS))
			val.set(Calendar.DAY_OF_MONTH, val.get(Calendar.DAY_OF_MONTH) + 1);
		else if (timeQuantization.equals(TimeQuantization.MONTHS))
			val.set(Calendar.MONTH, val.get(Calendar.MONTH) + 1);
		else if (timeQuantization.equals(TimeQuantization.YEARS))
			val.set(Calendar.YEAR, val.get(Calendar.YEAR) + 1);

		return val.getTime();
	}

	/**
	 * Retrieves the age of a date.
	 * 
	 * @param birthday
	 * @param today
	 * @return
	 */
	public static int getAge(Date birthday, Date today) {
		GregorianCalendar birthd = new GregorianCalendar();
		birthd.setTime(birthday);

		GregorianCalendar today_ = new GregorianCalendar();
		today_.setTime(today);

		int year = today_.get(Calendar.YEAR) - birthd.get(Calendar.YEAR);

		if (today_.get(Calendar.MONTH) < birthd.get(Calendar.MONTH)) {
			year -= 1;
		} else if (today_.get(Calendar.MONTH) == birthd.get(Calendar.MONTH)) {
			if (today_.get(Calendar.DATE) < birthd.get(Calendar.DATE)) {
				year -= 1;
			}
		}

		if (year < 0)
			throw new IllegalArgumentException("invalid age: " + year);

		return year;
	}

	/**
	 * 
	 * @return
	 */
	public static int getCurrentYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	public static int getYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}

	/**
	 * calculates the absolute difference between two dates. the time quantization
	 * can be chosen.
	 * 
	 * @param date1
	 * @param date2
	 * @param timeUnit
	 * @return
	 */
	public static long diff(Date date1, Date date2, TimeUnit timeUnit) {
		long diff = Math.abs(date2.getTime() - date1.getTime());

		return timeUnit.convert(diff, TimeUnit.MILLISECONDS);
	}
}
