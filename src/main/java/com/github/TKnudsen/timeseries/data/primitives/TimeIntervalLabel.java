package com.github.TKnudsen.timeseries.data.primitives;

import java.util.Date;

/**
 * <p>
 * Title: TimeIntervalLabel
 * </p>
 * 
 * <p>
 * Description: stores label information for a TimeInterval
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class TimeIntervalLabel<O extends Object> extends TimeInterval implements Comparable<TimeIntervalLabel<O>> {

	private O label;

	private TimeIntervalLabel() {
		super();
	}

	public TimeIntervalLabel(long startTime, long endTime, O label) {
		super(startTime, endTime);
		this.label = label;
	}

	public TimeIntervalLabel(TimeInterval timeInterval, O label) {
		super(timeInterval.getStartTime(), timeInterval.getEndTime());
		this.label = label;
	}

	public O getLabel() {
		return label;
	}

	public void setLabel(O label) {
		this.label = label;
	}

	@Override
	public int compareTo(TimeIntervalLabel<O> o) {
		if (o == null)
			return -1;

		return startTime.compareTo(o.getStartTime());
	}

	@Override
	public String toString() {
		return "TimeInterval: [" + new Date(startTime) + "-" + new Date(endTime) + "] Label: " + label;
	}
}
