package com.github.TKnudsen.timeseries.data.primitives;

import java.util.Date;

import com.github.TKnudsen.ComplexDataObject.data.interval.NumberInterval;

/**
 * <p>
 * Stores a start and an end time stamp
 * </p>
 * 
 * <p>
 * Copyright (c) 2016-2019
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.05
 */
public class TimeInterval extends NumberInterval {

	protected TimeInterval() {

	}

	public TimeInterval(long startTime, long endTime) {
		this(startTime, endTime, "");
	}

	public TimeInterval(long startTime, long endTime, String name) {
		super(startTime, endTime, name);
	}

	public TimeDuration getTimeDuration() {
		return new TimeDuration(TimeQuantization.MILLISECONDS, Math.abs(getEndTime() - getStartTime()));
	}

	public long getStartTime() {
		return getStart().longValue();
	}

	public void setStartTime(long startTime) {
		this.setStart(startTime);
	}

	public long getEndTime() {
		return getEnd().longValue();
	}

	public void setEndTime(long endTime) {
		this.setEnd(endTime);
	}

	@Override
	public String toString() {
		return "TimeInterval: [" + new Date(getStartTime()) + "-" + new Date(getEndTime()) + "]";
	}

}
