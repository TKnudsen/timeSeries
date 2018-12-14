package com.github.TKnudsen.timeseries.data.primitives;

import com.github.TKnudsen.ComplexDataObject.data.interfaces.ISelfDescription;

import java.util.Date;

/**
 * <p>
 * Stores a start and an end time stamp
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public class TimeInterval implements ISelfDescription {

	protected Long startTime;
	protected Long endTime;
	protected String name;
	protected String description;

	protected TimeInterval() {

	}

	public TimeInterval(long startTime, long endTime) {
		this(startTime, endTime, "", "");
	}

	public TimeInterval(long startTime, long endTime, String name, String description) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.name = name;
		this.description = description;
	}

	public boolean containsTime(long time) {
		if (time >= startTime && time <= endTime)
			return true;
		else
			return false;
	}

	public TimeDuration getTimeDuration() {
		return new TimeDuration(TimeQuantization.MILLISECONDS, Math.abs(getEndTime() - getStartTime()));
	}

	public long getDuration() {
		return Math.abs(getEndTime() - getStartTime());
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash += (39 * hash + startTime);
		hash *= (39 * hash + endTime);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (!(obj instanceof TimeInterval))
			return false;

		TimeInterval that = (TimeInterval) obj;
		return this.hashCode() == that.hashCode();
	}

	@Override
	public String toString() {
		return "TimeInterval: [" + new Date(startTime) + "-" + new Date(endTime) + "]";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

}
