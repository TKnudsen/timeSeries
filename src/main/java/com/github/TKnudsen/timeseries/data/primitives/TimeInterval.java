package com.github.TKnudsen.timeseries.data.primitives;

import java.util.Date;

/**
 * <p>
 * Title: TimeInterval
 * </p>
 * 
 * <p>
 * Description: structure that stores a start and an end time stamp
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public class TimeInterval {

	protected Long startTime;
	protected Long endTime;

	protected TimeInterval() {

	}
	
	public TimeInterval(long startTime, long endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public boolean containsTime(long time) {
		if (time >= startTime && time <= endTime)
			return true;
		else
			return false;
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
		hash += (39 * hash + endTime);
		return hash;
	}

	@Override
	public String toString() {
		return "TimeInterval: [" + new Date(startTime) + "-" + new Date(endTime) + "]";
	}
}
