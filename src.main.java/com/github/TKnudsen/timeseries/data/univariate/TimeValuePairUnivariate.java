package com.github.TKnudsen.timeseries.data.univariate;

import com.github.TKnudsen.timeseries.data.AbstractTimeValuePair;

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
}
