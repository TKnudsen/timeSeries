package com.github.TKnudsen.timeseries.operations.tools;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.github.TKnudsen.timeseries.data.ITimeSeries;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

/**
 * Statistical summary for a univariate time series, extending Apache Commons
 * {@link DescriptiveStatistics}.
 *
 * <p>
 * All NaN values are excluded on construction. Cached values for mean, median,
 * and count are invalidated on {@link #addValue(double)}.
 * </p>
 *
 * <p>
 * <b>Sampling model:</b> treats each time-value pair as an equally weighted
 * sample (sample statistics). For irregular time series where each value should
 * be weighted by the duration of its surrounding interval, use
 * {@link TimeSeriesTools#getMean(ITimeSeries)} instead.
 * </p>
 *
 * <p>
 * Aggregate statistics available: min, max, mean, geometric mean, n, sum, sum
 * of squares, standard deviation, variance, percentiles, skewness, kurtosis,
 * median.
 * </p>
 *
 * <p>
 * TIP: Using an equidistant time series produces the most accurate sample-based
 * statistical information.
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2012-2026
 * </p>
 *
 * @author Juergen Bernard
 * @version 2.0 revised in February 2026
 */
public class TimeSeriesStatistics extends DescriptiveStatistics {

	private static final long serialVersionUID = 4808087357785247611L;

	// Cached values - invalidated on addValue()
	private double cachedMean = Double.NaN;
	private double cachedMedian = Double.NaN;
	private int cachedCount = -1;

	/**
	 * Constructs statistics from a time series. NaN values are excluded.
	 *
	 * @param timeSeries the source time series; must not be null
	 * @throws NullPointerException if timeSeries is null
	 */
	public TimeSeriesStatistics(ITimeSeriesUnivariate timeSeries) {
		if (timeSeries == null)
			throw new NullPointerException("TimeSeriesStatistics: time series was null.");

		for (Double value : timeSeries.getValues())
			if (value != null && !Double.isNaN(value))
				super.addValue(value);
	}

	/**
	 * Constructs statistics from a list of values. NaN and null values are
	 * excluded.
	 *
	 * @param values the source values; must not be null
	 * @throws NullPointerException if values is null
	 */
	public TimeSeriesStatistics(List<Double> values) {
		if (values == null)
			throw new NullPointerException("TimeSeriesStatistics: values list was null.");

		for (Double value : values)
			if (value != null && !Double.isNaN(value))
				super.addValue(value);
	}

	@Override
	public void addValue(double v) {
		invalidateCache();
		super.addValue(v);
	}

	private void invalidateCache() {
		this.cachedMean = Double.NaN;
		this.cachedMedian = Double.NaN;
		this.cachedCount = -1;
	}

	/**
	 * Returns the sample mean. Cached after first computation.
	 *
	 * <p>
	 * Note: this is an equally-weighted sample mean. For irregular time series
	 * requiring duration-weighted means, use
	 * {@link TimeSeriesTools#getMean(ITimeSeries)}.
	 * </p>
	 *
	 * @return the mean, or NaN if no values present
	 */
	@Override
	public double getMean() {
		if (Double.isNaN(cachedMean))
			cachedMean = super.getMean();
		return cachedMean;
	}

	/**
	 * Returns the median (50th percentile). Cached after first computation.
	 *
	 * @return the median, or NaN if no values present
	 */
	public double getMedian() {
		if (Double.isNaN(cachedMedian))
			cachedMedian = super.getPercentile(50.0);
		return cachedMedian;
	}

	/**
	 * Returns the number of non-NaN values. Cached after first computation.
	 *
	 * @return the count of valid values
	 */
	public int getCount() {
		if (cachedCount == -1)
			cachedCount = getValues().length;
		return cachedCount;
	}

	/**
	 * Returns the standard deviation of the sample values.
	 *
	 * <p>
	 * Note: for duration-weighted standard deviation on irregular time series, use
	 * {@link TimeSeriesTools#getStdDeviation(ITimeSeries)}.
	 * </p>
	 *
	 * @return the standard deviation, or NaN if fewer than 2 values present
	 */
	@Override
	public double getStandardDeviation() {
		return super.getStandardDeviation();
	}

	/**
	 * Returns a percentile value. {@code percent} must be in [0, 100].
	 *
	 * @param percent the percentile to retrieve, in [0, 100]
	 * @return the percentile value
	 */
	public double getPercentile(int percent) {
		return super.getPercentile((double) percent);
	}

	/**
	 * Returns a percentile value. {@code percent} must be in [0, 100].
	 *
	 * @param percent the percentile to retrieve, in [0, 100]
	 * @return the percentile value
	 */
	@Override
	public double getPercentile(double percent) {
		return super.getPercentile(percent);
	}

	/**
	 * Returns the linear trend (OLS slope) of this time series in value units per
	 * millisecond. Delegates to
	 * {@link TimeSeriesStatistics#getLinearTrend(List, List)}.
	 *
	 * <p>
	 * Requires that this instance was constructed from an
	 * {@link ITimeSeriesUnivariate} - time stamps are not stored by
	 * {@link DescriptiveStatistics}. Use
	 * {@link TimeSeriesTools#getLinearTrend(ITimeSeries)} directly if you have the
	 * time series available.
	 * </p>
	 *
	 * @return NaN - linear trend requires time stamps; not computable from sample
	 *         values alone. Use {@link TimeSeriesTools#getLinearTrend}
	 */
	public double getLinearTrend() {
		throw new UnsupportedOperationException("getLinearTrend() requires timestamp data. "
				+ "Use TimeSeriesTools.getLinearTrend(ITimeSeriesUnivariate) instead.");
	}
}