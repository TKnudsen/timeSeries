package com.github.TKnudsen.timeseries.operations.tools;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.github.TKnudsen.ComplexDataObject.model.tools.MathFunctions;
import com.github.TKnudsen.timeseries.data.ITemporalLabeling;
import com.github.TKnudsen.timeseries.data.ITimeValuePair;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.primitives.TimeQuantization;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariateLabeled;
import com.github.TKnudsen.timeseries.data.univariate.TimeValuePairUnivariate;

/**
 * <p>
 * Title: TimeSeriesTools
 * </p>
 * 
 * <p>
 * Description: tools class for general statistical operations and routines
 * applied on univariate time series
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.07
 */
public final class TimeSeriesTools {

	private TimeSeriesTools() {
	}

	/**
	 * retrieves the minimum time stamp of all time series
	 * 
	 * @param timeSeries
	 * @return
	 */
	public static long getMinStart(List<ITimeSeriesUnivariate> timeSeries) {
		long min = Long.MAX_VALUE - 1;

		for (ITimeSeriesUnivariate ts : timeSeries)
			if (ts != null)
				min = Math.min(min, ts.getFirstTimestamp());

		if (min == Long.MAX_VALUE - 1)
			return Long.MIN_VALUE;

		return min;
	}

	/**
	 * retrieves the minimum time stamp of all time series
	 * 
	 * @param timeSeries
	 * @return
	 */
	public static long getMaxEnd(List<ITimeSeriesUnivariate> timeSeries) {
		long max = Long.MIN_VALUE + 1;

		for (ITimeSeriesUnivariate ts : timeSeries)
			if (ts != null)
				max = Math.max(max, ts.getLastTimestamp());

		if (max == Long.MIN_VALUE + 1)
			return Long.MAX_VALUE;

		return max;
	}

	public static double getMinValue(ITimeSeriesUnivariate ts) {
		if (ts == null)
			throw new IllegalStateException("TimeSeries is null");
		if (ts.isEmpty())
			throw new IllegalStateException("TimeSeries is empty");

		double min = Double.POSITIVE_INFINITY;
		for (double d : ts.getValues())
			if (Double.isNaN(d))
				continue;
			else
				min = Math.min(min, d);
		return min;
	}

	public static double getMaxValue(ITimeSeriesUnivariate ts) {
		if (ts == null)
			throw new IllegalStateException("TimeSeries is null");
		if (ts.isEmpty())
			throw new IllegalStateException("TimeSeries is empty");

		double max = Double.NEGATIVE_INFINITY;
		for (double d : ts.getValues())
			if (Double.isNaN(d))
				continue;
			else
				max = Math.max(max, d);
		return max;
	}

	public static double getMean(ITimeSeriesUnivariate ts) {
		if (ts == null)
			throw new IllegalStateException("TimeSeries is null");
		if (ts.isEmpty())
			throw new IllegalStateException("TimeSeries is empty");

		double globalLength = 0;
		double means = 0;
		for (int i = 0; i < ts.size(); i++) {
			double localLength = 0;
			if (i > 0)
				localLength += Math.abs(ts.getTimestamp(i) - ts.getTimestamp(i - 1)) / 2.0;
			if (i < ts.size() - 1)
				localLength += Math.abs(ts.getTimestamp(i + 1) - ts.getTimestamp(i)) / 2.0;
			// missing values handle
			globalLength += localLength; // TODO before or after the continue?
			if (compareDoubles(ts.getValue(i), ts.getMissingValueIndicator()))
				continue;
			means += (ts.getValue(i) * localLength);
		}
		means /= globalLength;

		return means;
	}

	public static double getVariance(ITimeSeriesUnivariate ts) {
		if (ts == null)
			throw new IllegalStateException("TimeSeries is null");
		if (ts.isEmpty())
			throw new IllegalStateException("TimeSeries is empty");

		double globalLength = 0;
		double variance = 0;
		double means = getMean(ts);
		for (int i = 0; i < ts.size(); i++) {
			double localLength = 0;
			if (i > 0)
				localLength += Math.abs(ts.getTimestamp(i) - ts.getTimestamp(i - 1)) / 2.0;
			if (i < ts.size() - 1)
				localLength += Math.abs(ts.getTimestamp(i + 1) - ts.getTimestamp(i)) / 2.0;
			variance += Math.pow((ts.getValue(i) - means), 2) * localLength;
			globalLength += localLength;
		}
		variance /= globalLength;

		return variance;
	}

	public static double getStdDeviation(ITimeSeriesUnivariate ts) {
		if (ts == null)
			throw new IllegalStateException("TimeSeries is null");
		if (ts.isEmpty())
			throw new IllegalStateException("TimeSeries is empty");

		return Math.sqrt(getVariance(ts));
	}

	public static double getLinearTrend(ITimeSeriesUnivariate ts) {
		if (ts == null)
			throw new IllegalStateException("TimeSeries is null");
		if (ts.isEmpty())
			throw new IllegalStateException("TimeSeries is empty");

		List<Double> timestamps = new ArrayList<Double>();
		List<Double> values = new ArrayList<Double>();

		for (int i = 0; i < ts.size(); i++)
			if (!Double.isNaN(ts.getValue(i))) {
				timestamps.add((double) ts.getTimestamp(i));
				values.add(ts.getValue(i));
			}

		double timeMean = getMean(timestamps);
		double valueMean = getMean(values);
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

		double numerator = getMean(dxy);
		double denominator = getMean(dxx);

		return numerator / denominator;
	}

	private static double getMean(double[] x) {
		double sum = 0;
		for (int i = 0; i < x.length; i++) {
			sum += x[i];
		}
		double d = sum / (double) x.length;
		return d;
	}

	private static double getMean(List<Double> data) {
		double sum = 0;
		for (double d : data)
			sum += d;

		double mean = sum / (double) data.size();
		return mean;
	}

	public static void calculateMovingAverageTimeSensitive(ITimeSeriesUnivariate ts, long window) {
		for (int i = 0; i < ts.size(); i++) {
			double d = 0;
			double w = 0;
			if (!Double.isNaN(ts.getValue(i))) {
				d = ts.getValue(i);
				w = 1.0;
			}
			long currentTime = ts.getTimestamp(i);
			long movingTime = ts.getTimestamp(i);
			int currentIndex = i - 1;

			while (currentIndex >= 0 && ts.getTimestamp(currentIndex) >= currentTime - window) {
				movingTime = ts.getTimestamp(currentIndex);
				if (!Double.isNaN(ts.getValue(currentIndex))) {
					if (1 - ((double) (currentTime - movingTime) / (double) window) < 0.000001) {
						// we skip this index due to the fact of a division
						// by a almost zero weight value
					} else {
						d += ts.getValue(currentIndex) * (1 - (double) (currentTime - movingTime) / (double) window);
						w += 1 - ((double) (currentTime - movingTime) / (double) window);
					}
				}
				currentIndex--;
			}

			movingTime = ts.getTimestamp(i);
			currentIndex = i + 1;

			while (currentIndex < ts.size() && ts.getTimestamp(currentIndex) < currentTime + window) {
				movingTime = ts.getTimestamp(currentIndex);
				if (!Double.isNaN(ts.getValue(currentIndex))) {
					if (1 - ((double) (movingTime - currentTime) / (double) window) < 0.000001) {
						// we skip this index due to the fact of a division
						// by a almost zero weight value
					} else {
						d += ts.getValue(currentIndex) * (1 - (double) (movingTime - currentTime) / (double) window);
						w += 1 - ((double) (movingTime - currentTime) / (double) window);
					}
				}
				currentIndex++;
			}

			if (d > 0 && d < 0.0000000001)
				ts.replaceValue(i, 0.0);
			else
				ts.replaceValue(i, d / w);
		}
	}

	public static boolean compareDoubles(double v1, double v2) {
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

	public static ITimeValuePair<Double> getTimeValuePair(ITimeSeriesUnivariate ts, int i) {
		if (ts == null)
			throw new IllegalStateException("TimeSeries is null");
		if (ts.size() - 1 < i)
			return null;
		return new TimeValuePairUnivariate(ts.getTimestamp(i), ts.getValue(i));
	}

	public static List<ITimeValuePair<Double>> getTimeValuePairs(ITimeSeriesUnivariate ts) {
		if (ts == null)
			throw new IllegalStateException("TimeSeries is null");
		List<ITimeValuePair<Double>> list = new ArrayList<ITimeValuePair<Double>>();
		for (int i = 0; i < ts.size(); i++)
			list.add(new TimeValuePairUnivariate(ts.getTimestamp(i), ts.getValue(i)));
		return list;
	}

	public static List<Entry<Long, List<Double>>> getTimeValueLists(ITimeSeriesMultivariate ts) {
		if (ts == null)
			throw new IllegalStateException("TimeSeries is null");

		List<Entry<Long, List<Double>>> pairs = new ArrayList<Map.Entry<Long, List<Double>>>();
		for (int i = 0; i < ts.size(); i++)
			pairs.add(new AbstractMap.SimpleEntry<Long, List<Double>>(ts.getTimestamp(i), ts.getValue(i)));

		return pairs;
	}

	public static ITimeSeriesUnivariate createTimeSeries(List<ITimeValuePair<Double>> timeValuePairs, Double missingValueIndicator) {
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

	public static long[] getQuantizationAsLong(ITimeSeriesUnivariate ts) {
		long[] quantisation = new long[ts.size() - 1];
		for (int i = 0; i < quantisation.length; i++) {
			quantisation[i] = ts.getTimestamp(i + 1) - ts.getTimestamp(i);
		}
		return quantisation;
	}

	public static double[] getQuantizationAsDouble(ITimeSeriesUnivariate ts) {
		double[] quantisation = new double[ts.size() - 1];
		for (int i = 0; i < quantisation.length; i++) {
			quantisation[i] = ts.getTimestamp(i + 1) - ts.getTimestamp(i);
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
	 * series interval. if the TimeQuantization of the patternInterval is at
	 * least of one day length, the next 00:00:00 GMT time is achieved
	 * 
	 * @param startTime
	 * @param patternInterval
	 * @return
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
	 * @param timeSeries
	 * @return
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
			returnTimeSeriesLabeled.setEventLabels(TimeSeriesLabelingTools.cloneEventLabels(((TimeSeriesUnivariateLabeled) timeSeries).getEventLabels()));
			returnTimeSeriesLabeled.setIntervalLabels(TimeSeriesLabelingTools.cloneIntervalLabels(((TimeSeriesUnivariateLabeled) timeSeries).getIntervalLabels()));
			return (ITimeSeriesUnivariate) returnTimeSeriesLabeled;
		}

		return returnTimeSeries;
	}

	public static double getInterpolatedValue(ITimeSeriesUnivariate timeSeries, Long time1, Long time2, Long target) throws IllegalArgumentException, IndexOutOfBoundsException {
		if (timeSeries == null || time1 == null || time2 == null || target == null)
			throw new IllegalArgumentException("TimeSeriesTools.getInterpolatedValue: given time stamps null");

		if (time1 >= time2)
			throw new IllegalArgumentException("TimeSeriesTools.getInterpolatedValue: given time stamps are unsorted");

		if (time1 < timeSeries.getFirstTimestamp() || time1 > timeSeries.getLastTimestamp())
			throw new IndexOutOfBoundsException("TimeSeriesTools.getInterpolatedValue: given time out of bounds");

		if (time2 < timeSeries.getFirstTimestamp() || time2 > timeSeries.getLastTimestamp())
			throw new IndexOutOfBoundsException("TimeSeriesTools.getInterpolatedValue: given time out of bounds");

		if (target < timeSeries.getFirstTimestamp() || target > timeSeries.getLastTimestamp())
			throw new IndexOutOfBoundsException("TimeSeriesTools.getInterpolatedValue: given time out of bounds");

		// may throw an IllegalArgumentException if time stamps don't exist
		double value1 = timeSeries.getValue(time1, false);
		double value2 = timeSeries.getValue(time2, false);

		return value1 + ((target - time1) / (double) (time2 - time1) * (value2 - value1));
	}

	/**
	 * segments a given time series
	 * 
	 * @param timeSeries
	 * @param start
	 * @param end
	 * @param requireExactMatch
	 *            whether or not the identification of the subsequencec is sort
	 *            of sophisticated in case of NOT exact matches.
	 * @return
	 */
	public static ITimeSeriesUnivariate getSubsequence(ITimeSeriesUnivariate timeSeries, long start, long end, boolean requireExactMatch) {
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

		int indexStart = timeSeries.findByDate(start, requireExactMatch);
		if (indexStart < 0 && indexStart >= timeSeries.size())
			return null;

		int indexEnd = timeSeries.findByDate(end, requireExactMatch);
		if (indexEnd < 0 && indexEnd >= timeSeries.size())
			return null;

		if (indexStart >= indexEnd)
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
	 * segments a time series w.r.t. a given duration pattern.
	 * 
	 * @param timeSeries
	 * @param timeDuration
	 * @return
	 */
	public static List<ITimeSeriesUnivariate> segmentTimeSeries(ITimeSeriesUnivariate timeSeries, TimeDuration timeDuration) {
		if (timeSeries == null)
			return null;

		List<ITimeSeriesUnivariate> subSeqences = new ArrayList<ITimeSeriesUnivariate>();

		if (timeSeries.size() == 0)
			return subSeqences;

		long duration = calculateEquidistanceInMillis(timeDuration);

		for (long l = timeSeries.getFirstTimestamp(); l <= timeSeries.getLastTimestamp(); l += duration) {
			ITimeSeriesUnivariate subSeqence = TimeSeriesTools.getSubsequence(timeSeries, l, l + duration, true);
			if (subSeqence != null)
				subSeqences.add(subSeqence);
		}

		return subSeqences;
	}

	/**
	 * merges a list of time series. builds up on identical timestamps. does not
	 * check consistencies such as gaps in between series.
	 * 
	 * @param timeSeriesList
	 * @return
	 */
	public static ITimeSeriesUnivariate mergeTimeSeries(List<ITimeSeriesUnivariate> timeSeriesList) {
		SortedMap<Long, List<Double>> rawValues = new TreeMap<>();

		for (ITimeSeriesUnivariate ts : timeSeriesList)
			if (ts != null)
				for (Long timeStamp : ts.getTimestamps())
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
}
