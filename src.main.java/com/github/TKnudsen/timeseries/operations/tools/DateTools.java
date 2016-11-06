package com.github.TKnudsen.timeseries.operations.tools;

import java.util.Calendar;
import java.util.Date;

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
	 * Creates a Date object with the given arguments. Please consider argument
	 * constraints.
	 * 
	 * @param year
	 *            the actual year. no -1900 needed.
	 * @param month
	 *            from 0-11
	 * @param day
	 *            from 1-31
	 * @param hour
	 *            from 0-23
	 * @param minute
	 *            from 0-59
	 * @param second
	 *            from 0-59
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
	 * @param CalendarField
	 *            with respect to the Calendar Enum. Example: Calendar.DATE
	 *            refers to the date within the month[1-31].
	 * @param value
	 * @return
	 */
	public static Date manipulateDate(Date date, int CalendarField, int value) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(CalendarField, value);
		return cal.getTime();
	}
}
