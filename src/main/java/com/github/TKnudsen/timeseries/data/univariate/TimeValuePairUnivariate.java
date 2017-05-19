package com.github.TKnudsen.timeseries.data.univariate;

import com.github.TKnudsen.timeseries.data.primitives.AbstractTimeValuePair;

/**
 * <p>
 * Title: TimeValuePairUnivariate
 * </p>
 * 
 * <p>
 * Description: a tuple consisting of temporal information as key and a number
 * as value. can represent one 'chain' in a time series.
 * 
 * However, our default storage concept for time series consists of two lists of
 * time stamps and values.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public class TimeValuePairUnivariate extends AbstractTimeValuePair<Double> {

	/**
	 * @param timestamp
	 *            the time stamp
	 * @param value
	 *            the value
	 */
	public TimeValuePairUnivariate(long timestamp, Double value) {
		super(timestamp, value);
	}

	@Override
	public TimeValuePairUnivariate clone() {
		return new TimeValuePairUnivariate(timestamp, new Double(value));
	}

	@Override
	public int hashCode() {
		int hashCode = 23;

		hashCode = 31 * hashCode + (int) timestamp;

		if (value == null)
			hashCode = 23 * hashCode;
		else
			hashCode = 23 * hashCode + (int) value.intValue();

		return hashCode;
	}
}
