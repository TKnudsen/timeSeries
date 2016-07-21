package com.github.TKnudsen.timeseries.util;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

/**
 * <p>
 * Title: TimeSeriesStatistics
 * </p>
 * 
 * <p>
 * Description: extension of Apache commons descriptive DescriptiveStatistics.
 * DescriptiveStatistics maintains the input data in memory and has the
 * capability of producing "rolling" statistics computed from a "window"
 * consisting of the most recently added values.
 * 
 * TIP: Using an EquidistantTimeSeries provides most accurate statistical
 * information.
 * 
 * Aggregate Statistics Included: min, max, mean, geometric mean, n, sum, sum of
 * squares, standard deviation, variance, percentiles, skewness, kurtosis,
 * median
 * 
 * "Rolling" capability? Yes
 * 
 * Values stored? Yes
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012-2016
 * </p>
 * 
 * @author Juergen Bernard
 */
public class TimeSeriesStatistics extends DescriptiveStatistics {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4808087357785247611L;

	private double median = Double.NaN;
	private int count = -1;

	/**
	 * NANs are removed!
	 * 
	 * @param vector
	 */
	public TimeSeriesStatistics(ITimeSeriesUnivariate timeSeries) {
		List<Double> values = timeSeries.getValues();
		for (int i = 0; i < values.size(); i++)
			if (!Double.isNaN(values.get(i)))
				addValue((double) values.get(i));
	}

	private void resetValues() {
		this.median = Double.NaN;
		this.count = -1;
	}

	@Override
	public void addValue(double v) {
		resetValues();
		super.addValue(v);
	}

	public double getMedian() {
		if (Double.isNaN(median))
			median = getPercentile(50);
		return median;
	}

	public int getCount() {
		if (count == -1)
			count = getValues().length;
		return count;
	}

	@Override
	public double getMax() {
		return super.getMax();
	}

	@Override
	public double getMin() {
		return super.getMin();
	}

	/**
	 * Must be between 0 and 100
	 * 
	 * @param percent
	 * @return
	 */
	public double getPercentile(int percent) {
		return super.getPercentile((double) percent);
	}

	/**
	 * Must be between 0 and 100
	 * 
	 * @param percent
	 * @return
	 */
	public double getPercentile(double percent) {
		return super.getPercentile((double) percent);
	}
}
