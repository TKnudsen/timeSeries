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

import com.github.TKnudsen.ComplexDataObject.model.io.parsers.objects.DoubleParser;
import com.github.TKnudsen.ComplexDataObject.model.tools.MathFunctions;
import com.github.TKnudsen.ComplexDataObject.model.tools.StatisticsSupport;
import com.github.TKnudsen.ComplexDataObject.model.weighting.Long.LinearLongWeightingKernel;
import com.github.TKnudsen.timeseries.data.ITemporalLabeling;
import com.github.TKnudsen.timeseries.data.ITimeSeries;
import com.github.TKnudsen.timeseries.data.ITimeValuePair;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.multivariate.TimeValuePairMultivariate;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.primitives.TimeQuantization;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariateFactory;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariateLabeled;
import com.github.TKnudsen.timeseries.data.univariate.TimeValuePairUnivariate;

import smile.math.Math;

/**
 * <p>
 * Tools class for general statistical operations and routines applied on
 * univariate time series
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015-2024
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.14
 */
public final class TimeSeriesTools {

	public static Long YEAR_IN_MILLISECONDS = 31556952000L; // 365.2425 days a solar year

	private static DoubleParser doubleParser = new DoubleParser();

	private TimeSeriesTools() {
	}

	/**
	 * retrieves the minimum time stamp of all time series
	 * 
	 * @param timeSeries time series
	 * @return long
	 */
	public static long getMinStart(Collection<ITimeSeriesUnivariate> timeSeries) {
		long min = Long.MAX_VALUE - 1;

		for (ITimeSeries<Double> ts : timeSeries)
			if (ts != null)
				min = Math.min(min, ts.getFirstTimestamp());

		if (min == Long.MAX_VALUE - 1)
			return Long.MIN_VALUE;

		return min;
	}

	/**
	 * retrieves the minimum time stamp of all time series
	 * 
	 * @param timeSeries time series
	 * @return long
	 */
	public static long getMaxEnd(Collection<ITimeSeriesUnivariate> timeSeries) {
		long max = Long.MIN_VALUE + 1;

		for (ITimeSeries<Double> ts : timeSeries)
			if (ts != null)
				max = Math.max(max, ts.getLastTimestamp());

		if (max == Long.MIN_VALUE + 1)
			return Long.MAX_VALUE;

		return max;
	}

	public static Double getMinValue(Collection<ITimeSeriesUnivariate> timeSeriesList) {
		Double min = Double.POSITIVE_INFINITY;

		if (timeSeriesList != null)
			for (ITimeSeriesUnivariate timeSeries : timeSeriesList)
				if (timeSeries != null && !timeSeries.isEmpty())
					min = Math.min(min, getMinValue(timeSeries));

		return min;
	}

	public static Double getMaxValue(Collection<ITimeSeriesUnivariate> timeSeriesList) {
		Double max = Double.NEGATIVE_INFINITY;

		if (timeSeriesList != null)
			for (ITimeSeriesUnivariate timeSeries : timeSeriesList)
				if (timeSeries != null && !timeSeries.isEmpty())
					max = Math.max(max, getMaxValue(timeSeries));

		return max;
	}

	public static double getMinValue(ITimeSeries<? extends Double> timeSeries) {
		if (timeSeries == null)
			throw new IllegalStateException("TimeSeries is null");
		if (timeSeries.isEmpty())
			throw new IllegalStateException("TimeSeries is empty");

		double min = Double.POSITIVE_INFINITY;
		for (double d : timeSeries.getValues())
			if (!Double.isNaN(d))
				min = Math.min(min, d);
		return min;
	}

	public static double getMaxValue(ITimeSeries<? extends Double> timeSeries) {
		if (timeSeries == null)
			throw new IllegalStateException("TimeSeries is null");
		if (timeSeries.isEmpty())
			throw new IllegalStateException("TimeSeries is empty");

		double max = Double.NEGATIVE_INFINITY;
		for (double d : timeSeries.getValues())
			if (!Double.isNaN(d))
				max = Math.max(max, d);
		return max;
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
	public static final double getMeanWeighted(ITimeSeries<? extends Double> timeSeries,
			double weightIncreaseOverTimeAdditive) {

		return getMeanWeighted(timeSeries, weightIncreaseOverTimeAdditive, 1.0);
	}

	/**
	 * calculates the mean/average of a time series. With the flag
	 * 'weightIncreaseOverTime', it is possible to weight later time stamps stronger
	 * than earlier ones. In this way a trend-like tendency can be inferred in a
	 * single value.
	 * 
	 * Attention: ignores null and NaN values in the time series.
	 * 
	 * @param timeSeries                           time series
	 * @param weightIncreaseOverTimeAdditive       additive way to add more weight
	 *                                             to later time stamps. 0 means no
	 *                                             increase
	 * @param weightIncreaseOverTimeMultiplicative multiplicative way to add more
	 *                                             weight to later time stamps,
	 *                                             suited to model recent weightings
	 *                                             even for long (high-frequent)
	 *                                             time series where additive models
	 *                                             do not work well. 1 means no
	 *                                             increase
	 * @return double
	 */
	public static final double getMeanWeighted(ITimeSeries<? extends Double> timeSeries,
			double weightIncreaseOverTimeAdditive, double weightIncreaseOverTimeMultiplicative) {

		Objects.requireNonNull(timeSeries);

		if (Double.isNaN(weightIncreaseOverTimeAdditive) || weightIncreaseOverTimeAdditive < 0)
			throw new IllegalArgumentException("weightIncreaseOverTime was " + weightIncreaseOverTimeAdditive);

		double w = 1.0;
		double wSum = 0;
		double sum = 0;

		for (Long timeStamp : timeSeries.getTimestamps()) {
			Double v = timeSeries.getValue(timeStamp, false);

			if (v == null || Double.isNaN(v)) {
				w *= weightIncreaseOverTimeMultiplicative;
				w += weightIncreaseOverTimeAdditive;
				continue;
			} else {
				sum += (v * w);
				wSum += w;
				w *= weightIncreaseOverTimeMultiplicative;
				w += weightIncreaseOverTimeAdditive;
			}
		}

		return MathFunctions.round(sum / wSum, 8);
	}

	/**
	 * ignores Double.NaN values.
	 * 
	 * @param timeSeries time series
	 * @return double
	 */
	public static double getMean(ITimeSeries<? extends Double> timeSeries) {
		if (timeSeries == null)
			throw new IllegalStateException("TimeSeries is null");
		if (timeSeries.isEmpty())
			throw new IllegalStateException("TimeSeries is empty");

		if (timeSeries.size() == 1) // to avoid a division by zero (time interval length)
			return timeSeries.getValue(0);

		double globalLength = 0;
		double means = 0;
		for (int i = 0; i < timeSeries.size(); i++) {
			// ignore missing values
			if (timeSeries.getValue(i) == null)
				continue;
			if (Double.isNaN(timeSeries.getValue(i)))
				continue;
			if (timeSeries.getMissingValueIndicator() != null
					&& compareDoubleObjects(timeSeries.getValue(i), timeSeries.getMissingValueIndicator()))
				continue;

			double localLength = 0;
			if (i > 0)
				localLength += Math.abs(timeSeries.getTimestamp(i) - timeSeries.getTimestamp(i - 1)) / 2.0;
			if (i < timeSeries.size() - 1)
				localLength += Math.abs(timeSeries.getTimestamp(i + 1) - timeSeries.getTimestamp(i)) / 2.0;

			globalLength += localLength;
			means += (timeSeries.getValue(i) * localLength);
		}
		means /= globalLength;

		return means;
	}

	public static double getVariance(ITimeSeries<Double> timeSeries) {
		if (timeSeries == null)
			throw new IllegalStateException("TimeSeries is null");
		if (timeSeries.isEmpty())
			throw new IllegalStateException("TimeSeries is empty");

		double globalLength = 0;
		double variance = 0;
		double means = getMean(timeSeries);
		for (int i = 0; i < timeSeries.size(); i++) {
			// ignore missing values
			if (timeSeries.getValue(i) == null)
				continue;
			if (Double.isNaN(timeSeries.getValue(i)))
				continue;
			if (timeSeries.getMissingValueIndicator() != null
					&& compareDoubleObjects(timeSeries.getValue(i), timeSeries.getMissingValueIndicator()))
				continue;

			double localLength = 0;
			if (i > 0)
				localLength += Math.abs(timeSeries.getTimestamp(i) - timeSeries.getTimestamp(i - 1)) / 2.0;
			if (i < timeSeries.size() - 1)
				localLength += Math.abs(timeSeries.getTimestamp(i + 1) - timeSeries.getTimestamp(i)) / 2.0;
			variance += Math.pow((timeSeries.getValue(i) - means), 2) * localLength;
			globalLength += localLength;
		}

		if (globalLength == 0)
			return 0.0;

		variance /= globalLength;

		return variance;
	}

	public static double getStdDeviation(ITimeSeries<Double> timeSeries) {
		if (timeSeries == null)
			throw new IllegalStateException("TimeSeries is null");
		if (timeSeries.isEmpty())
			throw new IllegalStateException("TimeSeries is empty");

		return Math.sqrt(getVariance(timeSeries));
	}

	/**
	 * provides the trend of a time series in value domain units per millisecond.
	 * 
	 * Multiplying the result by longer time intervals allows trend assessment,
	 * e.g., per year (1000L * 60L * 60L * 24L* 365.2425);
	 * 
	 * Dividing the result by the mean value of the time series allows percentage
	 * trend assessment.
	 * 
	 * Attention: has problems with values between [-1 ... 1]
	 * 
	 * @param timeSeries time series
	 * @return double
	 */
	public static double getLinearTrend(ITimeSeries<Double> timeSeries) {
		if (timeSeries == null)
			throw new IllegalStateException("TimeSeries is null");
		if (timeSeries.isEmpty())
			throw new IllegalStateException("TimeSeries is empty");

		List<Double> timestamps = new ArrayList<Double>();
		List<Double> values = new ArrayList<Double>();

		for (int i = 0; i < timeSeries.size(); i++)
			if (timeSeries.getValue(i) != null && !Double.isNaN(timeSeries.getValue(i))) {
				timestamps.add((double) timeSeries.getTimestamp(i));
				values.add(timeSeries.getValue(i));
			}

		if (timestamps.size() == 0)
			return Double.NaN;

		if (timestamps.size() == 1)
			return 0.0;

		double timeMean = MathFunctions.getMean(timestamps);
		double valueMean = MathFunctions.getMean(values);
		double[] timeDeviation = new double[timestamps.size()];
		double[] valueDeviation = new double[timestamps.size()];
		double[] dxy = new double[timestamps.size()];
		double[] dxx = new double[timestamps.size()];

		for (int i = 0; i < timestamps.size(); i++) {
			timeDeviation[i] = (timestamps.get(i)) - timeMean;
			valueDeviation[i] = values.get(i) - valueMean;
			dxy[i] = timeDeviation[i] * valueDeviation[i];
			dxx[i] = timeDeviation[i] * timeDeviation[i];
		}

		double numerator = MathFunctions.getMean(dxy);
		double denominator = MathFunctions.getMean(dxx);

		return numerator / denominator;
	}

	private static double getMean(double[] x) {
		Objects.requireNonNull(x);

		if (x.length == 0) {
			System.err.println("TimeSeriesTools.getMean: data array length 0. returning Double.NaN");
			return Double.NaN;
		}

		double sum = 0;
		double count = 0;

		for (int i = 0; i < x.length; i++)
			if (!Double.isNaN(x[i])) {
				sum += x[i];
				count++;
			}

		return sum / count;
	}

	private static double getMean(Collection<Double> data) {
		Objects.requireNonNull(data);

		if (data.isEmpty()) {
			System.err.println("TimeSeriesTools.getMean: data size 0. returning Double.NaN");
			return Double.NaN;
		}

		double sum = 0;
		double count = 0;

		for (double d : data)
			if (!Double.isNaN(d)) {
				sum += d;
				count++;
			}

		return sum / count;
	}

	public static void calculateMovingAverageTimeSensitive(ITimeSeries<Double> ts, long window) {
		calculateMovingAverageTimeSensitive(ts, window, true);
	}

	public static void calculateMovingAverageTimeSensitive(ITimeSeries<Double> timeSeries, long window,
			boolean considerFutureValues) {
		if (timeSeries == null)
			return;

		LinearLongWeightingKernel kernel = new LinearLongWeightingKernel(window);

		List<Double> retValues = new ArrayList<>();

		for (int i = 0; i < timeSeries.size(); i++) {

			Long referenceTimeStamp = timeSeries.getTimestamp(i);
			kernel.setReference(referenceTimeStamp);

			int firstIndex = timeSeries.findByDate(
					Math.max(referenceTimeStamp - kernel.getInterval(), timeSeries.getFirstTimestamp()), false);
			int lastIndex = timeSeries.findByDate(
					Math.min(referenceTimeStamp + kernel.getInterval(), timeSeries.getLastTimestamp()), false);

			double values = 0;
			double weights = 0;

			for (int k = firstIndex; k <= lastIndex; k++)
				if (!considerFutureValues && k > i)
					break;
				else if (!Double.isNaN(timeSeries.getValue(k))) {
					double w = kernel.getWeight(timeSeries.getTimestamp(k));
					values += timeSeries.getValue(k) * w;
					weights += w;
				}

			if (weights <= 0)
				retValues.add(Double.NaN);
			else {
				values /= weights;
				retValues.add(values);
			}
		}

		for (int i = 0; i < timeSeries.size(); i++)
			timeSeries.replaceValue(i, retValues.get(i));

	}

	public static boolean compareDoubles(double v1, double v2) {
		if (Double.isNaN(v1) && Double.isNaN(v2))
			return true;
		if (v1 == v2)
			return true;
		return false;
	}

	public static boolean compareDoubleObjects(Double v1, Double v2) {
		if (v1 == null && v2 == null)
			return true;
		if (v1 == null)
			return false;
		if (v2 == null)
			return false;
		if (Double.isNaN(v1) && Double.isNaN(v2))
			return true;
		if (v1 == v2)
			return true;
		return false;
	}

	public static double[] getArray(List<Double> values) {
		if (values == null)
			return null;
		double[] ret = new double[values.size()];
		for (int i = 0; i < values.size(); i++)
			ret[i] = values.get(i);
		return ret;
	}

	public static ITimeValuePair<Double> getTimeValuePair(ITimeSeries<Double> timeSeries, int index) {
		if (timeSeries == null)
			throw new IllegalStateException("TimeSeries is null");
		if (timeSeries.size() - 1 < index)
			return null;
		return new TimeValuePairUnivariate(timeSeries.getTimestamp(index), timeSeries.getValue(index));
	}

	public static ITimeValuePair<Double> getTimeValuePair(ITimeSeries<Double> timeSeries, long timestamp) {
		if (timeSeries == null)
			throw new IllegalStateException("TimeSeries is null");
		if (timeSeries.isEmpty())
			throw new IllegalStateException("TimeSeries is empty");
		Double value = timeSeries.getValue(timestamp, false);
		return new TimeValuePairUnivariate(timestamp, value);
	}

	public static List<ITimeValuePair<Double>> getTimeValuePairs(ITimeSeries<Double> timeSeries) {
		if (timeSeries == null)
			throw new IllegalStateException("TimeSeries is null");
		List<ITimeValuePair<Double>> list = new ArrayList<ITimeValuePair<Double>>();
		for (int i = 0; i < timeSeries.size(); i++)
			list.add(new TimeValuePairUnivariate(timeSeries.getTimestamp(i), timeSeries.getValue(i)));
		return list;
	}

	public static ITimeValuePair<List<Double>> getTimeValuePair(ITimeSeriesMultivariate timeSeries, int index) {
		if (timeSeries == null)
			throw new IllegalStateException("TimeSeries is null");
		if (timeSeries.size() - 1 < index)
			throw new IllegalArgumentException("given time series is shorter than required index " + index);
		return new TimeValuePairMultivariate(timeSeries.getTimestamp(index), timeSeries.getValue(index));
	}

	public static List<Entry<Long, List<Double>>> getTimeValueLists(ITimeSeriesMultivariate timeSeries) {
		if (timeSeries == null)
			throw new IllegalStateException("TimeSeries is null");

		List<Entry<Long, List<Double>>> pairs = new ArrayList<Map.Entry<Long, List<Double>>>();
		for (int i = 0; i < timeSeries.size(); i++)
			pairs.add(new AbstractMap.SimpleEntry<Long, List<Double>>(timeSeries.getTimestamp(i),
					timeSeries.getValue(i)));

		return pairs;
	}

	public static TimeSeriesUnivariate createTimeSeries(Collection<ITimeValuePair<Double>> timeValuePairs,
			Double missingValueIndicator) {
		if (timeValuePairs == null)
			return null;

		List<Long> timeStamps = new ArrayList<>();
		List<Double> values = new ArrayList<>();

		for (ITimeValuePair<Double> pair : timeValuePairs) {
			timeStamps.add(pair.getTimestamp());
			values.add(pair.getValue());
		}

		return new TimeSeriesUnivariate(timeStamps, values, missingValueIndicator);
	}

	/**
	 * creates a time series with a constant value, e.g., to draw a horizontal
	 * threshold line in a line chart.
	 * 
	 * @param timeStamps
	 * @param constantValue
	 * @return
	 */
	public static ITimeSeriesUnivariate createConstantTimeSeries(List<Long> timeStamps, double constantValue) {
		List<Long> list = new ArrayList<>(timeStamps);
		Collections.sort(list);

		List<Double> values = new ArrayList<>();
		for (Long l : list)
			values.add(constantValue);

		return new TimeSeriesUnivariate(list, values);
	}

	public static long[] getQuantizationsAsLong(ITimeSeries<Double> timeSeries) {
		long[] quantization = new long[timeSeries.size() - 1];
		for (int i = 0; i < quantization.length; i++) {
			quantization[i] = timeSeries.getTimestamp(i + 1) - timeSeries.getTimestamp(i);
		}
		return quantization;
	}

	public static double[] getQuantizationAsDouble(ITimeSeries<Double> timeSeries) {
		double[] quantisation = new double[timeSeries.size() - 1];
		for (int i = 0; i < quantisation.length; i++) {
			quantisation[i] = timeSeries.getTimestamp(i + 1) - timeSeries.getTimestamp(i);
		}
		return quantisation;
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
		long equidistanceInMillis = equidistance.getTypeFactor();
		if (equidistance.getType().equals(TimeQuantization.MILLISECONDS)) {
		} else {
			equidistanceInMillis *= 1000;
			if (equidistance.getType().equals(TimeQuantization.SECONDS)) {
			} else {
				equidistanceInMillis *= 60;
				if (equidistance.getType().equals(TimeQuantization.MINUTES)) {
				} else {
					equidistanceInMillis *= 60;
					if (equidistance.getType().equals(TimeQuantization.HOURS)) {
					} else {
						equidistanceInMillis *= 24;
						if (equidistance.getType().equals(TimeQuantization.DAYS)) {
						} else if (equidistance.getType().equals(TimeQuantization.WEEKS)) {
							equidistanceInMillis *= 7;
						} else if (equidistance.getType().equals(TimeQuantization.MONTHS)) {
							equidistanceInMillis *= 30;
						} else if (equidistance.getType().equals(TimeQuantization.QUARTER)) {
							equidistanceInMillis *= 120;
						} else if (equidistance.getType().equals(TimeQuantization.YEARS)) {
							equidistanceInMillis *= 365;
						} else if (equidistance.getType().equals(TimeQuantization.DECADES)) {
							equidistanceInMillis *= 3650;
						}
					}
				}
			}
		}
		return equidistanceInMillis;
	}

	/**
	 * Provides a clone of a given time series.
	 * 
	 * @param timeSeries time series
	 * @return time series
	 */
	public static ITimeSeriesUnivariate cloneTimeSeries(ITimeSeriesUnivariate timeSeries) {
		if (timeSeries == null)
			return null;

		List<Long> times = new ArrayList<>();
		List<Double> values = new ArrayList<>();

		for (int i = 0; i < timeSeries.size(); i++) {
			times.add(new Long(timeSeries.getTimestamp(i)));
			values.add(new Double(timeSeries.getValue(i).doubleValue()));
		}

		TimeSeriesUnivariate returnTimeSeries = new TimeSeriesUnivariate(times, values, Double.NaN);
		if (timeSeries.getName() != null)
			returnTimeSeries.setName(new String(timeSeries.getName()));
		else
			returnTimeSeries.setName(null);

		if (timeSeries.getDescription() != null)
			returnTimeSeries.setDescription(new String(timeSeries.getDescription()));
		else
			returnTimeSeries.setDescription(null);

		if (timeSeries.getMissingValueIndicator() != null)
			returnTimeSeries.setMissingValueIndicator(new Double(timeSeries.getMissingValueIndicator()));

		if (timeSeries instanceof ITemporalLabeling<?>) {
			ITemporalLabeling<?> returnTimeSeriesLabeled = new TimeSeriesUnivariateLabeled(returnTimeSeries);
			returnTimeSeriesLabeled.setEventLabels(TimeSeriesLabelingTools
					.cloneEventLabels(((TimeSeriesUnivariateLabeled) timeSeries).getEventLabels()));
			returnTimeSeriesLabeled.setIntervalLabels(TimeSeriesLabelingTools
					.cloneIntervalLabels(((TimeSeriesUnivariateLabeled) timeSeries).getIntervalLabels()));
			return (ITimeSeriesUnivariate) returnTimeSeriesLabeled;
		}

		return returnTimeSeries;
	}

	/**
	 * 
	 * @param timeSeries time series
	 * @param time       time
	 * @return list
	 */
	public static List<Long> getNearestTimeStampNeighbors(ITimeSeries<Double> timeSeries, Long time) {

		List<Long> nearestNeighbors = new ArrayList<Long>();
		long timeStampLower = timeSeries.getFirstTimestamp();
		long timeStampUpper = timeSeries.getLastTimestamp();
		for (int i = 0; i < timeSeries.getTimestamps().size() - 1; i++) {
			timeStampLower = timeSeries.getTimestamp(i);
			timeStampUpper = timeSeries.getTimestamp(i + 1);
			if (timeStampLower <= time && time <= timeStampUpper) {
				nearestNeighbors.add(timeStampLower);
				nearestNeighbors.add(timeStampUpper);
				break;
			}
		}
		return nearestNeighbors;
	}

	/**
	 * returns the value of a time series at a given position in time (timeStamp).
	 * Uses left and right timeStamps for the linear interpolation.
	 * 
	 * @param timeSeries     time series
	 * @param leftTimeStamp  left
	 * @param rightTimeStamp right
	 * @param timeStamp      time stamp
	 * @return double
	 * @throws IllegalArgumentException  e
	 * @throws IndexOutOfBoundsException e
	 */
	public static double getInterpolatedValue(ITimeSeries<Double> timeSeries, long leftTimeStamp, long rightTimeStamp,
			long timeStamp) throws IllegalArgumentException, IndexOutOfBoundsException {

		Objects.requireNonNull(timeSeries);

		if (leftTimeStamp >= rightTimeStamp)
			throw new IllegalArgumentException("TimeSeriesTools.getInterpolatedValue: given time stamps are unsorted");

		if (leftTimeStamp < timeSeries.getFirstTimestamp() || leftTimeStamp > timeSeries.getLastTimestamp())
			throw new IndexOutOfBoundsException("TimeSeriesTools.getInterpolatedValue: given time out of bounds");

		if (rightTimeStamp < timeSeries.getFirstTimestamp() || rightTimeStamp > timeSeries.getLastTimestamp())
			throw new IndexOutOfBoundsException("TimeSeriesTools.getInterpolatedValue: given time out of bounds");

		if (timeStamp < timeSeries.getFirstTimestamp() || timeStamp > timeSeries.getLastTimestamp())
			throw new IndexOutOfBoundsException("TimeSeriesTools.getInterpolatedValue: given time out of bounds");

		// may throw an IllegalArgumentException if time stamps don't exist
		// may be improved by checking for NaN with while iterators
		double value1 = timeSeries.getValue(leftTimeStamp, false);
		double value2 = timeSeries.getValue(rightTimeStamp, false);

		return value1 + ((timeStamp - leftTimeStamp) / (double) (rightTimeStamp - leftTimeStamp) * (value2 - value1));
	}

	/**
	 * returns the interpolated value domain of a time series at a given position in
	 * time (timeStamp). Uses left and right timeStamps for the linear
	 * interpolation.
	 * 
	 * @param timeSeries time series
	 * @param timeStamp  time stamp
	 * @return double
	 */
	public static double getInterpolatedValue(ITimeSeries<Double> timeSeries, long timeStamp) {

		Objects.requireNonNull(timeSeries);

		if (timeStamp < timeSeries.getFirstTimestamp())
			throw new IndexOutOfBoundsException(
					"TimeSeriesTools.getInterpolatedValue: given target time stamp earlier than first time stamp of time series. Would be an extrapolation, though.");

		if (timeStamp > timeSeries.getLastTimestamp())
			throw new IndexOutOfBoundsException(
					"TimeSeriesTools.getInterpolatedValue: given target time stamp earlier than first time stamp of time series. Would be an extrapolation, though.");

		if (timeSeries.containsTimestamp(timeStamp))
			return timeSeries.getValue(timeStamp, false);

		// identify precessor and successor
		int index1 = timeSeries.findByDate(timeStamp, false);
		if (index1 >= 0) {
			Long leftTimeStamp = timeSeries.getTimestamp(index1);
			Long rightTimeStamp = timeSeries.getTimestamp(index1 + 1);

			return getInterpolatedValue(timeSeries, leftTimeStamp, rightTimeStamp, timeStamp);
		} else {
			Long ts = timeSeries.getFirstTimestamp();
			for (Long l : timeSeries.getTimestamps()) {
				if (l > timeStamp)
					return getInterpolatedValue(timeSeries, ts, l, timeStamp);
				ts = l;
			}
		}

		throw new IndexOutOfBoundsException(
				"TimeSeriesTools.getInterpolatedValue: ran through two more or less sophisticated algorithms but did not succeed in interpolating the value for time stamp "
						+ timeStamp + " .");
	}

	/**
	 * Attention: only uses the two very outer (earliest/latest) time-value pairs to
	 * make an extrapolation. use the predict method for a more sophisticated
	 * approach.
	 * 
	 * @param timeSeries time series
	 * @param target     the time stamp of the value to be extrapolated
	 * @return double
	 */
	public static double extrapolate(ITimeSeriesUnivariate timeSeries, long target) {
		Objects.requireNonNull(timeSeries);

		if (timeSeries.size() < 2)
			throw new IndexOutOfBoundsException(
					"TimeSeriesTools.extrapolate: cannot extrapolate for a time series with less than size two");

		if (target >= timeSeries.getFirstTimestamp() && target <= timeSeries.getLastTimestamp())
			return getInterpolatedValue(timeSeries, target);

		long leftTimeStamp = 0L;
		long rightTimeStamp = 0L;
		double value1 = Double.NaN;
		double value2 = Double.NaN;

		if (target < timeSeries.getFirstTimestamp()) {
			leftTimeStamp = timeSeries.getTimestamp(0);
			rightTimeStamp = timeSeries.getTimestamp(1);
		} else if (target > timeSeries.getLastTimestamp()) {
			leftTimeStamp = timeSeries.getTimestamp(timeSeries.size() - 2);
			rightTimeStamp = timeSeries.getTimestamp(timeSeries.size() - 1);
		} else
			throw new IndexOutOfBoundsException(
					"TimeSeriesTools.extrapolate: problem with extrapolating time series " + target);

		value1 = timeSeries.getValue(leftTimeStamp, false);
		value2 = timeSeries.getValue(rightTimeStamp, false);

		return value1 + ((target - leftTimeStamp) / (double) (rightTimeStamp - leftTimeStamp) * (value2 - value1));
	}

	/**
	 * 
	 * only towards the future direction. does not work for targets earlier than the
	 * time series.
	 * 
	 * Insanely slow for long time series with high quantization
	 * 
	 * @param timeSeries time series
	 * @param target     target
	 * @return doubles
	 */
	public static double[] predict(ITimeSeriesUnivariate timeSeries, long target) {
		Objects.requireNonNull(timeSeries);

		if (timeSeries.isEmpty())
			throw new IndexOutOfBoundsException("TimeSeriesTools.predict: cannot predict for an empty time series");

		return predict(timeSeries, target, timeSeries.getFirstTimestamp());
	}

	/**
	 * uses past pairwise segments to model a robust future value. an aggregation of
	 * a great many to be precise.
	 * 
	 * only towards the future direction. does not work for targets earlier than the
	 * time series.
	 * 
	 * Insanely slow for long time series with high quantization
	 * 
	 * @param timeSeries      time series
	 * @param target          target
	 * @param maxAgeTimeStamp the time stamp prior to the target from which on value
	 *                        prediction will have an impact on the result.
	 * @return doubles
	 */
	public static double[] predict(ITimeSeriesUnivariate timeSeries, long target, long maxAgeTimeStamp) {

		Objects.requireNonNull(timeSeries);

		if (timeSeries.isEmpty())
			throw new IndexOutOfBoundsException("TimeSeriesTools.predict: cannot predict for an empty time series");

		if (target < timeSeries.getFirstTimestamp())
			throw new IndexOutOfBoundsException(
					"TimeSeriesTools.predict: cannot predict into the past earlier than the time series");

		if (target >= timeSeries.getFirstTimestamp() && target <= timeSeries.getLastTimestamp())
			return new double[] { getInterpolatedValue(timeSeries, target), 0.0 };

		if (timeSeries.size() < 2) {
			double durationInYears = ((target - timeSeries.getLastTimestamp())
					/ (double) TimeSeriesTools.YEAR_IN_MILLISECONDS);
			double pow = Math.pow(0.5, durationInYears);
			double temporalUncertainty = Math.min(1.0, Math.max(0.0, 1 - pow));

			return new double[] { timeSeries.getValue(timeSeries.getLastTimestamp(), false),
					Math.max(0, Math.min(1.0, temporalUncertainty)) };
		}

		long maxAgeDuration = target - maxAgeTimeStamp;
		if (maxAgeDuration < 0)
			throw new IndexOutOfBoundsException(
					"TimeSeriesTools.predict: prediction target time stamp must be greater than maxAgeTimeStamp");
		maxAgeDuration *= 2; // because there will always be two time stamps involved for the calculations,
		// the ages of which will be added

		// identify all pairwise trends the considered past
		List<Double> trends = new ArrayList<>();
		List<Double> predictions = new ArrayList<>();
		List<Double> weights = new ArrayList<>();
		long deltaTimeUnknown = target - timeSeries.getLastTimestamp();
		for (long k : timeSeries.getTimestamps())
			for (long l : timeSeries.getTimestamps())
				if (l <= k)
					continue;
				else {
					double w = 1 - ((target - k) + (target - l)) / (double) maxAgeDuration;

					if (w <= 0)
						continue;

					double trend = (timeSeries.getValue(l, false) - timeSeries.getValue(k, false)) / (l - k);
					// value at last time stamp + trend* delta t until today
					double prediction = timeSeries.getValue(timeSeries.getLastTimestamp(), false)
							+ trend * (deltaTimeUnknown);

					trends.add(trend);
					predictions.add(prediction);
					weights.add(w);
				}

		// create weighted average
		double weightedAverage = 0.0;
		double weightsum = 0.0;
		for (int i = 0; i < predictions.size(); i++) {
			weightedAverage += (predictions.get(i) * weights.get(i));
			weightsum += weights.get(i);
		}
		weightedAverage /= weightsum;

		double weightedVariance = 0.0;
		for (int i = 0; i < predictions.size(); i++)
			weightedVariance += (java.lang.Math.pow(predictions.get(i) - weightedAverage, 2.0));
		weightedVariance = weightedVariance /= weightsum;
		double std = Math.sqrt(weightedVariance);

		double valueUncertainty = std / weightedAverage;

		double durationInYears = (deltaTimeUnknown / (double) TimeSeriesTools.YEAR_IN_MILLISECONDS);
		double pow = Math.pow(0.33, durationInYears);
		double temporalUncertainty = Math.min(1.0, Math.max(0.0, 1 - pow));

//		return new double[] { weightedAverage,
//				Math.max(0, Math.min(1.0, (valueUncertainty + temporalUncertainty) * 0.5)) };
		return new double[] { weightedAverage,
				Math.max(0, Math.min(1.0, Math.max(valueUncertainty, temporalUncertainty))) };
	}

	/**
	 * segments a given time series
	 * 
	 * @param timeSeries                     time series
	 * @param start                          star
	 * @param end                            end
	 * @param requireStartEndTimestampsExist whether or not the identification of
	 *                                       the subsequence is sort of
	 *                                       sophisticated in case of NOT exact
	 *                                       matches. If not a subsequence is
	 *                                       returned with start/end time stamps
	 *                                       larger than the defined interval.
	 * @return time series
	 */
	public static ITimeSeriesUnivariate getSubsequence(ITimeSeriesUnivariate timeSeries, long start, long end,
			boolean requireStartEndTimestampsExist) {
		if (timeSeries == null)
			return null;
		if (timeSeries.isEmpty())
			return null;
		if (start > end)
			return null;
		if (start < timeSeries.getFirstTimestamp())
			return null;
		if (end > timeSeries.getLastTimestamp())
			return null;

		int indexStart = timeSeries.findByDate(start, requireStartEndTimestampsExist);
		if (indexStart < 0) // lower bound violation
			return null;
		if (indexStart >= timeSeries.size()) // upper bound violation
			return null;

		int indexEnd = timeSeries.findByDate(end, requireStartEndTimestampsExist);
		if (timeSeries.getTimestamp(indexEnd) != end && indexEnd < timeSeries.size() - 1)
			indexEnd++;
		if (indexEnd < 0 && indexEnd >= timeSeries.size())
			return null;

		// change made here from >= to >. A time series with a single time stamp is
		// still a plausible time series
		if (indexStart > indexEnd)
			return null;

		List<Long> timestamps = new ArrayList<>();
		List<Double> values = new ArrayList<>();

		for (int i = indexStart; i <= indexEnd; i++) {
			timestamps.add(timeSeries.getTimestamp(i));
			values.add(timeSeries.getValue(i));
		}

		ITimeSeriesUnivariate returnTimeSeries = new TimeSeriesUnivariate(timestamps, values);
		return returnTimeSeries;
	}

	/**
	 * segments a given time series
	 * 
	 * @param timeSeries                     time series
	 * @param start                          start
	 * @param end                            end
	 * @param requireStartEndTimestampsExist whether or not the identification of
	 *                                       the subsequence requires exact matches
	 *                                       at start and end. If not a subsequence
	 *                                       is returned with start/end time stamps
	 *                                       larger than the defined interval.
	 * @param cropIfTimeStampsDontExist      if no exact match is needed this
	 *                                       routine crops start and end time stamps
	 *                                       by linear interpolation.
	 * @return time series
	 */
	public static ITimeSeriesUnivariate getSubsequence(ITimeSeriesUnivariate timeSeries, long start, long end,
			boolean requireStartEndTimestampsExist, boolean cropIfTimeStampsDontExist) {
		ITimeSeriesUnivariate subsequence = getSubsequence(timeSeries, start, end, false);

		if (subsequence == null)
			return null;

		if (requireStartEndTimestampsExist)
			return subsequence;

		// crop start time stamp
		if (subsequence.getFirstTimestamp() != start)
			if (subsequence.getFirstTimestamp() < start) {
				Double value = subsequence.getValue(start, true);
				subsequence.insert(start, value);
				subsequence.removeTimeValue(subsequence.getFirstTimestamp());
			} else {
				throw new IllegalArgumentException(
						"TimeSeriesTools.getSubsequence: unexpected return value. please verify.");
			}

		// crop end time stamp
		if (subsequence.getLastTimestamp() != end)
			if (subsequence.getLastTimestamp() > end) {
				Double value = subsequence.getValue(end, true);
				subsequence.insert(end, value);
				subsequence.removeTimeValue(subsequence.getLastTimestamp());
			} else {
				throw new IllegalArgumentException(
						"TimeSeriesTools.getSubsequence: unexpected return value. please verify");
			}

		return subsequence;
	}

	public enum TemporalCropStrategy {
		NextOuter, NextInner, ExactInterplation;
	}

	/**
	 * Creates a new time series with a temporal subset defined by method
	 * parameters.
	 * 
	 * No extrapolation is applied if the existing temporal subset does not cover
	 * the entire required interval (start end).
	 * 
	 * @param timeSeries           time series
	 * @param start                start
	 * @param end                  end
	 * @param temporalCropStrategy category
	 * @return time series
	 */
	public static ITimeSeriesUnivariate getSubsequence(ITimeSeries<Double> timeSeries, long start, long end,
			TemporalCropStrategy temporalCropStrategy) {

		Objects.requireNonNull(timeSeries);

		if (timeSeries.isEmpty())
			return null;

		if (start > end)
			throw new IllegalArgumentException(
					"TimeSeriesTools.getSubsequence: start time " + start + " < end time " + end);

		// interval not addressable?
		if (start > timeSeries.getLastTimestamp())
			return null;
		if (end < timeSeries.getFirstTimestamp())
			return null;

		int indexStart = 0;
		if (start > timeSeries.getFirstTimestamp())
			indexStart = timeSeries.findByDate(start, false);
		if (indexStart < 0 && indexStart >= timeSeries.size())
			return null;

		int indexEnd = timeSeries.size() - 1;
		if (end < timeSeries.getLastTimestamp())
			indexEnd = timeSeries.findByDate(end, false);
		if (timeSeries.getTimestamp(indexEnd) != end && indexEnd < timeSeries.size() - 1)
			indexEnd++;
		if (indexEnd < 0 && indexEnd >= timeSeries.size())
			return null;

		if (indexStart > indexEnd)
			return null;

		List<Long> timestamps = new ArrayList<>();
		List<Double> values = new ArrayList<>();

		for (int i = indexStart; i <= indexEnd; i++) {
			timestamps.add(timeSeries.getTimestamp(i));
			values.add(timeSeries.getValue(i));
		}

		ITimeSeriesUnivariate returnTimeSeries = new TimeSeriesUnivariate(timestamps, values);

		// check start
		switch (temporalCropStrategy) {
		case NextOuter:
			// nothing to do
			break;
		case NextInner:
			if (returnTimeSeries.getFirstTimestamp() < start)
				returnTimeSeries.removeTimeValue(0);
			break;
		case ExactInterplation:
			if (returnTimeSeries.size() > 1)
				if (returnTimeSeries.getFirstTimestamp() < start) {
					double value = getInterpolatedValue(returnTimeSeries, returnTimeSeries.getFirstTimestamp(),
							returnTimeSeries.getTimestamp(1), start);
					returnTimeSeries.insert(start, value);
					returnTimeSeries.removeTimeValue(0);
				}
			break;
		default:
			throw new IllegalArgumentException();
		}

		// check end
		switch (temporalCropStrategy) {
		case NextOuter:
			// nothing to do
			break;
		case NextInner:
			if (returnTimeSeries.getLastTimestamp() > end)
				returnTimeSeries.removeTimeValue(returnTimeSeries.size() - 1);
			break;
		case ExactInterplation:
			if (returnTimeSeries.size() > 1)
				if (returnTimeSeries.getLastTimestamp() > end) {
					double value = getInterpolatedValue(returnTimeSeries,
							returnTimeSeries.getTimestamp(returnTimeSeries.size() - 2),
							returnTimeSeries.getLastTimestamp(), end);
					returnTimeSeries.insert(end, value);
					returnTimeSeries.removeTimeValue(returnTimeSeries.size() - 1);
				}
			break;
		default:
			throw new IllegalArgumentException();
		}

		return returnTimeSeries;
	}

	/**
	 * segments a time series w.r.t. a given duration pattern.
	 * 
	 * @param timeSeries   time series
	 * @param timeDuration duration
	 * @return time series list
	 */
	public static List<ITimeSeriesUnivariate> segmentTimeSeries(ITimeSeriesUnivariate timeSeries,
			TimeDuration timeDuration) {
		if (timeSeries == null)
			return null;

		List<ITimeSeriesUnivariate> subSeqences = new ArrayList<ITimeSeriesUnivariate>();

		if (timeSeries.size() == 0)
			return subSeqences;

		long duration = calculateEquidistanceInMillis(timeDuration);

		for (long l = timeSeries.getFirstTimestamp(); l <= timeSeries.getLastTimestamp(); l += duration) {
			ITimeSeriesUnivariate subSeqence = TimeSeriesTools.getSubsequence(timeSeries, l, l + duration, false, true);
			if (subSeqence != null)
				subSeqences.add(subSeqence);
		}

		return subSeqences;
	}

	/**
	 * merges a list of time series. builds up on identical timestamps. does not
	 * check consistencies such as gaps in between series.
	 * 
	 * @param timeSeriesList time series
	 * @return time series
	 */
	public static ITimeSeriesUnivariate getMeanTimeSeries(Iterable<ITimeSeriesUnivariate> timeSeriesList) {
		SortedMap<Long, List<Double>> rawValues = new TreeMap<>();

		for (ITimeSeriesUnivariate ts : timeSeriesList)
			if (ts != null)
				for (Long timeStamp : ts.getTimestamps())
					if (!rawValues.containsKey(timeStamp))
						rawValues.put(timeStamp, new ArrayList<>());

		for (ITimeSeriesUnivariate ts : timeSeriesList)
			if (ts != null)
				for (int i = 0; i < ts.size(); i++) {
					Long timeStamp = ts.getTimestamp(i);
					Double value = ts.getValue(i);
					rawValues.get(timeStamp).add(value);
				}

		// create new meta time series and re-initialize the painter
		List<Long> timeStamps = new ArrayList<>();
		List<Double> means = new ArrayList<>();

		for (Long timeStamp : rawValues.keySet()) {
			timeStamps.add(timeStamp);
			means.add(MathFunctions.getMean(rawValues.get(timeStamp)));
		}

		ITimeSeriesUnivariate tsMean = new TimeSeriesUnivariate(timeStamps, means, Double.NaN);

		return tsMean;
	}

	/**
	 * @deprecated
	 * @param timeSeriesList
	 * @param quantil
	 * @return
	 */
	public static ITimeSeriesUnivariate[] createQuantilesQuartilesMeanTimeSeries(
			Collection<ITimeSeriesUnivariate> timeSeriesList, double quantil) {
		return computeQuantilesQuartilesMeanTimeSeries(timeSeriesList, quantil);
	}

	/**
	 * returns six time series for a list of time series
	 * 
	 * <p>
	 * [0] lower quantile [1] lower quartile [2] mean [3] median [4] upper quartile
	 * [5] upper quantile
	 * </p>
	 * 
	 * @param timeSeriesList time series
	 * @param quantil        between [0-100]
	 * @return time series
	 */
	public static ITimeSeriesUnivariate[] computeQuantilesQuartilesMeanTimeSeries(
			Iterable<ITimeSeriesUnivariate> timeSeriesList, double quantil) {

		// gather all available time stamps
		SortedSet<Long> timeStamps = new TreeSet<>();
		for (ITimeSeries<? extends Double> timeSeries : timeSeriesList)
			for (long l : timeSeries.getTimestamps())
				timeStamps.add(l);

		List<Double> lowerQuantil = new ArrayList<>();
		List<Double> lowerQuartil = new ArrayList<>();
		List<Double> mean = new ArrayList<>();
		List<Double> median = new ArrayList<>();
		List<Double> upperQuartil = new ArrayList<>();
		List<Double> upperQuantil = new ArrayList<>();

		// calculate statistical information
		for (Long timeStamp : timeStamps) {
			List<Double> values = new ArrayList<>();

			// variant where only existing time stamps are used, leading to a created times
			// series with time stamps based on few values
			// for (ITimeSeries<? extends Double> timeSeries : timeSeriesList) {
			// if (timeSeries.containsTimestamp(timeStamp)) {
			// Double v = timeSeries.getValue(timeStamp, false);
			// if (v != null && !Double.isNaN(v))
			// values.add(v);
			// }
			// }

			// variant where every time series provides interpolated time stamps also, to
			// have values for every time stamp asked for.
			// Note: takes longer (three times in a recent comparison). If this becomes a
			// problem, sampling of time stamps may be a useful approach.
			for (ITimeSeries<? extends Double> timeSeries : timeSeriesList) {
				if (timeSeries.getFirstTimestamp() > timeStamp)
					continue;
				if (timeSeries.getLastTimestamp() < timeStamp)
					continue;

				Double v = timeSeries.getValue(timeStamp, true);
				if (v != null && !Double.isNaN(v))
					values.add(v);
			}

			StatisticsSupport statistics = new StatisticsSupport(values);
			// System.out.println(statistics.getCount());

			lowerQuantil.add(statistics.getPercentile(quantil));
			lowerQuartil.add(statistics.getPercentile(25));
			mean.add(statistics.getMean());
			median.add(statistics.getMedian());
			upperQuartil.add(statistics.getPercentile(75));
			upperQuantil.add(statistics.getPercentile(100 - quantil));
		}

		// create output time series
		ITimeSeriesUnivariate[] timeSeriesArray = new TimeSeriesUnivariate[6];

		timeSeriesArray[0] = new TimeSeriesUnivariate(new ArrayList<>(timeStamps), lowerQuantil);
		timeSeriesArray[1] = new TimeSeriesUnivariate(new ArrayList<>(timeStamps), lowerQuartil);
		timeSeriesArray[2] = new TimeSeriesUnivariate(new ArrayList<>(timeStamps), mean);
		timeSeriesArray[3] = new TimeSeriesUnivariate(new ArrayList<>(timeStamps), median);
		timeSeriesArray[4] = new TimeSeriesUnivariate(new ArrayList<>(timeStamps), upperQuartil);
		timeSeriesArray[5] = new TimeSeriesUnivariate(new ArrayList<>(timeStamps), upperQuantil);

		return timeSeriesArray;
	}

	/**
	 * returns an integer time series representing the count of existing time
	 * stamps.
	 * 
	 * @param timeSeriesList    the time series time series.
	 * @param requireExactMatch if, for a time stamp, a time stamp in a time series
	 *                          must exist (true), of if within start-end interval
	 *                          is a sufficient criterion (false).
	 * @return time series
	 */
	public static ITimeSeriesUnivariate getCountsTimeSeries(Iterable<ITimeSeriesUnivariate> timeSeriesList,
			boolean requireExactMatch) {

		// gather all available time stamps
		SortedSet<Long> timeStamps = new TreeSet<>();
		for (ITimeSeries<? extends Double> timeSeries : timeSeriesList)
			for (long l : timeSeries.getTimestamps())
				timeStamps.add(l);

		List<Double> counts = new ArrayList<>();

		// calculate counts
		for (Long timeStamp : timeStamps) {
			double i = 0;
			for (ITimeSeries<? extends Double> timeSeries : timeSeriesList)
				if (requireExactMatch)
					i = timeSeries.containsTimestamp(timeStamp) ? i++ : i;
				else if (!timeSeries.isEmpty() && timeSeries.getFirstTimestamp() <= timeStamp
						&& timeSeries.getLastTimestamp() >= timeStamp)
					i++;

			counts.add(i);
		}

		return new TimeSeriesUnivariate(new ArrayList<>(timeStamps), counts);
	}

	/**
	 * Checks whether a timeSeries is equidistant. A time series is equidistant iff
	 * the time intervals (the quantization) between any to time stamps are equal.
	 * 
	 * @param timeSeries time series
	 * @return boolean
	 */
	public static boolean isEquidistant(ITimeSeries<Double> timeSeries) {
		long[] quantizations = getQuantizationsAsLong(timeSeries);

		if (quantizations == null)
			return false;

		if (quantizations.length == 0)
			return false;

		long last = quantizations[0];
		for (int i = 1; i == quantizations.length; i++)
			if (quantizations[i] != last)
				return false;
			else
				last = quantizations[i];

		return true;
	}

	/**
	 * gathers the average value within a given interval.
	 * 
	 * @param timeSeries time series
	 * @param start      start
	 * @param end        end
	 * @return value
	 */
	public static double getValueFromInterval(ITimeSeries<? extends Double> timeSeries, long start, long end) {
		if (start > timeSeries.getLastTimestamp())
			throw new IllegalArgumentException(
					"start time > time series end time (" + start + " vs. " + timeSeries.getLastTimestamp() + ")");

		if (end < timeSeries.getFirstTimestamp())
			throw new IllegalArgumentException(
					"end time < time series start time (" + end + " vs. " + timeSeries.getFirstTimestamp() + ")");

		if (end < start)
			throw new IllegalArgumentException("end < start");

		if (end == start)
			return timeSeries.getValue(start, true);

		double areaUnderCurve = 0;

		int indexStart = timeSeries.findByDate(Math.max(start, timeSeries.getFirstTimestamp()), false);
		Long leftTimeStamp = timeSeries.getTimestamp(indexStart);

		// if first index is outside the range
		if (start > leftTimeStamp)
			indexStart++;

		int indexEnd = timeSeries.findByDate(Math.min(end, timeSeries.getLastTimestamp()), false);

		// if indexEnd is later than end
		if (timeSeries.getTimestamp(indexEnd) > end)
			indexEnd--;

		// iterate over valid indices
		double sum = 0;
		for (int i = indexStart; i <= indexEnd; i++) {
			if (i > 0) {
				Long l1 = timeSeries.getTimestamp(i - 1);
				Long l2 = timeSeries.getTimestamp(i);
				double deltaT = (l2 - l1) * 0.5;
				double v = timeSeries.getValue(i);
				areaUnderCurve += (v * deltaT);
				sum += deltaT;
			}
			if (i < timeSeries.size() - 1) {
				Long l1 = timeSeries.getTimestamp(i);
				Long l2 = timeSeries.getTimestamp(i + 1);
				double deltaT = (l2 - l1) * 0.5;
				double v = timeSeries.getValue(i);
				areaUnderCurve += (v * deltaT);
				sum += deltaT;
			}
		}

		return areaUnderCurve / sum;
	}

	/**
	 * calculates a time series that represents the delta of the value domain of the
	 * original time series between any two neighboring time stamps. Does take the
	 * duration between time stamps into account, normalized to years.
	 * 
	 * @param timeSeries     time series
	 * @param relativeValues if percentage values of change is desired. be aware
	 *                       that relative time series may produce infinite values
	 *                       due to the division-by-zero problem.
	 * @return time series
	 */
	public static ITimeSeriesUnivariate getChangeTimeSeries(ITimeSeriesUnivariate timeSeries, boolean relativeValues) {

		Objects.requireNonNull(timeSeries);

		List<ITimeValuePair<Double>> timeValuePairs = new ArrayList<>();

		Long lastTimeStamp = null;
		Double lastValue = null;
		for (Long timeStamp : timeSeries.getTimestamps()) {
			double value = timeSeries.getValue(timeStamp, false);

			if (lastValue != null && lastTimeStamp != null) {
				long dur = Math.abs(timeStamp - lastTimeStamp);
				double y = dur / (double) DateTools.YEAR_IN_MILLISECONDS_EXACT;

				if (relativeValues) {
					if (lastValue == 0)
						timeValuePairs.add(new TimeValuePairUnivariate(timeStamp, 0.0));
					else {
						double relative = ((value - lastValue) / Math.abs(lastValue)) * 100 / y;
						timeValuePairs.add(new TimeValuePairUnivariate(timeStamp, relative));
					}
				} else {
					double diff = (value - lastValue) / y;
					timeValuePairs.add(new TimeValuePairUnivariate(timeStamp, diff));
				}
			}

			lastTimeStamp = timeStamp;
			lastValue = value;
		}

		return TimeSeriesUnivariateFactory.newTimeSeries(timeValuePairs);
	}

	/**
	 * calculates a time series that represents the delta of the value domain of the
	 * original time series between a given time stamp and an interpolated time
	 * stamp in the past with temporal distance of comparisonDuration. Does take the
	 * duration between time stamps into account, normalized to years.
	 * 
	 * @param timeSeries         time series
	 * @param relativeValues     if percentage values of change is desired. be aware
	 *                           that relative time series may produce infinite
	 *                           values due to the division-by-zero problem.
	 * @param comparisonDuration duration between two time stamps for the change
	 *                           comparison
	 * @return time series
	 */
	public static ITimeSeriesUnivariate getChangeTimeSeries(ITimeSeriesUnivariate timeSeries, boolean relativeValues,
			TimeDuration comparisonDuration) {

		Objects.requireNonNull(timeSeries);
		Objects.requireNonNull(comparisonDuration);

		List<ITimeValuePair<Double>> timeValuePairs = new ArrayList<>();

		for (Long timeStamp : timeSeries.getTimestamps()) {
			double value = timeSeries.getValue(timeStamp, false);
			long earlier = timeStamp - comparisonDuration.getDuration();

			if (earlier < timeSeries.getFirstTimestamp())
				continue;

			double lastValue = getInterpolatedValue(timeSeries, earlier);

			if (!Double.isNaN(lastValue)) {
				long dur = Math.abs(timeStamp - earlier);
				double y = dur / (double) DateTools.YEAR_IN_MILLISECONDS_EXACT;

				if (relativeValues) {
					if (lastValue == 0) {
					} else {
						double relative = ((value - lastValue) / Math.abs(lastValue)) * 100 / y;
						timeValuePairs.add(new TimeValuePairUnivariate(timeStamp, relative));
					}
				} else {
					double diff = (value - lastValue) / y;
					timeValuePairs.add(new TimeValuePairUnivariate(timeStamp, diff));
				}
			}
		}

		return TimeSeriesUnivariateFactory.newTimeSeries(timeValuePairs);
	}

	/**
	 * creates a time series with a value domain according to the LN where possible.
	 * 
	 * NOTE: Negative values can be computed using a negation trick.
	 * 
	 * NOTE: Values in the interval [-2...2] are handled with a linear function
	 * (*0,345) to fit the overall ln curve of the function.
	 * 
	 * @param timeSeries          time series
	 * @param allowNegativeValues uses negation to produce numbers. if false NaN is
	 *                            returned for particular time stamps.
	 * @return time series
	 */
	public static ITimeSeriesUnivariate getLogarithmLikeTimeSeries(ITimeSeriesUnivariate timeSeries,
			boolean allowNegativeValues) {

		Objects.requireNonNull(timeSeries);

		List<ITimeValuePair<Double>> timeValuePairs = new ArrayList<>();

		for (Long timeStamp : timeSeries.getTimestamps()) {
			double value = timeSeries.getValue(timeStamp, false);
			double lnValue = Double.NaN;
			if (value < 0 && !allowNegativeValues)
				lnValue = Double.NaN;
			else if (value < 2 && value > -2)
				lnValue = value * 0.345;
			else {
				lnValue = Math.log(Math.abs(value));
				lnValue *= ((value < 0) ? -1 : 1);
			}

			timeValuePairs.add(new TimeValuePairUnivariate(timeStamp, lnValue));
		}

		return TimeSeriesUnivariateFactory.newTimeSeries(timeValuePairs);
	}

	/**
	 * subtracts a constant value and returns a new time series
	 * 
	 * @param timeSeries time series
	 * @param value
	 * @return time series
	 */
	public static ITimeSeriesUnivariate getSubstractedValueTimeSeries(ITimeSeriesUnivariate timeSeries, double value) {

		Objects.requireNonNull(timeSeries);

		List<ITimeValuePair<Double>> timeValuePairs = new ArrayList<>();

		for (int i = 0; i < timeSeries.size(); i++) {
			double v = timeSeries.getValue(i);
			timeValuePairs.add(new TimeValuePairUnivariate(timeSeries.getTimestamp(i), v - value));
		}

		return TimeSeriesUnivariateFactory.newTimeSeries(timeValuePairs);
	}

	/**
	 * adds a constant value and returns a new time series
	 * 
	 * @param timeSeries time series
	 * @param value
	 * @return time series
	 */
	public static ITimeSeriesUnivariate getAddedValueTimeSeries(ITimeSeriesUnivariate timeSeries, double value) {
		return getSubstractedValueTimeSeries(timeSeries, -value);
	}

	public static long getFirstTimestamp(Collection<ITimeSeriesUnivariate> timeSeriesList) {
		Objects.requireNonNull(timeSeriesList);

		if (timeSeriesList.size() == 0)
			throw new IllegalArgumentException(
					"TimeSeriesTools: unable to retrieve first time stamp for empty list of time series");

		long l = Long.MAX_VALUE;

		for (ITimeSeriesUnivariate ts : timeSeriesList)
			if (ts != null)
				l = Math.min(l, ts.getFirstTimestamp());

		return l;
	}

	public static long getLastTimestamp(Collection<ITimeSeriesUnivariate> timeSeriesList) {
		Objects.requireNonNull(timeSeriesList);

		if (timeSeriesList.size() == 0)
			throw new IllegalArgumentException(
					"TimeSeriesTools: unable to retrieve first time stamp for empty list of time series");

		long l = Long.MIN_VALUE;

		for (ITimeSeriesUnivariate ts : timeSeriesList)
			if (ts != null)
				l = Math.max(l, ts.getLastTimestamp());

		return l;
	}

	/**
	 * checks whether a time series contains NaN.
	 * 
	 * @param timeSeries time series
	 * @return boolean
	 */
	public static boolean containsNaN(ITimeSeries<? extends Double> timeSeries) {
		for (Double d : timeSeries.getValues())
			if (Double.isNaN(d))
				return true;

		return false;
	}

	/**
	 * removes all occurrences / time value pairs with values equals a given T.
	 * 
	 * @param <T>        type of object
	 * @param timeSeries time series
	 * @param value      value
	 */
	public static <T> void remove(ITimeSeries<T> timeSeries, T value) {
		if (timeSeries == null)
			return;

		if (timeSeries.isEmpty())
			return;

		int i = 0;
		while (i < timeSeries.size()) {
			if (value instanceof Double) {
				double d = doubleParser.apply(value);
				if (d == doubleParser.apply(timeSeries.getValue(i)))
//				if (Double.isNaN(d) && Double.isNaN(doubleParser.apply(timeSeries.getValue(i))))
					timeSeries.removeTimeValue(i);
				else
					i++;
			} else if (timeSeries.getValue(i) == value)
				timeSeries.removeTimeValue(i);
			else
				i++;
		}
	}

	/**
	 * clamps values of the time series larger/smaller than a given minimum/maximum.
	 * 
	 * @param timeSeries
	 * @param minValue
	 * @param maxValue
	 */
	public static ITimeSeriesUnivariate clampTimeSeries(ITimeSeriesUnivariate timeSeries, Double minValue,
			Double maxValue) {

		for (Long l : timeSeries.getTimestamps()) {
			if (timeSeries.getValue(l, false) < minValue)
				timeSeries.replaceValue(l, minValue);
			if (timeSeries.getValue(l, false) > maxValue)
				timeSeries.replaceValue(l, maxValue);
		}

		return timeSeries;
	}

}
