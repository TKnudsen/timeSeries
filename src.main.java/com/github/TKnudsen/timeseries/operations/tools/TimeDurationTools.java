package com.github.TKnudsen.timeseries.operations.tools;

import java.util.Date;
import java.util.HashMap;

import com.github.TKnudsen.timeseries.data.ITimeSeries;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.primitives.TimeQuantization;

/**
 * <p>
 * Title: TimeDurationTools.
 * </p>
 * 
 * <p>
 * Description: provides little helpers whenever a TimeDuration is to be
 * interpreted or to be calculated. Remember: a TimeDuration is a tuple
 * consisting of a long and a duration (such as minute, second, hour, etc.).
 * 
 * Please do not mix TimeDurations with TimeIntervals (which would be something
 * with a concrete start and a concrete end time).
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2010-2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.04
 */

public class TimeDurationTools {

	/**
	 * Calculates the flat date before the start time according to the given
	 * time series interval. example: 00:00:00 @ hourly means 01:00:00
	 * 
	 * @param startTime
	 * @param patternInterval
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Date getDateBeforeStartTimeAccordingToPatternInterval(long startTime, TimeDuration patternInterval) {
		Date date = new Date(startTime);
		if (patternInterval.getType() == TimeQuantization.MILLISECONDS) {
			// do nothing
		} else {
			date = new Date(startTime / 1000 * 1000);
			if (patternInterval.getType() == TimeQuantization.SECONDS) {
				// do nothing
			} else {
				date.setSeconds(0);
				if (patternInterval.getType() == TimeQuantization.MINUTES) {
					// do nothing
				} else {
					date.setMinutes(0);
					if (patternInterval.getType() == TimeQuantization.HOURS) {
						// do nothing
					} else {
						date.setHours(0);
						if (patternInterval.getType() == TimeQuantization.DAYS) {
							// do nothing
						} else if (patternInterval.getType() == TimeQuantization.WEEKS) {
							while (date.getDay() != 1)
								date.setDate(date.getDate() - 1);
						} else if (patternInterval.getType() == TimeQuantization.MONTHS) {
							date.setDate(1);
						} else if (patternInterval.getType() == TimeQuantization.YEARS) {
							date.setDate(1);
							date.setMonth(0);
						} else if (patternInterval.getType() == TimeQuantization.DECADES) {
							date.setDate(1);
							date.setMonth(0);
							date.setYear(date.getYear() / 10 * 10);
						}
					}
				}
			}
		}
		return date;
	}

	/**
	 * Calculates the flat date/time after the start time according to the given
	 * time series interval. if the TimeQuantization of the patternInterval is
	 * at least of one day length, the next 00:00:00 GMT time is acchieved
	 * 
	 * @param startTime
	 * @param patternInterval
	 * @return
	 */
	public static Date getDateAfterStartTimeAccordingToPatternInterval(long startTime, TimeDuration patternInterval) {

		if (patternInterval.getDuration() == 0) {
			return null;
		}
		long mod = startTime % patternInterval.getDuration();
		if (mod != 0)
			startTime += (patternInterval.getDuration() - mod);
		Date date = new Date(startTime);
		return date;
	}

	/**
	 * Heuristics that calculates the most suitable equi-distance of a given
	 * time series.
	 * 
	 * @param timeSeries
	 * @return TimeSeriesInterval of the dominating quantization
	 */
	public static TimeDuration getDominatingTimeSeriesQuantization(ITimeSeries<?> timeSeries) {

		HashMap<Long, Integer> durationCounts = new HashMap<Long, Integer>();

		for (int i = 1; i < timeSeries.size(); i++) {
			Long duration = Math.abs(timeSeries.getTimestamp(i) - timeSeries.getTimestamp(i - 1));
			if (durationCounts.get(duration) == null)
				durationCounts.put(duration, 0);
			else
				durationCounts.put(duration, durationCounts.get(duration) + 1);
		}

		int max = 0;
		long dominatingQuantization = 0L;

		for (Long l : durationCounts.keySet())
			if (durationCounts.get(l) > max) {
				dominatingQuantization = l;
				max = durationCounts.get(l);
			}

		return calculateTimeDuration(dominatingQuantization);
	}

	/**
	 * Calculates the TimeDuration representation of a given duration in long.
	 * 
	 * @param duration
	 * @return
	 */
	public static TimeDuration calculateTimeDuration(long duration) {
		// Milliseconds
		if (duration % 1000 == 0) {
			duration /= 1000;
			// Seconds
			if (duration % 60 == 0) {
				duration /= 60;
				// Minutes
				if (duration % 60 == 0) {
					duration /= 60;
					// Hours
					if (duration % 24 == 0) {
						duration /= 24;
						// Days
						return new TimeDuration(TimeQuantization.DAYS, duration);
					} else {
						return new TimeDuration(TimeQuantization.HOURS, duration);
					}
				} else {
					return new TimeDuration(TimeQuantization.MINUTES, duration);
				}
			} else {
				return new TimeDuration(TimeQuantization.SECONDS, duration);
			}
		} else {
			return new TimeDuration(TimeQuantization.MILLISECONDS, duration);
		}
	}

	/**
	 * @deprecated use calculateTimeDuration.
	 * @param equidistanceInMillis
	 * @return
	 */
	public static TimeDuration calculateEquidistanceFromLongMillis(long equidistanceInMillis) {
		// Milliseconds
		if (equidistanceInMillis % 1000 == 0) {
			equidistanceInMillis /= 1000;
			// Seconds
			if (equidistanceInMillis % 60 == 0) {
				equidistanceInMillis /= 60;
				// Minutes
				if (equidistanceInMillis % 60 == 0) {
					equidistanceInMillis /= 60;
					// Hours
					if (equidistanceInMillis % 24 == 0) {
						equidistanceInMillis /= 24;
						// Days
						return new TimeDuration(TimeQuantization.DAYS, equidistanceInMillis);
					} else {
						return new TimeDuration(TimeQuantization.HOURS, equidistanceInMillis);
					}
				} else {
					return new TimeDuration(TimeQuantization.MINUTES, equidistanceInMillis);
				}
			} else {
				return new TimeDuration(TimeQuantization.SECONDS, equidistanceInMillis);
			}
		} else {
			return new TimeDuration(TimeQuantization.MILLISECONDS, equidistanceInMillis);
		}
	}

	/**
	 * Estimates the most appropriate TimeQuantization (minutes, hours, days) of
	 * a given duration.
	 * 
	 * @param duration
	 * @return
	 */
	public static TimeQuantization guessTimeQuantization(long duration) {
		// Milliseconds?
		if (duration < 1000)
			return TimeQuantization.MILLISECONDS;
		else {
			duration /= 1000;
			// Seconds?
			if (duration < 60)
				return TimeQuantization.SECONDS;
			else {
				duration /= 60;
				// Minutes?
				if (duration < 60)
					return TimeQuantization.MINUTES;
				else {
					duration /= 60;
					// Hours?
					if (duration < 24)
						return TimeQuantization.HOURS;
					else {
						duration /= 24;
						// Days?
						if (duration < 365)
							return TimeQuantization.DAYS;
						else
							return TimeQuantization.YEARS;
					}
				}
			}
		}

	}

	/**
	 * @deprecated use guessTimeQuantization
	 * @param equidistanceInMillis
	 * @return
	 */
	public static TimeQuantization calculateSuitableTimeQuantization(long equidistanceInMillis) {
		// Milliseconds?
		if (equidistanceInMillis < 1000)
			return TimeQuantization.MILLISECONDS;
		else {
			equidistanceInMillis /= 1000;
			// Seconds?
			if (equidistanceInMillis < 60)
				return TimeQuantization.SECONDS;
			else {
				equidistanceInMillis /= 60;
				// Minutes?
				if (equidistanceInMillis < 60)
					return TimeQuantization.MINUTES;
				else {
					equidistanceInMillis /= 60;
					// Hours?
					if (equidistanceInMillis < 24)
						return TimeQuantization.HOURS;
					else {
						equidistanceInMillis /= 24;
						// Days?
						if (equidistanceInMillis < 365)
							return TimeQuantization.DAYS;
						else
							return TimeQuantization.YEARS;
					}
				}
			}
		}
	}
}
