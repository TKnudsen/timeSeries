package com.github.TKnudsen.timeseries.data.multivariate;

import java.util.List;

import com.github.TKnudsen.timeseries.data.AbstractTimeValuePair;

public class TimeValuePairMultivariate extends AbstractTimeValuePair<List<Double>> {

	/**
	 * @param timestamp
	 *            the time stamp
	 * @param value
	 *            the value(s)
	 */
	public TimeValuePairMultivariate(long timestamp, List<Double> value) {
		super(timestamp, value);
	}
}
