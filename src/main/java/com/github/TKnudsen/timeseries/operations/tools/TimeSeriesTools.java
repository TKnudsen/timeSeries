package com.github.TKnudsen.timeseries.operations.tools;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.github.TKnudsen.ComplexDataObject.data.time.TimeDuration;
import com.github.TKnudsen.ComplexDataObject.data.time.TimeQuantization;
import com.github.TKnudsen.ComplexDataObject.model.tools.DataConversion;
import com.github.TKnudsen.ComplexDataObject.model.tools.DateTools;
import com.github.TKnudsen.ComplexDataObject.model.tools.MathFunctions;
import com.github.TKnudsen.ComplexDataObject.model.tools.StatisticsSupport;
import com.github.TKnudsen.ComplexDataObject.model.weighting.Long.LinearLongWeightingKernel;
import com.github.TKnudsen.timeseries.data.ITimeSeries;
import com.github.TKnudsen.timeseries.data.ITimeValuePair;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.multivariate.TimeValuePairMultivariate;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariateFactory;
import com.github.TKnudsen.timeseries.data.univariate.TimeValuePairUnivariate;
import com.github.TKnudsen.timeseries.operations.prediction.TimeSeriesPrediction;

/**
 * Utility class providing static methods for analysis and transformation of
 * uni-variate time series.
 *
 * <p>
 * Covers:
 * </p>
 * <ul>
 * <li>Statistical aggregation: mean, variance, quantiles, counts</li>
 * <li>Interpolation and extrapolation</li>
 * <li>Segmentation and subsequence extraction</li>
 * <li>Transformation: change rate, logarithm-like scaling, clamping</li>
 * <li>Prediction with uncertainty estimation</li>
 * </ul>
 *
 * <p>
 * Null handling: all methods throw {@link NullPointerException} for null series
 * inputs. Null and NaN values within a series are treated as missing data and
 * handled per-method as documented.
 * </p>
 *
 * <p>
 * Thread safety: all methods are state-less and safe for concurrent use.
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2015-2026
 * </p>
 *
 * @author Juergen Bernard
 * @version 2.0 revised February 2026
 */
public final class TimeSeriesTools {

	public static final Long YEAR_IN_MILLISECONDS = 31556952000L; // 365.2425 days a solar year

	private TimeSeriesTools() {
	}

	/**
	 * Returns the minimum first time stamp across all non-null, non-empty series in
	 * the collection.
	 *
	 * <p>
	 * Null and empty series within the collection are silently skipped.
	 * </p>
	 *
	 * @param timeSeries the collection of time series to scan; must not be null
	 * @return the minimum first time stamp, or {@code Long.MAX_VALUE} if no valid
	 *         series exist
	 * @throws NullPointerException if timeSeries is null
	 */
	public static long getMinStart(Collection<ITimeSeriesUnivariate> timeSeries) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		long min = Long.MAX_VALUE;

		for (ITimeSeries<Double> ts : timeSeries) {
			if (ts == null || ts.isEmpty())
				continue;
			min = Math.min(min, ts.getFirstTimestamp());
		}

		return min;
	}

	/**
	 * Returns the minimum non-NaN value across all non-null, non-empty series in
	 * the collection.
	 *
	 * <p>
	 * Null elements within the collection are silently skipped. Series where all
	 * values are NaN contribute nothing to the result.
	 * </p>
	 *
	 * @param timeSeriesList the collection of time series to scan; must not be null
	 * @return the minimum value, or {@code Double.NaN} if no valid values exist
	 * @throws NullPointerException if timeSeriesList is null
	 */
	public static Double getMinValue(Collection<ITimeSeriesUnivariate> timeSeriesList) {
		Objects.requireNonNull(timeSeriesList, "timeSeriesList must not be null");

		double min = Double.POSITIVE_INFINITY;

		for (ITimeSeriesUnivariate timeSeries : timeSeriesList) {
			if (timeSeries == null || timeSeries.isEmpty())
				continue;
			double candidate = getMinValue(timeSeries);
			if (!Double.isNaN(candidate))
				min = Math.min(min, candidate);
		}

		return Double.isInfinite(min) ? Double.NaN : min;
	}

	/**
	 * Returns the minimum non-NaN value in the time series.
	 *
	 * @param timeSeries the time series to scan; must not be null or empty
	 * @return the minimum value, or {@code Double.NaN} if all values are NaN
	 * @throws NullPointerException  if timeSeries is null
	 * @throws IllegalStateException if timeSeries is empty
	 */
	public static double getMinValue(ITimeSeries<? extends Double> timeSeries) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");
		if (timeSeries.isEmpty())
			throw new IllegalStateException("TimeSeries is empty");

		double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < timeSeries.size(); i++) {
			double v = timeSeries.getValue(i);
			if (!Double.isNaN(v) && v < min)
				min = v;
		}
		return Double.isInfinite(min) ? Double.NaN : min;
	}

	/**
	 * Returns the maximum last time stamp across all non-null, non-empty series in
	 * the collection.
	 *
	 * <p>
	 * Null and empty series within the collection are silently skipped.
	 * </p>
	 *
	 * @param timeSeries the collection of time series to scan; must not be null
	 * @return the maximum last time stamp, or {@code Long.MIN_VALUE} if no valid
	 *         series exist
	 * @throws NullPointerException if timeSeries is null
	 */
	public static long getMaxEnd(Collection<ITimeSeriesUnivariate> timeSeries) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		long max = Long.MIN_VALUE;

		for (ITimeSeries<Double> ts : timeSeries) {
			if (ts == null || ts.isEmpty())
				continue;
			max = Math.max(max, ts.getLastTimestamp());
		}

		return max;
	}

	/**
	 * Returns the maximum non-NaN value across all non-null, non-empty series in
	 * the collection.
	 *
	 * <p>
	 * Null and empty series within the collection are silently skipped. Series
	 * where all values are NaN contribute nothing to the result.
	 * </p>
	 *
	 * @param timeSeriesList the collection of time series to scan; must not be null
	 * @return the maximum value, or {@code Double.NaN} if no valid values exist
	 * @throws NullPointerException if timeSeriesList is null
	 */
	public static Double getMaxValue(Collection<ITimeSeriesUnivariate> timeSeriesList) {
		Objects.requireNonNull(timeSeriesList, "timeSeriesList must not be null");

		double max = Double.NEGATIVE_INFINITY;

		for (ITimeSeriesUnivariate timeSeries : timeSeriesList) {
			if (timeSeries == null || timeSeries.isEmpty())
				continue;
			double candidate = getMaxValue(timeSeries);
			if (!Double.isNaN(candidate))
				max = Math.max(max, candidate);
		}

		return Double.isInfinite(max) ? Double.NaN : max;
	}

	/**
	 * Returns the maximum non-NaN value in the time series.
	 *
	 * @param timeSeries the time series to scan; must not be null or empty
	 * @return the maximum value, or {@code Double.NaN} if all values are NaN
	 * @throws NullPointerException  if timeSeries is null
	 * @throws IllegalStateException if timeSeries is empty
	 */
	public static double getMaxValue(ITimeSeries<? extends Double> timeSeries) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");
		if (timeSeries.isEmpty())
			throw new IllegalStateException("TimeSeries is empty");

		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < timeSeries.size(); i++) {
			double v = timeSeries.getValue(i);
			if (!Double.isNaN(v) && v > max)
				max = v;
		}
		return Double.isInfinite(max) ? Double.NaN : max;
	}

	/**
	 * calculates the mean/average of a time series. With the flag
	 * 'weightIncreaseOverTime', it is possible to weight later time stamps stronger
	 * than earlier ones. In this way a trend-like tendency can be inferred in a
	 * single value.
	 * 
	 * Attention: ignores null and NaN values in the time series.
	 * 
	 * @param timeSeries                     time series
	 * @param weightIncreaseOverTimeAdditive additive way to add more weight to
	 *                                       later time stamps. 0 means no increase
	 * @return double
	 */
	public static double getMeanWeighted(ITimeSeries<? extends Double> timeSeries,
			double weightIncreaseOverTimeAdditive) {

		return getMeanWeighted(timeSeries, weightIncreaseOverTimeAdditive, 1.0);
	}

	/**
	 * Computes the weighted mean of a time series, with optional weight increase
	 * over time to emphasize recent values.
	 *
	 * <p>
	 * Two complementary weighting modes are supported and applied together:
	 * </p>
	 * <ul>
	 * <li><b>Additive</b> ({@code weightIncreaseOverTimeAdditive}): increments the
	 * weight by a fixed amount at each step. Use {@code 0} to disable. Suitable for
	 * short series.</li>
	 * <li><b>Multiplicative</b> ({@code weightIncreaseOverTimeMultiplicative}):
	 * scales the weight by a fixed factor at each step. Use {@code 1} to disable.
	 * Suitable for long or high-frequency series where additive weighting has
	 * negligible effect.</li>
	 * </ul>
	 *
	 * <p>
	 * Null and NaN values are ignored and do not contribute to the result. Returns
	 * {@code Double.NaN} if all values are null or NaN.
	 * </p>
	 *
	 * @param timeSeries                           the time series to average; must
	 *                                             not be null
	 * @param weightIncreaseOverTimeAdditive       additive weight increment per
	 *                                             step; must be &ge; 0 and not NaN
	 * @param weightIncreaseOverTimeMultiplicative multiplicative weight factor per
	 *                                             step; must be &gt; 0 and not NaN
	 * @return the weighted mean, or {@code Double.NaN} if no valid values exist
	 * @throws NullPointerException     if timeSeries is null
	 * @throws IllegalArgumentException if either weight parameter is invalid
	 */
	public static double getMeanWeighted(ITimeSeries<? extends Double> timeSeries,
			double weightIncreaseOverTimeAdditive, double weightIncreaseOverTimeMultiplicative) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		if (Double.isNaN(weightIncreaseOverTimeAdditive) || weightIncreaseOverTimeAdditive < 0)
			throw new IllegalArgumentException(
					"weightIncreaseOverTimeAdditive must be >= 0, was " + weightIncreaseOverTimeAdditive);

		if (Double.isNaN(weightIncreaseOverTimeMultiplicative) || weightIncreaseOverTimeMultiplicative <= 0)
			throw new IllegalArgumentException(
					"weightIncreaseOverTimeMultiplicative must be > 0, was " + weightIncreaseOverTimeMultiplicative);

		double w = 1.0;
		double wSum = 0.0;
		double sum = 0.0;

		for (int i = 0; i < timeSeries.size(); i++) {
			Double v = timeSeries.getValue(i);
			if (v != null && !Double.isNaN(v)) {
				sum += v * w;
				wSum += w;
			}
			w *= weightIncreaseOverTimeMultiplicative;
			w += weightIncreaseOverTimeAdditive;
		}

		return wSum == 0 ? Double.NaN : sum / wSum; // NaN if wSum == 0 (all values null or NaN)
	}

	/**
	 * Computes the time-weighted mean of a time series using the trapezoidal
	 * (midpoint interval) method.
	 *
	 * <p>
	 * Each value is weighted by half the temporal distance to its neighbors:
	 * {@code localWeight = (t[i] - t[i-1]) / 2 + (t[i+1] - t[i]) / 2}. This
	 * correctly handles irregularly sampled series.
	 * </p>
	 *
	 * <p>
	 * Null values, NaN values, and values matching the series missing value
	 * indicator are ignored and contribute neither weight nor sum. Returns
	 * {@code Double.NaN} if no valid values exist or if the series has fewer than
	 * two valid neighbours (zero total weight).
	 * </p>
	 *
	 * @param timeSeries the time series to average; must not be null
	 * @return the time-weighted mean, or {@code Double.NaN} if undefined
	 * @throws NullPointerException if timeSeries is null
	 */
	public static double getMean(ITimeSeries<? extends Double> timeSeries) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		if (timeSeries.isEmpty())
			return Double.NaN;

		// single point has no temporal neighbors - weight is undefined
		if (timeSeries.size() == 1)
			return timeSeries.getValue(0) != null ? timeSeries.getValue(0) : Double.NaN;

		Double missingValueIndicator = timeSeries.getMissingValueIndicator();

		double globalLength = 0.0;
		double weightedSum = 0.0;

		for (int i = 0; i < timeSeries.size(); i++) {
			Double v = timeSeries.getValue(i);

			if (v == null || Double.isNaN(v))
				continue;
			if (missingValueIndicator != null && MathFunctions.compareDoubles(v, missingValueIndicator))
				continue;

			double localLength = 0.0;
			if (i > 0)
				localLength += (timeSeries.getTimestamp(i) - timeSeries.getTimestamp(i - 1)) / 2.0;
			if (i < timeSeries.size() - 1)
				localLength += (timeSeries.getTimestamp(i + 1) - timeSeries.getTimestamp(i)) / 2.0;

			globalLength += localLength;
			weightedSum += v * localLength;
		}

		return weightedSum / globalLength; // NaN if globalLength == 0
	}

	/**
	 * Computes the time-weighted variance of a time series using the trapezoidal
	 * (midpoint interval) method.
	 *
	 * <p>
	 * Each squared deviation is weighted by half the temporal distance to its
	 * neighbors: {@code localWeight = (t[i] - t[i-1]) / 2 + (t[i+1] - t[i]) / 2}.
	 * This correctly handles irregularly sampled series.
	 * </p>
	 *
	 * <p>
	 * Null values, NaN values, and values matching the series missing value
	 * indicator are ignored. Returns {@code Double.NaN} if no valid values exist or
	 * total weight is zero.
	 * </p>
	 *
	 * @param timeSeries the time series to compute variance for; must not be null
	 * @return the time-weighted variance, or {@code Double.NaN} if undefined
	 * @throws NullPointerException if timeSeries is null
	 */
	public static double getVariance(ITimeSeries<Double> timeSeries) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		if (timeSeries.isEmpty())
			return Double.NaN;

		double mean = getMean(timeSeries);
		if (Double.isNaN(mean))
			return Double.NaN;

		Double missingValueIndicator = timeSeries.getMissingValueIndicator();

		double globalLength = 0.0;
		double variance = 0.0;

		for (int i = 0; i < timeSeries.size(); i++) {
			Double v = timeSeries.getValue(i);

			if (v == null || Double.isNaN(v))
				continue;
			if (missingValueIndicator != null && MathFunctions.compareDoubles(v, missingValueIndicator))
				continue;

			double localLength = 0.0;
			if (i > 0)
				localLength += (timeSeries.getTimestamp(i) - timeSeries.getTimestamp(i - 1)) / 2.0;
			if (i < timeSeries.size() - 1)
				localLength += (timeSeries.getTimestamp(i + 1) - timeSeries.getTimestamp(i)) / 2.0;

			variance += Math.pow(v - mean, 2) * localLength;
			globalLength += localLength;
		}

		if (globalLength == 0.0)
			return Double.NaN;

		return variance / globalLength;
	}

	/**
	 * Computes the time-weighted standard deviation of a time series.
	 *
	 * <p>
	 * Delegates to {@link #getVariance(ITimeSeries)} and returns its square root.
	 * Null, NaN, and missing-value-indicator entries are ignored.
	 * </p>
	 *
	 * <p>
	 * Returns {@code Double.NaN} if the variance is undefined.
	 * </p>
	 *
	 * @param timeSeries the time series to compute standard deviation for; must not
	 *                   be null
	 * @return the time-weighted standard deviation, or {@code Double.NaN} if
	 *         undefined
	 * @throws NullPointerException if timeSeries is null
	 */
	public static double getStdDeviation(ITimeSeries<Double> timeSeries) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		return Math.sqrt(getVariance(timeSeries));
	}

	/**
	 * Calculates the linear trend (slope) of a time series using ordinary least
	 * squares regression. Returns absolute values in units per millisecond.
	 *
	 * <p>
	 * The trend represents the rate of absolute change in the value domain per
	 * millisecond. Convert to other time units by multiplying:
	 * </p>
	 * <ul>
	 * <li>Per second: x 1,000</li>
	 * <li>Per day: x 86,400,000</li>
	 * <li>Per year: x 31,556,952,000</li>
	 * </ul>
	 *
	 * <p>
	 * Express as a percentage by dividing by the mean value - only meaningful when
	 * the mean is not close to zero.
	 * </p>
	 *
	 * <p>
	 * <b>Algorithm:</b> Ordinary Least Squares (OLS):
	 * </p>
	 *
	 * <pre>
	 * slope = sum((t_i - t_mean) * (v_i - v_mean)) / sum((t_i - t_mean)^2)
	 * </pre>
	 *
	 * <p>
	 * <b>Example:</b>
	 * </p>
	 *
	 * <pre>
	 * [(0ms: 10.0), (1000ms: 20.0), (2000ms: 30.0)]  - to slope ~= 0.01 units/ms
	 * Annual trend: slope x 1000 x 60 x 60 x 24 x 365.2425
	 * </pre>
	 *
	 * @param timeSeries the time series; must not be null
	 * @return the linear trend in units per millisecond, or {@code Double.NaN} if:
	 *         fewer than 2 valid data points exist, all timestamps are identical,
	 *         or all values are null or NaN
	 * @throws NullPointerException if timeSeries is null
	 */
	public static double getLinearTrend(ITimeSeries<Double> timeSeries) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		if (timeSeries.isEmpty())
			return Double.NaN;

		Double missingValueIndicator = timeSeries.getMissingValueIndicator();

		// first pass: collect valid points and accumulate means
		int n = timeSeries.size();
		double[] ts = new double[n];
		double[] vs = new double[n];
		int count = 0;

		for (int i = 0; i < n; i++) {
			Double v = timeSeries.getValue(i);
			if (v == null || Double.isNaN(v))
				continue;
			if (missingValueIndicator != null && MathFunctions.compareDoubles(v, missingValueIndicator))
				continue;
			ts[count] = timeSeries.getTimestamp(i);
			vs[count] = v;
			count++;
		}

		if (count <= 1)
			return Double.NaN;

		// compute means in a single pass over valid points
		double timeMean = 0.0;
		double valueMean = 0.0;
		for (int i = 0; i < count; i++) {
			timeMean += ts[i];
			valueMean += vs[i];
		}
		timeMean /= count;
		valueMean /= count;

		// OLS slope
		double sumDxy = 0.0;
		double sumDxx = 0.0;
		for (int i = 0; i < count; i++) {
			double dt = ts[i] - timeMean;
			sumDxy += dt * (vs[i] - valueMean);
			sumDxx += dt * dt;
		}

		if (sumDxx == 0.0)
			return Double.NaN;

		return sumDxy / sumDxx;
	}

	/**
	 * Replaces each value in {@code timeSeries} with a time-sensitive linear
	 * weighted moving average over a symmetric window of {@code window}
	 * milliseconds, considering both past and future values.
	 *
	 * <p>
	 * Convenience overload of
	 * {@link #calculateMovingAverageTimeSensitive(ITimeSeries, long, boolean)} with
	 * {@code considerFutureValues = true}.
	 * </p>
	 *
	 * @param timeSeries the series to smooth in place; must not be null
	 * @param window     half-window size in milliseconds
	 * @throws NullPointerException if timeSeries is null
	 */
	public static void calculateMovingAverageTimeSensitive(ITimeSeries<Double> timeSeries, long window) {
		calculateMovingAverageTimeSensitive(timeSeries, window, true);
	}

	/**
	 * Replaces each value in {@code timeSeries} with a time-sensitive linear
	 * weighted moving average over a symmetric window of {@code window}
	 * milliseconds.
	 *
	 * <p>
	 * Values are weighted by proximity to the reference time stamp using a
	 * {@link LinearLongWeightingKernel}. If {@code considerFutureValues} is false,
	 * only time stamps up to and including the reference are used.
	 * </p>
	 *
	 * <p>
	 * Positions where all neighbors are NaN or have zero total weight are set to
	 * {@code Double.NaN}.
	 * </p>
	 *
	 * <p>
	 * A two-pass approach is used: all smoothed values are computed first, then
	 * written back - preventing already-replaced values from contaminating
	 * subsequent computations.
	 * </p>
	 *
	 * @param timeSeries           the series to smooth in place; must not be null
	 * @param window               half-window size in milliseconds
	 * @param considerFutureValues if false, only past and present values contribute
	 * @throws NullPointerException if timeSeries is null
	 */
	public static void calculateMovingAverageTimeSensitive(ITimeSeries<Double> timeSeries, long window,
			boolean considerFutureValues) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		if (timeSeries.isEmpty())
			return;

		Double missingValueIndicator = timeSeries.getMissingValueIndicator();
		LinearLongWeightingKernel kernel = new LinearLongWeightingKernel(window);
		long firstTimestamp = timeSeries.getFirstTimestamp();
		long lastTimestamp = timeSeries.getLastTimestamp();

		List<Double> smoothed = new ArrayList<>(timeSeries.size());

		for (int i = 0; i < timeSeries.size(); i++) {
			long referenceTimestamp = timeSeries.getTimestamp(i);
			kernel.setReference(referenceTimestamp);

			long windowStart = Math.max(referenceTimestamp - kernel.getInterval(), firstTimestamp);
			long windowEnd = Math.min(referenceTimestamp + kernel.getInterval(), lastTimestamp);

			int firstIndex = Math.max(timeSeries.findByDate(windowStart, false), 0);
			int lastIndex = Math.min(timeSeries.findByDate(windowEnd, false), timeSeries.size() - 1);

			int effectiveLastIndex = considerFutureValues ? lastIndex : Math.min(lastIndex, i);

			double weightedSum = 0.0;
			double weightSum = 0.0;

			for (int k = firstIndex; k <= effectiveLastIndex; k++) {
				Double v = timeSeries.getValue(k);
				if (v == null || Double.isNaN(v))
					continue;
				if (missingValueIndicator != null && MathFunctions.compareDoubles(v, missingValueIndicator))
					continue;
				double w = kernel.getWeight(timeSeries.getTimestamp(k));
				weightedSum += v * w;
				weightSum += w;
			}

			smoothed.add(weightSum > 0 ? weightedSum / weightSum : Double.NaN);
		}

		for (int i = 0; i < timeSeries.size(); i++)
			timeSeries.replaceValue(i, smoothed.get(i));
	}

	/**
	 * Converts a {@link List} of {@link Double} values to a {@code double[]}
	 * primitive array.
	 *
	 * <p>
	 * Delegates to {@link DataConversion#toPrimitives(Collection)}.
	 * </p>
	 *
	 * <p>
	 * The list must not contain {@code null} elements; a
	 * {@code NullPointerException} will be thrown during iteration if it does.
	 * </p>
	 *
	 * @param values the list to convert; must not be null
	 * @return a {@code double[]} of the same size and order as the input
	 * @throws NullPointerException if values is null or contains null elements
	 */
	public static double[] getArray(List<Double> values) {
		Objects.requireNonNull(values, "values must not be null");

		return DataConversion.toPrimitives(values);
	}

	/**
	 * Returns the {@link ITimeValuePair} at the given index in the time series.
	 *
	 * @param timeSeries the time series to query; must not be null
	 * @param index      the index to retrieve; must be in [0, size - 1]
	 * @return the time-value pair at {@code index}
	 * @throws NullPointerException      if timeSeries is null
	 * @throws IndexOutOfBoundsException if index is out of range
	 */
	public static ITimeValuePair<Double> getTimeValuePair(ITimeSeries<Double> timeSeries, int index) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		if (index < 0 || index >= timeSeries.size())
			throw new IndexOutOfBoundsException("index " + index + " out of bounds for size " + timeSeries.size());

		return new TimeValuePairUnivariate(timeSeries.getTimestamp(index), timeSeries.getValue(index));
	}

	/**
	 * Returns the {@link ITimeValuePair} for the given time stamp in the time
	 * series.
	 *
	 * <p>
	 * If no exact match exists for {@code timestamp}, the value will be
	 * {@code null} - the pair is still returned with the requested time stamp and a
	 * null value.
	 * </p>
	 *
	 * @param timeSeries the time series to query; must not be null and must not be
	 *                   empty
	 * @param timestamp  the time stamp to look up
	 * @return the time-value pair at {@code timestamp}; value may be {@code null}
	 *         if no exact match exists
	 * @throws NullPointerException  if timeSeries is null
	 * @throws IllegalStateException if timeSeries is empty
	 */
	public static ITimeValuePair<Double> getTimeValuePair(ITimeSeries<Double> timeSeries, long timestamp) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		if (timeSeries.isEmpty())
			throw new IllegalStateException("timeSeries must not be empty");

		Double value = timeSeries.getValue(timestamp, false);
		return new TimeValuePairUnivariate(timestamp, value);
	}

	/**
	 * Returns all time-value pairs in the time series as a {@link List}, in
	 * chronological order.
	 *
	 * <p>
	 * Values may be {@code null} for entries where no value is recorded; these are
	 * preserved as-is in the returned pairs.
	 * </p>
	 *
	 * @param timeSeries the time series to convert; must not be null
	 * @return a mutable {@link List} of all time-value pairs; empty if the series
	 *         is empty
	 * @throws NullPointerException if timeSeries is null
	 */
	public static List<ITimeValuePair<Double>> getTimeValuePairs(ITimeSeries<Double> timeSeries) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		int size = timeSeries.size();
		List<ITimeValuePair<Double>> list = new ArrayList<>(size);

		for (int i = 0; i < size; i++)
			list.add(new TimeValuePairUnivariate(timeSeries.getTimestamp(i), timeSeries.getValue(i)));

		return list;
	}

	/**
	 * Returns the {@link ITimeValuePair} at the given index in the multivariate
	 * time series.
	 *
	 * @param timeSeries the time series to query; must not be null
	 * @param index      the index to retrieve; must be in [0, size - 1]
	 * @return the time-value pair at {@code index}
	 * @throws NullPointerException      if timeSeries is null
	 * @throws IndexOutOfBoundsException if index is out of range
	 */
	public static ITimeValuePair<List<Double>> getTimeValuePair(ITimeSeriesMultivariate timeSeries, int index) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		if (index < 0 || index >= timeSeries.size())
			throw new IndexOutOfBoundsException("index " + index + " out of bounds for size " + timeSeries.size());

		return new TimeValuePairMultivariate(timeSeries.getTimestamp(index), timeSeries.getValue(index));
	}

	/**
	 * Returns all time-value pairs in the multivariate time series as a
	 * {@link List} of {@link Entry} instances, in chronological order.
	 *
	 * <p>
	 * Each entry maps a time stamp to the list of dimension values at that
	 * position. Value lists may contain {@code null} elements for dimensions where
	 * no value is recorded.
	 * </p>
	 *
	 * @param timeSeries the time series to convert; must not be null
	 * @return a mutable {@link List} of all time-value entries; empty if the series
	 *         is empty
	 * @throws NullPointerException if timeSeries is null
	 */
	public static List<Entry<Long, List<Double>>> getTimeValueLists(ITimeSeriesMultivariate timeSeries) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		int size = timeSeries.size();
		List<Entry<Long, List<Double>>> pairs = new ArrayList<>(size);

		for (int i = 0; i < size; i++)
			pairs.add(new AbstractMap.SimpleEntry<>(timeSeries.getTimestamp(i), timeSeries.getValue(i)));

		return pairs;
	}

	/**
	 * Creates a {@link TimeSeriesUnivariate} from a collection of
	 * {@link ITimeValuePair} instances.
	 *
	 * <p>
	 * Pairs are added in the iteration order of the collection. Null pairs within
	 * the collection will throw {@link NullPointerException} during iteration.
	 * </p>
	 *
	 * @param timeValuePairs        the pairs to build the series from; must not be
	 *                              null
	 * @param missingValueIndicator the value used to represent missing data; may be
	 *                              {@code null} or {@code Double.NaN}
	 * @return a new {@link TimeSeriesUnivariate} containing all pairs
	 * @throws NullPointerException if timeValuePairs is null or contains null pairs
	 * @deprecated use TimeSeriesUnivariateFactory.newTimeSeries directly.
	 */
	public static ITimeSeriesUnivariate createTimeSeries(Collection<ITimeValuePair<Double>> timeValuePairs,
			Double missingValueIndicator) {
		return TimeSeriesUnivariateFactory.newTimeSeries(timeValuePairs, missingValueIndicator);
	}

	/**
	 * Creates a time series with a constant value at each of the given time stamps,
	 * for example to represent a horizontal threshold line.
	 *
	 * <p>
	 * The time stamp list is copied and sorted ascending - the original list is not
	 * modified.
	 * </p>
	 *
	 * @param timeStamps    the time stamps for the series; must not be null or
	 *                      empty
	 * @param constantValue the value assigned to every time stamp
	 * @return a new {@link ITimeSeriesUnivariate} with {@code constantValue} at
	 *         each time stamp; never null
	 * @throws NullPointerException     if timeStamps is null
	 * @throws IllegalArgumentException if timeStamps is empty or contains duplicate
	 *                                  time stamps
	 * @deprecated use TimeSeriesUnivariateFactory.createConstantTimeSeries
	 *             directly.
	 */
	public static ITimeSeriesUnivariate createConstantTimeSeries(List<Long> timeStamps, double constantValue) {
		return TimeSeriesUnivariateFactory.createConstantTimeSeries(timeStamps, constantValue);
	}

	/**
	 * Returns the intervals between consecutive time stamps as a {@code long[]}
	 * array in milliseconds.
	 *
	 * <p>
	 * For a series of size {@code n}, the returned array has length {@code n - 1},
	 * where {@code result[i] = timestamp[i+1] - timestamp[i]}. Returns an empty
	 * array for a series with fewer than 2 entries.
	 * </p>
	 *
	 * @param timeSeries the series to measure; must not be null
	 * @return array of consecutive time stamp intervals in milliseconds; never
	 *         null, length {@code max(0, size - 1)}
	 * @throws NullPointerException if timeSeries is null
	 */
	public static long[] getQuantizationsAsLong(ITimeSeries<Double> timeSeries) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		int n = timeSeries.size();
		if (n < 2)
			return new long[0];

		long[] quantization = new long[n - 1];
		for (int i = 0; i < quantization.length; i++)
			quantization[i] = timeSeries.getTimestamp(i + 1) - timeSeries.getTimestamp(i);

		return quantization;
	}

	/**
	 * Returns the intervals between consecutive time stamps as a {@code double[]}
	 * array in milliseconds.
	 *
	 * <p>
	 * Convenience wrapper around {@link #getQuantizationsAsLong(ITimeSeries)} for
	 * contexts requiring floating-point precision.
	 * </p>
	 *
	 * <p>
	 * For a series of size {@code n}, the returned array has length {@code n - 1}.
	 * Returns an empty array for a series with fewer than 2 entries.
	 * </p>
	 *
	 * @param timeSeries the series to measure; must not be null
	 * @return array of consecutive time stamp intervals in milliseconds; never
	 *         null, length {@code max(0, size - 1)}
	 * @throws NullPointerException if timeSeries is null
	 */
	public static double[] getQuantizationAsDouble(ITimeSeries<Double> timeSeries) {
		long[] longs = getQuantizationsAsLong(timeSeries);
		double[] result = new double[longs.length];
		for (int i = 0; i < longs.length; i++)
			result[i] = longs[i];
		return result;
	}

	public static TimeQuantization calculateSuitableTimeQuantization(long equidistanceInMillis) {
		// Milliseconds?
		if (equidistanceInMillis < 1000)
			return TimeQuantization.MILLISECONDS;
		else {
			equidistanceInMillis /= 1000;
			// Seconds?
			if (equidistanceInMillis < 60)
				return TimeQuantization.SECONDS;
			else {
				equidistanceInMillis /= 60;
				// Minutes?
				if (equidistanceInMillis < 60)
					return TimeQuantization.MINUTES;
				else {
					equidistanceInMillis /= 60;
					// Hours?
					if (equidistanceInMillis < 24)
						return TimeQuantization.HOURS;
					else {
						equidistanceInMillis /= 24;
						// Days?
						if (equidistanceInMillis < 365)
							return TimeQuantization.DAYS;
						else
							return TimeQuantization.YEARS;
					}
				}
			}
		}
	}

	/**
	 * finds the flat date/time after the start time according to the given time
	 * series interval. if the TimeQuantization of the patternInterval is at least
	 * of one day length, the next 00:00:00 GMT time is achieved
	 * 
	 * @param startTime       start
	 * @param patternInterval interval
	 * @return date
	 */
	public static Date getDateAfterStartTimeAccordingToPatternInterval(long startTime, TimeDuration patternInterval) {

		if (patternInterval.getDuration() == 0) {
			return null;
		}
		long mod = startTime % calculateEquidistanceInMillis(patternInterval);
		if (mod != 0)
			startTime += (calculateEquidistanceInMillis(patternInterval) - mod);
		Date date = new Date(startTime);
		return date;
	}

	public static long calculateEquidistanceInMillis(TimeDuration equidistance) {
		long factor = equidistance.getTypeFactor();
		switch (equidistance.getType()) {
		case MILLISECONDS:
			return factor;
		case SECONDS:
			return factor * 1_000L;
		case MINUTES:
			return factor * 60_000L;
		case HOURS:
			return factor * 3_600_000L;
		case DAYS:
			return factor * 86_400_000L;
		case WEEKS:
			return factor * 604_800_000L;
		case MONTHS:
			return factor * 2_592_000_000L; // 30 days
		case QUARTER:
			return factor * 10_368_000_000L; // 120 days
		case YEARS:
			return factor * 31_536_000_000L; // 365 days
		case DECADES:
			return factor * 315_360_000_000L;
		default:
			throw new IllegalArgumentException("Unknown TimeQuantization: " + equidistance.getType());
		}

//		long equidistanceInMillis = equidistance.getTypeFactor();
//		if (equidistance.getType().equals(TimeQuantization.MILLISECONDS)) {
//		} else {
//			equidistanceInMillis *= 1000;
//			if (equidistance.getType().equals(TimeQuantization.SECONDS)) {
//			} else {
//				equidistanceInMillis *= 60;
//				if (equidistance.getType().equals(TimeQuantization.MINUTES)) {
//				} else {
//					equidistanceInMillis *= 60;
//					if (equidistance.getType().equals(TimeQuantization.HOURS)) {
//					} else {
//						equidistanceInMillis *= 24;
//						if (equidistance.getType().equals(TimeQuantization.DAYS)) {
//						} else if (equidistance.getType().equals(TimeQuantization.WEEKS)) {
//							equidistanceInMillis *= 7;
//						} else if (equidistance.getType().equals(TimeQuantization.MONTHS)) {
//							equidistanceInMillis *= 30;
//						} else if (equidistance.getType().equals(TimeQuantization.QUARTER)) {
//							equidistanceInMillis *= 120;
//						} else if (equidistance.getType().equals(TimeQuantization.YEARS)) {
//							equidistanceInMillis *= 365;
//						} else if (equidistance.getType().equals(TimeQuantization.DECADES)) {
//							equidistanceInMillis *= 3650;
//						}
//					}
//				}
//			}
//		}
//		return equidistanceInMillis;
	}

	/**
	 * Creates a deep clone of the given uni-variate time series, preserving time
	 * stamps, values, missing value indicator, name, description, metadata
	 * attributes, and - if present - event and interval labels.
	 *
	 * @param timeSeries the time series to clone; must not be null
	 * @return a new independent {@link ITimeSeriesUnivariate} equal to the input
	 * @throws NullPointerException if timeSeries is null
	 */
	public static ITimeSeriesUnivariate cloneTimeSeries(ITimeSeriesUnivariate timeSeries) {
		return TimeSeriesUnivariateFactory.clone(timeSeries);
	}

	/**
	 * Returns the two time stamps in the series that immediately bracket
	 * {@code time} - the largest time stamp &le; {@code time} and the smallest time
	 * stamp &ge; {@code time}.
	 *
	 * <p>
	 * If {@code time} exactly matches a time stamp, both returned values are equal
	 * to that time stamp.
	 * </p>
	 *
	 * <p>
	 * Returns an empty list if {@code time} is outside the series range
	 * {@code [firsttime stamp, lasttime stamp]}.
	 * </p>
	 *
	 * @param timeSeries the series to search; must not be null or empty
	 * @param time       the target time stamp in milliseconds since epoch
	 * @return a list of two bracketing time stamps, or an empty list if
	 *         {@code time} is outside the series range
	 * @throws NullPointerException  if timeSeries is null
	 * @throws IllegalStateException if timeSeries is empty
	 */
	public static List<Long> getNearestTimeStampNeighbors(ITimeSeries<Double> timeSeries, long time) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");
		if (timeSeries.isEmpty())
			throw new IllegalStateException("TimeSeriesTools.getNearestTimeStampNeighbors: series is empty");

		long first = timeSeries.getFirstTimestamp();
		long last = timeSeries.getLastTimestamp();

		if (time < first || time > last)
			return Collections.emptyList();

		// exact match - both neighbors are the same time stamp
		if (timeSeries.containsTimestamp(time)) {
			List<Long> result = new ArrayList<>(2);
			result.add(time);
			result.add(time);
			return result;
		}

		// floor index - O(log n) via binary search in TimeSeries base class
		int floorIdx = timeSeries.findByDate(time, false);
		List<Long> result = new ArrayList<>(2);
		result.add(timeSeries.getTimestamp(floorIdx));
		result.add(timeSeries.getTimestamp(floorIdx + 1));
		return result;
	}

	/**
	 * Returns a linearly interpolated value at {@code timeStamp} using the values
	 * at {@code leftTimeStamp} and {@code rightTimeStamp} as anchors.
	 *
	 * <p>
	 * The interpolation formula is:
	 * </p>
	 * 
	 * <pre>
	 * alpha = (timeStamp - left) / (right - left)
	 * result = value(left) + alpha * (value(right) - value(left))
	 * </pre>
	 *
	 * <p>
	 * {@code timeStamp} may lie outside {@code [leftTimeStamp, rightTimeStamp]} --
	 * in that case the result is an extrapolated value.
	 * </p>
	 *
	 * <p>
	 * Returns {@code Double.NaN} if either anchor value is null or NaN.
	 * </p>
	 *
	 * @param timeSeries     the series to query; must not be null
	 * @param leftTimeStamp  the left anchor timestamp; must be strictly less than
	 *                       {@code rightTimeStamp} and within series bounds
	 * @param rightTimeStamp the right anchor timestamp; must be within series
	 *                       bounds
	 * @param timeStamp      the target timestamp
	 * @return the interpolated (or extrapolated) value, or {@code Double.NaN} if
	 *         either anchor value is null or NaN
	 * @throws NullPointerException      if timeSeries is null
	 * @throws IllegalArgumentException  if leftTimeStamp &ge; rightTimeStamp
	 * @throws IndexOutOfBoundsException if either anchor timestamp is outside the
	 *                                   series range
	 */
	public static double getInterpolatedValue(ITimeSeries<Double> timeSeries, long leftTimeStamp, long rightTimeStamp,
			long timeStamp) throws IllegalArgumentException, IndexOutOfBoundsException {

		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		if (leftTimeStamp >= rightTimeStamp)
			throw new IllegalArgumentException("TimeSeriesTools.getInterpolatedValue: leftTimeStamp must be"
					+ " strictly less than rightTimeStamp (" + leftTimeStamp + " >= " + rightTimeStamp + ")");

		long first = timeSeries.getFirstTimestamp();
		long last = timeSeries.getLastTimestamp();

		if (leftTimeStamp < first || leftTimeStamp > last)
			throw new IndexOutOfBoundsException("TimeSeriesTools.getInterpolatedValue: leftTimeStamp " + leftTimeStamp
					+ " outside series range [" + first + ", " + last + "]");

		if (rightTimeStamp < first || rightTimeStamp > last)
			throw new IndexOutOfBoundsException("TimeSeriesTools.getInterpolatedValue: rightTimeStamp " + rightTimeStamp
					+ " outside series range [" + first + ", " + last + "]");

		Double v1 = timeSeries.getValue(leftTimeStamp, false);
		Double v2 = timeSeries.getValue(rightTimeStamp, false);

		if (v1 == null || Double.isNaN(v1) || v2 == null || Double.isNaN(v2))
			return Double.NaN;

		double alpha = (double) (timeStamp - leftTimeStamp) / (double) (rightTimeStamp - leftTimeStamp);

		return v1 + alpha * (v2 - v1);
	}

	/**
	 * Returns the linearly interpolated value of the time series at
	 * {@code timeStamp}.
	 *
	 * <p>
	 * If {@code timeStamp} exactly matches an existing entry, that value is
	 * returned directly. Otherwise the two bracketing entries are used for linear
	 * interpolation.
	 * </p>
	 *
	 * <p>
	 * Returns {@code Double.NaN} if the bracketing anchor value is null or NaN.
	 * </p>
	 *
	 * @param timeSeries the series to interpolate; must not be null or empty
	 * @param timeStamp  the target timestamp; must be within
	 *                   {@code [firstTimestamp, lastTimestamp]}
	 * @return the exact or interpolated value
	 * @throws NullPointerException      if timeSeries is null
	 * @throws IllegalStateException     if timeSeries is empty
	 * @throws IndexOutOfBoundsException if timeStamp is outside the series range
	 */
	public static double getInterpolatedValue(ITimeSeries<Double> timeSeries, long timeStamp) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");
		if (timeSeries.isEmpty())
			throw new IllegalStateException("TimeSeriesTools.getInterpolatedValue: series is empty");

		long first = timeSeries.getFirstTimestamp();
		long last = timeSeries.getLastTimestamp();

		if (timeStamp < first || timeStamp > last)
			throw new IndexOutOfBoundsException("TimeSeriesTools.getInterpolatedValue: timestamp " + timeStamp
					+ " outside series range [" + first + ", " + last + "]");

		// exact match - no interpolation needed
		if (timeSeries.containsTimestamp(timeStamp)) {
			Double exact = timeSeries.getValue(timeStamp, false);
			return exact != null ? exact : Double.NaN;
		}

		// floor index is guaranteed valid: timeStamp is in range and not an
		// exact match, so a left neighbor and a right neighbor always exist
		int floorIdx = timeSeries.findByDate(timeStamp, false);
		long leftTimeStamp = timeSeries.getTimestamp(floorIdx);
		long rightTimeStamp = timeSeries.getTimestamp(floorIdx + 1);

		return getInterpolatedValue(timeSeries, leftTimeStamp, rightTimeStamp, timeStamp);
	}

	/**
	 * Extrapolates the value of the time series at {@code target} using the two
	 * outermost (earliest or latest) time-value pairs.
	 *
	 * <p>
	 * If {@code target} is within the series range, delegates to
	 * {@link #getInterpolatedValue(ITimeSeries, long)} instead.
	 * </p>
	 *
	 * <p>
	 * For a more sophisticated extrapolation approach, use the {@code predict}
	 * method.
	 * </p>
	 *
	 * <p>
	 * Returns {@code Double.NaN} if either anchor value is null or NaN.
	 * </p>
	 *
	 * @param timeSeries the series to extrapolate from; must not be null and must
	 *                   contain at least 2 entries
	 * @param target     the target timestamp
	 * @return the interpolated or extrapolated value, or {@code Double.NaN} if
	 *         anchor values are missing
	 * @throws NullPointerException     if timeSeries is null
	 * @throws IllegalArgumentException if timeSeries has fewer than 2 entries
	 */
	public static double extrapolate(ITimeSeriesUnivariate timeSeries, long target) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		if (timeSeries.size() < 2)
			throw new IllegalArgumentException(
					"TimeSeriesTools.extrapolate: series must have at least 2 " + "entries, has " + timeSeries.size());

		// in-range: delegate to interpolation
		if (target >= timeSeries.getFirstTimestamp() && target <= timeSeries.getLastTimestamp())
			return getInterpolatedValue(timeSeries, target);

		// extrapolation: use the two nearest outer anchor points
		int size = timeSeries.size();
		long leftTimestamp;
		long rightTimestamp;

		if (target < timeSeries.getFirstTimestamp()) {
			leftTimestamp = timeSeries.getTimestamp(0);
			rightTimestamp = timeSeries.getTimestamp(1);
		} else {
			leftTimestamp = timeSeries.getTimestamp(size - 2);
			rightTimestamp = timeSeries.getTimestamp(size - 1);
		}

		return getInterpolatedValue(timeSeries, leftTimestamp, rightTimestamp, target);
	}

	/**
	 * Predicts the value of the time series at a future {@code target} time stamp
	 * using the full series history starting from the first time stamp.
	 *
	 * <p>
	 * Only forward prediction is supported - {@code target} must be strictly
	 * greater than the last time stamp in the series.
	 * </p>
	 *
	 * <p>
	 * <b>Performance note:</b> this method is O(n x quantization) and can be very
	 * slow for long, high-frequency series. For in-range queries use
	 * {@link #getInterpolatedValue(ITimeSeries, long)} instead.
	 * </p>
	 *
	 * @param timeSeries the series to predict from; must not be null or empty
	 * @param target     the future time stamp to predict; must be greater than the
	 *                   last time stamp in the series
	 * @return the predicted values as a {@code double[]}
	 * @throws NullPointerException     if timeSeries is null
	 * @throws IllegalArgumentException if timeSeries is empty or target is not
	 *                                  strictly after the last time stamp
	 */
	public static double[] predict(ITimeSeriesUnivariate timeSeries, long target) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		if (timeSeries.isEmpty())
			throw new IllegalArgumentException("TimeSeriesTools.predict: series must not be empty");

		if (target <= timeSeries.getLastTimestamp())
			throw new IllegalArgumentException("TimeSeriesTools.predict: target " + target
					+ " must be strictly after the last timestamp " + timeSeries.getLastTimestamp());

		return predict(timeSeries, target, timeSeries.getFirstTimestamp());
	}

	/**
	 * Predicts the value of the time series at a future {@code target} time stamp
	 * using a weighted average of all pairwise linear trends within the observation
	 * window {@code [maxAgeTimeStamp, lastTimestamp]}.
	 *
	 * <p>
	 * Each pair of time stamps {@code (k, l)} with {@code k < l} within the window
	 * contributes a linear trend extrapolated to {@code target}. Pairs are weighted
	 * by recency - pairs closer to {@code target} receive higher weight via the
	 * linear decay:
	 * {@code w = 1 - ((target-k) + (target-l)) / (2 x windowDuration)}. Pairs with
	 * non-positive weight are excluded.
	 * </p>
	 *
	 * <p>
	 * Returns a two-element array {@code [predictedValue, uncertainty]} where
	 * uncertainty is in {@code [0, 1]}. Uncertainty combines:
	 * </p>
	 * <ul>
	 * <li><b>Value uncertainty:</b> coefficient of variation of the weighted
	 * prediction distribution (std / mean). High when trends disagree.</li>
	 * <li><b>Temporal uncertainty:</b> exponential decay based on how far
	 * {@code target} is beyond the last observation.</li>
	 * </ul>
	 *
	 * <p>
	 * <b>Performance:</b> O(k^2 log k) where k is the number of timestamps in the
	 * window {@code [maxAgeTimeStamp, lastTimestamp]}. For long, high-frequency
	 * series with a wide window this is very slow. Narrow the window to 1--3x the
	 * series periodicity for acceptable performance.
	 * </p>
	 *
	 * <p>
	 * Only forward prediction is supported - {@code target} must be strictly after
	 * the last time stamp.
	 * </p>
	 *
	 * @param timeSeries      the series to predict from; must not be null or empty
	 * @param target          the future time stamp to predict; must be &gt; last
	 *                        time stamp
	 * @param maxAgeTimeStamp the earliest time stamp whose pairs contribute to the
	 *                        prediction; must be &le; {@code target}
	 * @return {@code [predictedValue, uncertainty]} where uncertainty &isin;
	 *         {@code [0, 1]}
	 * @throws NullPointerException     if timeSeries is null
	 * @throws IllegalArgumentException if timeSeries is empty, target is not
	 *                                  strictly after the last time stamp, or
	 *                                  maxAgeTimeStamp &gt; target
	 */
	public static double[] predict(ITimeSeriesUnivariate timeSeries, long target, long maxAgeTimeStamp) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		return TimeSeriesPrediction.predict(timeSeries, target, maxAgeTimeStamp);
	}

	/**
	 * Returns a subsequence of the time series covering the range
	 * {@code [start, end]}.
	 *
	 * <p>
	 * Boundary behavior is controlled by {@link TemporalCropStrategy}:
	 * </p>
	 * <ul>
	 * <li><b>NextOuter:</b> nearest time stamps enclosing {@code [start, end]} --
	 * returned series may start before {@code start} and end after {@code end}</li>
	 * <li><b>NextInner:</b> only time stamps strictly within {@code [start, end]} --
	 * returned series may start after {@code start} and end before {@code end}</li>
	 * <li><b>ExactInterpolation:</b> interpolated values inserted at {@code start}
	 * and {@code end} - returned series covers exactly {@code [start, end]}</li>
	 * </ul>
	 *
	 * <p>
	 * No extrapolation is performed. If the requested interval has no overlap with
	 * the series, {@code null} is returned.
	 * </p>
	 *
	 * <p>
	 * The missing value indicator, name, and description of the source series are
	 * preserved.
	 * </p>
	 *
	 * <p>
	 * <b>Example:</b>
	 * </p>
	 * 
	 * <pre>
	 * Time series: [10: 1.0, 20: 2.0, 30: 3.0, 40: 4.0]
	 *
	 * getSubsequence(ts, 15, 35, NextOuter)
	 *    - to [10: 1.0, 20: 2.0, 30: 3.0, 40: 4.0]
	 *
	 * getSubsequence(ts, 15, 35, NextInner)
	 *    - to [20: 2.0, 30: 3.0]
	 *
	 * getSubsequence(ts, 15, 35, ExactInterpolation)
	 *    - to [15: 1.5, 20: 2.0, 30: 3.0, 35: 3.5]
	 *
	 * getSubsequence(ts, 50, 60, any)
	 *    - to null  (no overlap)
	 * </pre>
	 *
	 * @param timeSeries           the series to segment; must not be null
	 * @param start                the start of the range (inclusive); must be &le;
	 *                             {@code end}
	 * @param end                  the end of the range (inclusive); must be &ge;
	 *                             {@code start}
	 * @param temporalCropStrategy how to handle boundary time stamps
	 * @return a new {@link ITimeSeriesUnivariate} covering the requested range, or
	 *         {@code null} if there is no overlap with the series
	 * @throws NullPointerException     if timeSeries or temporalCropStrategy is
	 *                                  null
	 * @throws IllegalArgumentException if start &gt; end
	 */
	public static ITimeSeriesUnivariate getSubsequence(ITimeSeries<Double> timeSeries, long start, long end,
			TemporalCropStrategy temporalCropStrategy) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");
		Objects.requireNonNull(temporalCropStrategy, "temporalCropStrategy must not be null");

		if (start > end)
			throw new IllegalArgumentException(
					"TimeSeriesTools.getSubsequence: start " + start + " must be <= end " + end);

		if (timeSeries.isEmpty())
			return null;

		// no overlap - return null rather than throwing, consistent with the
		// "partial overlap" contract documented above
		if (start > timeSeries.getLastTimestamp() || end < timeSeries.getFirstTimestamp())
			return null;

		// resolve start index
		int indexStart;
		if (start <= timeSeries.getFirstTimestamp()) {
			indexStart = 0;
		} else {
			indexStart = timeSeries.findByDate(start, false);
			// for NextInner: advance past the floor if it is strictly before start
			if (temporalCropStrategy == TemporalCropStrategy.NextInner && timeSeries.getTimestamp(indexStart) < start)
				indexStart++;
		}

		// resolve end index
		int indexEnd;
		if (end >= timeSeries.getLastTimestamp()) {
			indexEnd = timeSeries.size() - 1;
		} else {
			indexEnd = timeSeries.findByDate(end, false);
			// for NextOuter/ExactInterpolation: include the entry just after end
			if (temporalCropStrategy != TemporalCropStrategy.NextInner && timeSeries.getTimestamp(indexEnd) < end
					&& indexEnd < timeSeries.size() - 1)
				indexEnd++;
			// for NextInner: do not include the entry after end
			if (temporalCropStrategy == TemporalCropStrategy.NextInner && timeSeries.getTimestamp(indexEnd) > end)
				indexEnd--;
		}

		if (indexStart > indexEnd)
			return null; // NextInner may legitimately produce an empty range

		int count = indexEnd - indexStart + 1;
		List<Long> timestamps = new ArrayList<>(count);
		List<Double> values = new ArrayList<>(count);

		for (int i = indexStart; i <= indexEnd; i++) {
			timestamps.add(timeSeries.getTimestamp(i));
			values.add(timeSeries.getValue(i));
		}

		TimeSeriesUnivariate result = new TimeSeriesUnivariate(timestamps, values,
				timeSeries.getMissingValueIndicator());

		if (timeSeries instanceof ITimeSeriesUnivariate) {
			result.setName(((ITimeSeriesUnivariate) timeSeries).getName());
			result.setDescription(((ITimeSeriesUnivariate) timeSeries).getDescription());
		}

		// ExactInterpolation: crop boundaries to exactly [start, end]
		if (temporalCropStrategy == TemporalCropStrategy.ExactInterpolation) {
			if (result.size() > 1 && result.getFirstTimestamp() < start) {
				double v = getInterpolatedValue(result, result.getTimestamp(0), result.getTimestamp(1), start);
				result.insert(start, v);
				result.removeTimeValue(0);
			}
			if (result.size() > 1 && result.getLastTimestamp() > end) {
				int last = result.size() - 1;
				double v = getInterpolatedValue(result, result.getTimestamp(last - 1), result.getTimestamp(last), end);
				result.insert(end, v);
				result.removeTimeValue(result.size() - 1);
			}
		}

		return result;
	}

	/**
	 * Returns a subsequence covering {@code [start, end]} using
	 * {@link TemporalCropStrategy#NextOuter} (boundaries may extend slightly beyond
	 * the requested range when exact matches do not exist).
	 *
	 * <p>
	 * Convenience wrapper for
	 * {@link #getSubsequence(ITimeSeries, long, long, TemporalCropStrategy)}.
	 * </p>
	 *
	 * @param timeSeries                     the series to segment; must not be null
	 * @param start                          start of range; must be &le; end
	 * @param end                            end of range; must be &ge; start
	 * @param requireStartEndTimestampsExist if {@code true}, exact time stamp
	 *                                       matches are required - delegates to
	 *                                       {@link TemporalCropStrategy#NextOuter};
	 *                                       if {@code false}, enclosing time stamps
	 *                                       are used
	 * @return subsequence or {@code null} if no overlap
	 * @throws NullPointerException     if timeSeries is null
	 * @throws IllegalArgumentException if start &gt; end, or exact match required
	 *                                  but not found
	 */
	public static ITimeSeriesUnivariate getSubsequence(ITimeSeriesUnivariate timeSeries, long start, long end,
			boolean requireStartEndTimestampsExist) {
		if (requireStartEndTimestampsExist) {
			// strict mode: validate exact matches before delegating
			Objects.requireNonNull(timeSeries, "timeSeries must not be null");
			if (!timeSeries.isEmpty() && timeSeries.containsTimestamp(start) && timeSeries.containsTimestamp(end))
				return getSubsequence(timeSeries, start, end, TemporalCropStrategy.NextOuter);
			throw new IllegalArgumentException("TimeSeriesTools.getSubsequence: exact timestamps " + start + " and/or "
					+ end + " not found in series");
		}
		return getSubsequence(timeSeries, start, end, TemporalCropStrategy.NextOuter);
	}

	/**
	 * Returns a subsequence covering {@code [start, end]}, optionally cropped to
	 * exactly {@code [start, end]} by linear interpolation.
	 *
	 * <p>
	 * Convenience wrapper for
	 * {@link #getSubsequence(ITimeSeries, long, long, TemporalCropStrategy)}.
	 * </p>
	 *
	 * @param timeSeries                     the series to segment; must not be null
	 * @param start                          start of range;
	 * @param end                            end of range;
	 * @param requireStartEndTimestampsExist if {@code true}, returns the enclosing
	 *                                       subsequence without cropping
	 * @param cropIfTimeStampsDontExist      if {@code true} and
	 *                                       {@code requireStartEndTimestampsExist}
	 *                                       is {@code false}, boundaries are
	 *                                       cropped by linear interpolation
	 * @return subsequence or {@code null} if no overlap
	 * @throws NullPointerException     if timeSeries is null
	 * @throws IllegalArgumentException if start &gt; end
	 */
	public static ITimeSeriesUnivariate getSubsequence(ITimeSeriesUnivariate timeSeries, long start, long end,
			boolean requireStartEndTimestampsExist, boolean cropIfTimeStampsDontExist) {
		if (requireStartEndTimestampsExist || !cropIfTimeStampsDontExist)
			return getSubsequence(timeSeries, start, end, TemporalCropStrategy.NextOuter);
		return getSubsequence(timeSeries, start, end, TemporalCropStrategy.ExactInterpolation);
	}

	/**
	 * Segments a time series into consecutive windows of equal duration.
	 *
	 * <p>
	 * Windows start at the first time stamp and advance by {@code timeDuration}
	 * until the last time stamp is passed. Each window covers
	 * {@code [l, l + duration]} and is cropped to exact boundaries by linear
	 * interpolation ({@link TemporalCropStrategy#ExactInterpolation}).
	 * </p>
	 *
	 * <p>
	 * The last window may be shorter than {@code timeDuration} if the series does
	 * not end on a window boundary.
	 * </p>
	 *
	 * @param timeSeries   the series to segment; must not be null or empty
	 * @param timeDuration the window duration; must not be null and must produce a
	 *                     positive millisecond duration
	 * @return a list of non-overlapping subsequences in chronological order; never
	 *         null, may be empty if the series is shorter than one window
	 * @throws NullPointerException     if timeSeries or timeDuration is null
	 * @throws IllegalArgumentException if timeSeries is empty or the duration is
	 *                                  zero or negative
	 */
	public static List<ITimeSeriesUnivariate> segmentTimeSeries(ITimeSeriesUnivariate timeSeries,
			TimeDuration timeDuration) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");
		Objects.requireNonNull(timeDuration, "timeDuration must not be null");

		if (timeSeries.isEmpty())
			throw new IllegalArgumentException("TimeSeriesTools.segmentTimeSeries: series must not be empty");

		long duration = calculateEquidistanceInMillis(timeDuration);
		if (duration <= 0)
			throw new IllegalArgumentException(
					"TimeSeriesTools.segmentTimeSeries: duration must be positive, was " + duration);

		long first = timeSeries.getFirstTimestamp();
		long last = timeSeries.getLastTimestamp();

		int estimatedCount = (int) ((last - first) / duration) + 1;
		List<ITimeSeriesUnivariate> subsequences = new ArrayList<>(estimatedCount);

		for (long l = first; l <= last; l += duration) {
			long windowEnd = l + duration;
			// guard against long overflow for very large durations
			if (windowEnd < l)
				break;

			ITimeSeriesUnivariate subsequence = getSubsequence(timeSeries, l, Math.min(windowEnd, last),
					TemporalCropStrategy.ExactInterpolation);

			if (subsequence != null)
				subsequences.add(subsequence);
		}

		return subsequences;
	}

	/**
	 * Computes a mean time series from a collection of uni-variate time series.
	 *
	 * <p>
	 * For each time stamp that appears in any input series, the mean of all values
	 * at that time stamp is computed and stored in the result. Time stamps are
	 * aligned by exact match - no interpolation is performed.
	 * </p>
	 *
	 * <p>
	 * Null series within the iterable are silently skipped. Null values within a
	 * series are excluded from the mean computation for that time stamp.
	 * </p>
	 *
	 * <p>
	 * Returns an empty series if the input is empty or contains only null series.
	 * </p>
	 *
	 * @param timeSeriesList the series to aggregate; must not be null; null
	 *                       elements are permitted and skipped
	 * @return a new {@link ITimeSeriesUnivariate} containing the per-timestamp
	 *         mean; never null
	 * @throws NullPointerException if timeSeriesList is null
	 */
	public static ITimeSeriesUnivariate getMeanTimeSeries(Iterable<ITimeSeriesUnivariate> timeSeriesList) {
		Objects.requireNonNull(timeSeriesList, "timeSeriesList must not be null");

		SortedMap<Long, List<Double>> rawValues = new TreeMap<>();

		for (ITimeSeriesUnivariate ts : timeSeriesList) {
			if (ts == null)
				continue;
			for (int i = 0; i < ts.size(); i++) {
				Double value = ts.getValue(i);
				if (value != null && !Double.isNaN(value))
					rawValues.computeIfAbsent(ts.getTimestamp(i), k -> new ArrayList<>()).add(value);
			}
		}

		int size = rawValues.size();
		List<Long> timestamps = new ArrayList<>(size);
		List<Double> means = new ArrayList<>(size);

		for (Map.Entry<Long, List<Double>> entry : rawValues.entrySet()) {
			timestamps.add(entry.getKey());
			means.add(MathFunctions.getMean(entry.getValue()));
		}

		return new TimeSeriesUnivariate(timestamps, means, Double.NaN);
	}

	/**
	 * Computes six statistical time series over a collection of uni-variate time
	 * series, evaluated at every time stamp present in any input series.
	 *
	 * <p>
	 * The returned array contains, in order:
	 * </p>
	 * <ol start="0">
	 * <li>Lower quantile at {@code quantile}%</li>
	 * <li>Lower quartile (25th percentile)</li>
	 * <li>Mean</li>
	 * <li>Median (50th percentile)</li>
	 * <li>Upper quartile (75th percentile)</li>
	 * <li>Upper quantile at {@code (100 - quantile)}%</li>
	 * </ol>
	 *
	 * <p>
	 * For each time stamp, values are collected from all input series that span
	 * that time stamp, using linear interpolation where no exact match exists. Null
	 * and NaN values are excluded.
	 * </p>
	 *
	 * <p>
	 * Null series within the iterable are silently skipped.
	 * </p>
	 *
	 * @param timeSeriesList the series to aggregate; must not be null; null
	 *                       elements are permitted and skipped
	 * @param quantile       the lower quantile percentage in {@code [0, 50]}; the
	 *                       upper quantile is {@code 100 - quantile}
	 * @return an array of six {@link ITimeSeriesUnivariate} instances as described
	 *         above; never null
	 * @throws NullPointerException     if timeSeriesList is null
	 * @throws IllegalArgumentException if quantile is outside {@code [0, 50]}
	 */
	public static ITimeSeriesUnivariate[] computeQuantilesQuartilesMeanTimeSeries(
			Iterable<ITimeSeriesUnivariate> timeSeriesList, double quantile) {
		Objects.requireNonNull(timeSeriesList, "timeSeriesList must not be null");

		if (quantile < 0.0 || quantile > 50.0)
			throw new IllegalArgumentException("TimeSeriesTools.computeQuantilesQuartilesMeanTimeSeries: "
					+ "quantile must be in [0, 50], was " + quantile);

		// collect all time stamps across all series - TreeSet ensures sorted order
		SortedSet<Long> timestampSet = new TreeSet<>();
		for (ITimeSeries<? extends Double> ts : timeSeriesList) {
			if (ts == null)
				continue;
			for (int i = 0; i < ts.size(); i++)
				timestampSet.add(ts.getTimestamp(i));
		}

		int n = timestampSet.size();
		List<Long> timestampList = new ArrayList<>(timestampSet);

		List<Double> lowerQuantile = new ArrayList<>(n);
		List<Double> lowerQuartile = new ArrayList<>(n);
		List<Double> mean = new ArrayList<>(n);
		List<Double> median = new ArrayList<>(n);
		List<Double> upperQuartile = new ArrayList<>(n);
		List<Double> upperQuantile = new ArrayList<>(n);

		for (long timeStamp : timestampList) {
			List<Double> values = new ArrayList<>();

			for (ITimeSeries<? extends Double> ts : timeSeriesList) {
				if (ts == null)
					continue;
				if (ts.getFirstTimestamp() > timeStamp || ts.getLastTimestamp() < timeStamp)
					continue;
				Double v = ts.getValue(timeStamp, true);
				if (v != null && !Double.isNaN(v))
					values.add(v);
			}

			StatisticsSupport statistics = new StatisticsSupport(values);

			lowerQuantile.add(statistics.getPercentile(quantile));
			lowerQuartile.add(statistics.getPercentile(25.0));
			mean.add(statistics.getMean());
			median.add(statistics.getMedian());
			upperQuartile.add(statistics.getPercentile(75.0));
			upperQuantile.add(statistics.getPercentile(100.0 - quantile));
		}

		// timestampList is already an ArrayList - share it across all six series
		// since TimeSeriesUnivariate accepts the list directly without copying
		ITimeSeriesUnivariate[] result = new ITimeSeriesUnivariate[6];
		result[0] = new TimeSeriesUnivariate(new ArrayList<>(timestampList), lowerQuantile, Double.NaN);
		result[1] = new TimeSeriesUnivariate(new ArrayList<>(timestampList), lowerQuartile, Double.NaN);
		result[2] = new TimeSeriesUnivariate(new ArrayList<>(timestampList), mean, Double.NaN);
		result[3] = new TimeSeriesUnivariate(new ArrayList<>(timestampList), median, Double.NaN);
		result[4] = new TimeSeriesUnivariate(new ArrayList<>(timestampList), upperQuartile, Double.NaN);
		result[5] = new TimeSeriesUnivariate(new ArrayList<>(timestampList), upperQuantile, Double.NaN);
		return result;
	}

	/**
	 * Returns a time series representing the count of input series that have a
	 * value at each time stamp present in any input series.
	 *
	 * <p>
	 * For each time stamp collected across all input series, the count reflects how
	 * many series satisfy the criterion controlled by {@code requireExactMatch}:
	 * </p>
	 * <ul>
	 * <li>If {@code true}: only series containing that exact time stamp are
	 * counted.</li>
	 * <li>If {@code false}: series whose range {@code [first, last]} covers the
	 * time stamp are counted, regardless of whether an exact entry exists.</li>
	 * </ul>
	 *
	 * <p>
	 * Null series within the iterable are silently skipped.
	 * </p>
	 *
	 * @param timeSeriesList    the series to count; must not be null; null elements
	 *                          are permitted and skipped
	 * @param requireExactMatch if {@code true}, only exact time stamp matches are
	 *                          counted; if {@code false}, range coverage is
	 *                          sufficient
	 * @return a new {@link ITimeSeriesUnivariate} with {@code Double} counts at
	 *         each observed time stamp; never null
	 * @throws NullPointerException if timeSeriesList is null
	 */
	public static ITimeSeriesUnivariate getCountsTimeSeries(Iterable<ITimeSeriesUnivariate> timeSeriesList,
			boolean requireExactMatch) {
		Objects.requireNonNull(timeSeriesList, "timeSeriesList must not be null");

		// collect all time stamps across all series
		SortedSet<Long> timestampSet = new TreeSet<>();
		for (ITimeSeries<? extends Double> ts : timeSeriesList) {
			if (ts == null)
				continue;
			for (int i = 0; i < ts.size(); i++)
				timestampSet.add(ts.getTimestamp(i));
		}

		int n = timestampSet.size();
		List<Long> timestampList = new ArrayList<>(timestampSet);
		List<Double> counts = new ArrayList<>(n);

		for (long timeStamp : timestampList) {
			int count = 0;
			for (ITimeSeries<? extends Double> ts : timeSeriesList) {
				if (ts == null)
					continue;
				if (requireExactMatch) {
					if (ts.containsTimestamp(timeStamp))
						count++;
				} else {
					if (!ts.isEmpty() && ts.getFirstTimestamp() <= timeStamp && ts.getLastTimestamp() >= timeStamp)
						count++;
				}
			}
			counts.add((double) count);
		}

		return new TimeSeriesUnivariate(timestampList, counts, Double.NaN);
	}

	/**
	 * Returns {@code true} if the time series is equi-distant - all intervals
	 * between consecutive time stamps are equal.
	 *
	 * <p>
	 * A series with fewer than 2 entries is considered equi-distant by convention --
	 * there are no intervals to violate the condition.
	 * </p>
	 *
	 * <p>
	 * Delegates interval extraction to
	 * {@link #getQuantizationsAsLong(ITimeSeries)}, which returns {@code null} for
	 * a null or empty series.
	 * </p>
	 *
	 * @param timeSeries the series to check; must not be null
	 * @return {@code true} if all consecutive intervals are equal, or if the series
	 *         has fewer than 2 entries
	 * @throws NullPointerException if timeSeries is null
	 */
	public static boolean isEquidistant(ITimeSeries<Double> timeSeries) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		long[] quantizations = getQuantizationsAsLong(timeSeries);

		if (quantizations == null || quantizations.length == 0)
			return true; // vacuously equi-distant - no intervals to compare

		long expected = quantizations[0];
		for (int i = 1; i < quantizations.length; i++)
			if (quantizations[i] != expected)
				return false;

		return true;
	}

	/**
	 * Returns the time-weighted mean value of the time series over the interval
	 * {@code [start, end]} using the trapezoidal method.
	 *
	 * <p>
	 * Each value is weighted by its temporal influence - half the distance to its
	 * left neighbor plus half the distance to its right neighbor, clamped to
	 * {@code [start, end]}. Boundary values are linearly interpolated when
	 * {@code start} or {@code end} do not exactly match existing time stamps.
	 * </p>
	 *
	 * <p>
	 * Returns {@code Double.NaN} if no valid entries exist within the interval.
	 * </p>
	 *
	 * @param timeSeries the series to query; must not be null
	 * @param start      the start of the interval (inclusive); must be &le;
	 *                   {@code end}
	 * @param end        the end of the interval (inclusive); must be within the
	 *                   series range
	 * @return the time-weighted mean over {@code [start, end]}, or
	 *         {@code Double.NaN} if no valid values exist in the interval
	 * @throws NullPointerException     if timeSeries is null
	 * @throws IllegalArgumentException if end &lt; start, start &gt; last
	 *                                  timestamp, or end &lt; first timestamp
	 */
	public static double getValueFromInterval(ITimeSeries<? extends Double> timeSeries, long start, long end) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		if (end < start)
			throw new IllegalArgumentException(
					"TimeSeriesTools.getValueFromInterval: end " + end + " < start " + start);

		if (end == start) {
			Double v = timeSeries.getValue(start, true);
			return v != null ? v : Double.NaN;
		}

		ITimeSeriesUnivariate subsequence = getSubsequence((ITimeSeriesUnivariate) timeSeries, start, end,
				TemporalCropStrategy.ExactInterpolation);

		if (subsequence == null || subsequence.size() < 2)
			return Double.NaN;

		// Must average the cropped subsequence, not the original, untouched
		// timeSeries -- otherwise every call for the same series returns that
		// series' own overall mean regardless of [start, end], which is exactly
		// what happened here before this fix.
		return getMeanWeighted(subsequence, 0.0);
	}

	/**
	 * Returns a time series representing the rate of change between each pair of
	 * neighboring time stamps, normalized to a per-year rate.
	 *
	 * <p>
	 * For each time stamp {@code t[i]} with {@code i > 0}, the change is computed
	 * as:
	 * </p>
	 * 
	 * <pre>
	 * diff = (value[i] - value[i - 1]) / years(t[i - 1], t[i])
	 * </pre>
	 * <p>
	 * If {@code relativeValues} is {@code true}, {@code diff} is divided by
	 * {@code |value[i-1]|} and multiplied by 100 to give a percentage rate. Entries
	 * where {@code value[i-1] == 0} are recorded as {@code 0.0} to avoid division
	 * by zero.
	 * </p>
	 *
	 * <p>
	 * The returned series has {@code size() - 1} entries.
	 * </p>
	 *
	 * @param timeSeries     the series to differentiate; must not be null and must
	 *                       have at least 2 entries
	 * @param relativeValues if {@code true}, returns percentage change per year
	 * @return a new series of per-year change values; never null
	 * @throws NullPointerException     if timeSeries is null
	 * @throws IllegalArgumentException if timeSeries has fewer than 2 entries
	 */
	public static ITimeSeriesUnivariate getChangeTimeSeries(ITimeSeriesUnivariate timeSeries, boolean relativeValues) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		if (timeSeries.size() < 2)
			throw new IllegalArgumentException("TimeSeriesTools.getChangeTimeSeries: series must have "
					+ "at least 2 entries, has " + timeSeries.size());

		int n = timeSeries.size();
		List<Long> timestamps = new ArrayList<>(n - 1);
		List<Double> changes = new ArrayList<>(n - 1);

		long prevTimestamp = timeSeries.getTimestamp(0);
		Double prevBoxed = timeSeries.getValue(0);
		double prevValue = (prevBoxed != null) ? prevBoxed : Double.NaN;

		for (int i = 1; i < n; i++) {
			long t = timeSeries.getTimestamp(i);
			Double boxed = timeSeries.getValue(i);
			double value = (boxed != null) ? boxed : Double.NaN;

			if (!Double.isNaN(prevValue) && !Double.isNaN(value)) {
				double y = (t - prevTimestamp) / (double) DateTools.YEAR_IN_MILLISECONDS_EXACT;
				double change;

				if (relativeValues) {
					// guard division by zero - record zero change when base is zero
					change = (prevValue == 0.0) ? 0.0 : ((value - prevValue) / Math.abs(prevValue)) * 100.0 / y;
				} else {
					change = (value - prevValue) / y;
				}

				timestamps.add(t);
				changes.add(change);
			}

			prevTimestamp = t;
			prevValue = value;
		}

		if (timestamps.isEmpty())
			return new TimeSeriesUnivariate(new ArrayList<>(), new ArrayList<>(), Double.NaN);

		return new TimeSeriesUnivariate(timestamps, changes, Double.NaN);
	}

	/**
	 * Returns a time series representing the rate of change between each time stamp
	 * and an interpolated value {@code comparisonDuration} in the past, normalized
	 * to a per-year rate.
	 *
	 * <p>
	 * For each time stamp {@code t}, the earlier reference point is
	 * {@code t - comparisonDuration}. If the earlier point is before the series
	 * start, that time stamp is skipped. The earlier value is obtained by linear
	 * interpolation.
	 * </p>
	 *
	 * <p>
	 * If {@code relativeValues} is {@code true}, the change is expressed as a
	 * percentage per year. Entries where the earlier value is {@code 0} are skipped
	 * to avoid division by zero.
	 * </p>
	 *
	 * @param timeSeries         the series to differentiate; must not be null
	 * @param relativeValues     if {@code true}, returns percentage change per year
	 * @param comparisonDuration the lookback duration; must not be null and must
	 *                           produce a positive millisecond duration
	 * @return a new series of per-year change values; never null
	 * @throws NullPointerException     if timeSeries or comparisonDuration is null
	 * @throws IllegalArgumentException if comparisonDuration produces a
	 *                                  non-positive duration
	 */
	public static ITimeSeriesUnivariate getChangeTimeSeries(ITimeSeriesUnivariate timeSeries, boolean relativeValues,
			TimeDuration comparisonDuration) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");
		Objects.requireNonNull(comparisonDuration, "comparisonDuration must not be null");

		long durationMillis = comparisonDuration.getDuration();
		if (durationMillis <= 0)
			throw new IllegalArgumentException("TimeSeriesTools.getChangeTimeSeries: comparisonDuration "
					+ "must be positive, was " + durationMillis);

		int n = timeSeries.size();
		List<Long> timestamps = new ArrayList<>(n);
		List<Double> changes = new ArrayList<>(n);

		for (int i = 0; i < n; i++) {
			long t = timeSeries.getTimestamp(i);
			Double boxed = timeSeries.getValue(i);
			if (boxed == null || Double.isNaN(boxed))
				continue;
			double value = boxed;

			long earlier = t - durationMillis;
			if (earlier < timeSeries.getFirstTimestamp())
				continue;

			double prevValue = getInterpolatedValue(timeSeries, earlier);
			if (Double.isNaN(prevValue))
				continue;

			double y = durationMillis / (double) DateTools.YEAR_IN_MILLISECONDS_EXACT;
			double change;

			if (relativeValues) {
				if (prevValue == 0.0)
					continue; // skip - undefined percentage change from zero base
				change = ((value - prevValue) / Math.abs(prevValue)) * 100.0 / y;
			} else {
				change = (value - prevValue) / y;
			}

			timestamps.add(t);
			changes.add(change);
		}

		if (timestamps.isEmpty())
			return new TimeSeriesUnivariate(new ArrayList<>(), new ArrayList<>(), Double.NaN);

		return new TimeSeriesUnivariate(timestamps, changes, Double.NaN);
	}

	/**
	 * Returns a time series with values transformed by a logarithm-like function
	 * that is continuous across zero.
	 *
	 * <p>
	 * The transformation is:
	 * </p>
	 * <ul>
	 * <li>{@code |value| >= 2}: {@code sign(value) * ln(|value|)}</li>
	 * <li>{@code |value| <  2}: {@code value * LN_LINEAR_SLOPE} - linear
	 * approximation matching the slope of {@code ln} at the boundary
	 * ({@code ln(2)/2 ~= 0.347})</li>
	 * <li>Negative values with {@code allowNegativeValues=false}:
	 * {@code Double.NaN}</li>
	 * </ul>
	 *
	 * @param timeSeries          the series to transform; must not be null
	 * @param allowNegativeValues if {@code true}, negative values are handled via
	 *                            negation; if {@code false}, negative values
	 *                            produce {@code Double.NaN}
	 * @return a new transformed series; never null
	 * @throws NullPointerException if timeSeries is null
	 */
	public static ITimeSeriesUnivariate getLogarithmLikeTimeSeries(ITimeSeriesUnivariate timeSeries,
			boolean allowNegativeValues) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		// slope of the linear segment: approximates ln'(x) at x=2, i.e. ln(2)/2
		final double LN_LINEAR_SLOPE = 0.345;
		final double LN_LINEAR_BOUNDARY = 2.0;

		int n = timeSeries.size();
		List<Long> timestamps = new ArrayList<>(n);
		List<Double> lnValues = new ArrayList<>(n);

		for (int i = 0; i < n; i++) {
			long t = timeSeries.getTimestamp(i);
			Double boxed = timeSeries.getValue(i);

			double value = (boxed != null) ? boxed : Double.NaN;
			double lnValue;

			if (Double.isNaN(value)) {
				lnValue = Double.NaN;
			} else if (value < 0 && !allowNegativeValues) {
				lnValue = Double.NaN;
			} else if (Math.abs(value) < LN_LINEAR_BOUNDARY) {
				lnValue = value * LN_LINEAR_SLOPE;
			} else {
				lnValue = Math.log(Math.abs(value)) * (value < 0 ? -1 : 1);
			}

			timestamps.add(t);
			lnValues.add(lnValue);
		}

		if (timestamps.isEmpty())
			return new TimeSeriesUnivariate(new ArrayList<>(), new ArrayList<>(), Double.NaN);

		return new TimeSeriesUnivariate(timestamps, lnValues, Double.NaN);
	}

	/**
	 * Returns a new time series with a constant subtracted from every value.
	 *
	 * <p>
	 * Null and NaN values in the source series are preserved as {@code Double.NaN}
	 * in the result.
	 * </p>
	 *
	 * @param timeSeries the series to transform; must not be null
	 * @param value      the constant to subtract
	 * @return a new series with {@code value} subtracted from each entry; never
	 *         null
	 * @throws NullPointerException if timeSeries is null
	 */
	public static ITimeSeriesUnivariate getSubtractedValueTimeSeries(ITimeSeriesUnivariate timeSeries, double value) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		int n = timeSeries.size();
		List<Long> timestamps = new ArrayList<>(n);
		List<Double> values = new ArrayList<>(n);

		for (int i = 0; i < n; i++) {
			Double boxed = timeSeries.getValue(i);
			timestamps.add(timeSeries.getTimestamp(i));
			values.add(boxed != null ? boxed - value : Double.NaN);
		}

		if (timestamps.isEmpty())
			return new TimeSeriesUnivariate(new ArrayList<>(), new ArrayList<>(), Double.NaN);

		return new TimeSeriesUnivariate(timestamps, values, timeSeries.getMissingValueIndicator());
	}

	/**
	 * Returns a new time series with a constant added to every value.
	 *
	 * <p>
	 * Delegates to
	 * {@link #getSubtractedValueTimeSeries(ITimeSeriesUnivariate, double)}.
	 * </p>
	 *
	 * @param timeSeries the series to transform; must not be null
	 * @param value      the constant to add
	 * @return a new series with {@code value} added to each entry; never null
	 * @throws NullPointerException if timeSeries is null
	 */
	public static ITimeSeriesUnivariate getAddedValueTimeSeries(ITimeSeriesUnivariate timeSeries, double value) {
		return getSubtractedValueTimeSeries(timeSeries, -value);
	}

	/**
	 * Returns the earliest first time stamp across a collection of time series.
	 *
	 * <p>
	 * Null series within the collection are silently skipped.
	 * </p>
	 *
	 * @param timeSeriesList the series to scan; must not be null or empty, and must
	 *                       contain at least one non-null series
	 * @return the earliest first time stamp across all non-null series
	 * @throws NullPointerException     if timeSeriesList is null
	 * @throws IllegalArgumentException if timeSeriesList is empty or contains only
	 *                                  null series
	 */
	public static long getFirstTimestamp(Collection<ITimeSeriesUnivariate> timeSeriesList) {
		Objects.requireNonNull(timeSeriesList, "timeSeriesList must not be null");

		if (timeSeriesList.isEmpty())
			throw new IllegalArgumentException("TimeSeriesTools.getFirstTimestamp: list must not be empty");

		long result = Long.MAX_VALUE;

		for (ITimeSeriesUnivariate ts : timeSeriesList)
			if (ts != null && !ts.isEmpty())
				result = Math.min(result, ts.getFirstTimestamp());

		if (result == Long.MAX_VALUE)
			throw new IllegalArgumentException(
					"TimeSeriesTools.getFirstTimestamp: list contains no " + "non-null, non-empty series");

		return result;
	}

	/**
	 * Returns the latest last time stamp across a collection of time series.
	 *
	 * <p>
	 * Null series within the collection are silently skipped.
	 * </p>
	 *
	 * @param timeSeriesList the series to scan; must not be null or empty, and must
	 *                       contain at least one non-null series
	 * @return the latest last time stamp across all non-null series
	 * @throws NullPointerException     if timeSeriesList is null
	 * @throws IllegalArgumentException if timeSeriesList is empty or contains only
	 *                                  null or empty series
	 */
	public static long getLastTimestamp(Collection<ITimeSeriesUnivariate> timeSeriesList) {
		Objects.requireNonNull(timeSeriesList, "timeSeriesList must not be null");

		if (timeSeriesList.isEmpty())
			throw new IllegalArgumentException("TimeSeriesTools.getLastTimestamp: list must not be empty");

		long result = Long.MIN_VALUE;

		for (ITimeSeriesUnivariate ts : timeSeriesList)
			if (ts != null && !ts.isEmpty())
				result = Math.max(result, ts.getLastTimestamp());

		if (result == Long.MIN_VALUE)
			throw new IllegalArgumentException(
					"TimeSeriesTools.getLastTimestamp: list contains no " + "non-null, non-empty series");

		return result;
	}

	/**
	 * Returns {@code true} if the time series contains any {@code NaN} or
	 * {@code null} values.
	 *
	 * <p>
	 * Note: values equal to the series' missing value indicator but not {@code NaN}
	 * are not detected by this method.
	 * </p>
	 *
	 * @param timeSeries the series to check; must not be null
	 * @return {@code true} if any value is {@code null} or {@code Double.NaN}
	 * @throws NullPointerException if timeSeries is null
	 */
	public static boolean containsNaN(ITimeSeries<? extends Double> timeSeries) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		for (int i = 0; i < timeSeries.size(); i++) {
			Double v = timeSeries.getValue(i);
			if (v == null || Double.isNaN(v))
				return true;
		}

		return false;
	}

	/**
	 * Returns a new time series with values clamped to
	 * {@code [minValue, maxValue]}.
	 *
	 * <p>
	 * Values below {@code minValue} are replaced with {@code minValue}; values
	 * above {@code maxValue} are replaced with {@code maxValue}. Null and NaN
	 * values are preserved as {@code Double.NaN}.
	 * </p>
	 *
	 * @param timeSeries the series to clamp; must not be null
	 * @param minValue   the lower bound (inclusive)
	 * @param maxValue   the upper bound (inclusive); must be &ge; minValue
	 * @return a new clamped series; never null
	 * @throws NullPointerException     if timeSeries is null
	 * @throws IllegalArgumentException if minValue &gt; maxValue
	 */
	public static ITimeSeriesUnivariate clampTimeSeries(ITimeSeriesUnivariate timeSeries, double minValue,
			double maxValue) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		if (minValue > maxValue)
			throw new IllegalArgumentException(
					"TimeSeriesTools.clampTimeSeries: minValue " + minValue + " > maxValue " + maxValue);

		int n = timeSeries.size();
		List<Long> timestamps = new ArrayList<>(n);
		List<Double> values = new ArrayList<>(n);

		for (int i = 0; i < n; i++) {
			timestamps.add(timeSeries.getTimestamp(i));
			Double boxed = timeSeries.getValue(i);
			if (boxed == null || Double.isNaN(boxed))
				values.add(Double.NaN);
			else
				values.add(Math.min(maxValue, Math.max(minValue, boxed)));
		}

		return new TimeSeriesUnivariate(timestamps, values, timeSeries.getMissingValueIndicator());
	}

}
