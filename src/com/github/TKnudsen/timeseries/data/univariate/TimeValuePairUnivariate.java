package com.github.TKnudsen.timeseries.data.univariate;

import com.github.TKnudsen.timeseries.data.ITimeValuePair;

/**
 * <p>
 * Title: TimeValuePairUnivariate
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015-2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class TimeValuePairUnivariate implements ITimeValuePair<Double> {

	private final long timestamp;
	private Double value;

	/**
	 * @param timestamp
	 *            the time stamp
	 * @param value
	 *            the value
	 */
	public TimeValuePairUnivariate(long timestamp, Double value) {
		this.timestamp = timestamp;
		this.value = value;
	}

	/**
	 * @return the time stamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Equals function. The weight has to be equal, too. So be sure that all
	 * weights have the value 1.0 per default.
	 * 
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof ITimeValuePair))
			return false;

		ITimeValuePair<?> tvp = (ITimeValuePair<?>) obj;
		if (timestamp == tvp.getTimestamp() && value == tvp.getValue())
			return true;
		return false;
	}

	@Override
	public String toString() {
		return String.format("[%d, %.3f]", timestamp, value);
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
}
