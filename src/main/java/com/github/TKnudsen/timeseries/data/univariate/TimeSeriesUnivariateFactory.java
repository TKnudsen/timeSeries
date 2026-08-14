package com.github.TKnudsen.timeseries.data.univariate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;

import com.github.TKnudsen.timeseries.data.ITemporalLabeling;
import com.github.TKnudsen.timeseries.data.ITimeValuePair;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesLabelingTools;

/**
 * Factory for creating {@link ITimeSeriesUnivariate} instances from various
 * input structures.
 *
 * <p>
 * All factory methods validate inputs eagerly - null inputs throw
 * {@link NullPointerException}, empty inputs throw
 * {@link IllegalArgumentException}. Null values within collections are
 * preserved as missing data.
 * </p>
 *
 * <p>
 * All {@link List} and {@link Collection} parameters accept any
 * {@link Collection} sub type - callers are not required to convert to
 * {@link List} before calling.
 * </p>
 *
 * <p>
 * Copyright: (c) 2016-2026 Juergen Bernard,
 * https://github.com/TKnudsen/timeSeries
 * </p>
 *
 * @author Juergen Bernard, David Sessler
 * @version 2.0 revised February 2026
 */
public final class TimeSeriesUnivariateFactory {

	private TimeSeriesUnivariateFactory() {
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
	 */
	public static ITimeSeriesUnivariate createConstantTimeSeries(List<Long> timeStamps, double constantValue) {
		Objects.requireNonNull(timeStamps, "timeStamps must not be null");
		if (timeStamps.isEmpty())
			throw new IllegalArgumentException(
					"TimeSeriesTools.createConstantTimeSeries: " + "timeStamps must not be empty");

		List<Long> sorted = new ArrayList<>(timeStamps);
		Collections.sort(sorted);

		return new TimeSeriesUnivariate(sorted, new ArrayList<>(Collections.nCopies(sorted.size(), constantValue)),
				Double.NaN);
	}

	// -----------------------------------------------------------------------
	// From Collection<ITimeValuePair>
	// -----------------------------------------------------------------------

	/**
	 * Creates a new time series from a collection of time-value pairs, using
	 * {@code Double.NaN} as the missing value indicator.
	 *
	 * @param timeValues the pairs to populate the series; must not be null or empty
	 * @return a new populated {@link ITimeSeriesUnivariate}
	 * @throws NullPointerException     if timeValues is null or contains null pairs
	 * @throws IllegalArgumentException if timeValues is empty
	 */
	public static ITimeSeriesUnivariate newTimeSeries(Collection<? extends ITimeValuePair<Double>> timeValues) {
		return newTimeSeries(timeValues, Double.NaN);
	}

	/**
	 * Creates a new time series from a collection of time-value pairs, using the
	 * specified missing value indicator.
	 *
	 * @param timeValues            the pairs to populate the series; must not be
	 *                              null or empty
	 * @param missingValueIndicator the sentinel value for missing data; may be
	 *                              {@code Double.NaN}
	 * @return a new populated {@link ITimeSeriesUnivariate}
	 * @throws NullPointerException     if timeValues is null or contains null pairs
	 * @throws IllegalArgumentException if timeValues is empty
	 */
	public static ITimeSeriesUnivariate newTimeSeries(Collection<? extends ITimeValuePair<Double>> timeValues,
			Double missingValueIndicator) {
		Objects.requireNonNull(timeValues, "timeValues must not be null");
		if (timeValues.isEmpty())
			throw new IllegalArgumentException("timeValues must not be empty");

		int size = timeValues.size();
		List<Long> timestamps = new ArrayList<>(size);
		List<Double> values = new ArrayList<>(size);

		for (ITimeValuePair<Double> pair : timeValues) {
			Objects.requireNonNull(pair, "timeValues must not contain null pairs");
			timestamps.add(pair.getTimestamp());
			values.add(pair.getValue());
		}

		return new TimeSeriesUnivariate(timestamps, values, missingValueIndicator);
	}

	// -----------------------------------------------------------------------
	// From parallel Collections of time stamps and values
	// -----------------------------------------------------------------------

	/**
	 * Creates a new time series from parallel time stamp and value collections,
	 * using {@code Double.NaN} as the missing value indicator.
	 *
	 * @param timestamps timestamps in ascending order; must not be null or empty
	 * @param values     values aligned with timestamps; must not be null or empty
	 * @return a new populated {@link ITimeSeriesUnivariate}
	 * @throws NullPointerException     if either collection is null
	 * @throws IllegalArgumentException if either collection is empty or they differ
	 *                                  in size
	 */
	public static ITimeSeriesUnivariate newTimeSeries(Collection<Long> timestamps, Collection<Double> values) {
		return newTimeSeries(timestamps, values, Double.NaN);
	}

	/**
	 * Creates a new time series from parallel time stamp and value collections,
	 * using the specified missing value indicator.
	 *
	 * @param timestamps            time stamps in ascending order; must not be null
	 *                              or empty
	 * @param values                values aligned with time stamps; must not be
	 *                              null or empty
	 * @param missingValueIndicator the sentinel value for missing data; may be
	 *                              {@code Double.NaN}
	 * @return a new populated {@link ITimeSeriesUnivariate}
	 * @throws NullPointerException     if either collection is null
	 * @throws IllegalArgumentException if either collection is empty or they differ
	 *                                  in size
	 */
	public static ITimeSeriesUnivariate newTimeSeries(Collection<Long> timestamps, Collection<Double> values,
			Double missingValueIndicator) {
		validateParallel(timestamps, values);

		return new TimeSeriesUnivariate(new ArrayList<>(timestamps), new ArrayList<>(values), missingValueIndicator);
	}

	/**
	 * Creates a named, described time series from parallel time stamp and value
	 * collections.
	 *
	 * @param timestamps            time stamps in ascending order; must not be null
	 *                              or empty
	 * @param values                values aligned with time stamps; must not be
	 *                              null or empty
	 * @param missingValueIndicator the sentinel value for missing data; may be
	 *                              {@code Double.NaN}
	 * @param name                  series name; may be null
	 * @param description           series description; may be null
	 * @return a new populated {@link ITimeSeriesUnivariate}
	 * @throws NullPointerException     if either collection is null
	 * @throws IllegalArgumentException if either collection is empty or they differ
	 *                                  in size
	 */
	public static ITimeSeriesUnivariate newTimeSeries(Collection<Long> timestamps, Collection<Double> values,
			Double missingValueIndicator, String name, String description) {
		validateParallel(timestamps, values);

		TimeSeriesUnivariate ts = new TimeSeriesUnivariate(new ArrayList<>(timestamps), new ArrayList<>(values),
				missingValueIndicator);
		ts.setName(name);
		ts.setDescription(description);
		return ts;
	}

	// -----------------------------------------------------------------------
	// From SortedMap
	// -----------------------------------------------------------------------

	/**
	 * Creates a new time series from a sorted map of time stamp to value entries,
	 * using {@code Double.NaN} as the missing value indicator.
	 *
	 * @param data the source map; must not be null or empty
	 * @return a new populated {@link ITimeSeriesUnivariate}
	 * @throws NullPointerException     if data is null
	 * @throws IllegalArgumentException if data is empty
	 */
	public static ITimeSeriesUnivariate newTimeSeries(SortedMap<Long, Double> data) {
		return newTimeSeries(data, Double.NaN);
	}

	/**
	 * Creates a new time series from a sorted map of time stamp to value entries,
	 * using the specified missing value indicator.
	 *
	 * @param data                  the source map; must not be null or empty
	 * @param missingValueIndicator the sentinel value for missing data; may be
	 *                              {@code Double.NaN}
	 * @return a new populated {@link ITimeSeriesUnivariate}
	 * @throws NullPointerException     if data is null
	 * @throws IllegalArgumentException if data is empty
	 */
	public static ITimeSeriesUnivariate newTimeSeries(SortedMap<Long, Double> data, Double missingValueIndicator) {
		Objects.requireNonNull(data, "data must not be null");
		if (data.isEmpty())
			throw new IllegalArgumentException("data must not be empty");

		return new TimeSeriesUnivariate(new ArrayList<>(data.keySet()), new ArrayList<>(data.values()),
				missingValueIndicator);
	}

	// -----------------------------------------------------------------------
	// Validation helpers
	// -----------------------------------------------------------------------

	/**
	 * Creates a deep clone of the given univariate time series, preserving time
	 * stamps, values, missing value indicator, name, description, metadata
	 * attributes, and - if present - event and interval labels.
	 *
	 * @param timeSeries the time series to clone; must not be null
	 * @return a new independent {@link ITimeSeriesUnivariate} equal to the input
	 * @throws NullPointerException if timeSeries is null
	 */
	public static ITimeSeriesUnivariate clone(ITimeSeriesUnivariate timeSeries) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		int size = timeSeries.size();
		List<Long> timestamps = new ArrayList<>(size);
		List<Double> values = new ArrayList<>(size);

		for (int i = 0; i < size; i++) {
			timestamps.add(timeSeries.getTimestamp(i));
			values.add(timeSeries.getValue(i));
		}

		Double indicator = timeSeries.getMissingValueIndicator() != null ? timeSeries.getMissingValueIndicator()
				: Double.NaN;

		TimeSeriesUnivariate clone = new TimeSeriesUnivariate(timestamps, values, indicator);
		clone.setName(timeSeries.getName());
		clone.setDescription(timeSeries.getDescription());

		for (String attribute : timeSeries.getAttributes())
			clone.add(attribute, timeSeries.getAttribute(attribute));

		if (timeSeries instanceof ITemporalLabeling<?> && timeSeries instanceof TimeSeriesUnivariateLabeled) {
			TimeSeriesUnivariateLabeled labeled = new TimeSeriesUnivariateLabeled(clone);
			TimeSeriesUnivariateLabeled source = (TimeSeriesUnivariateLabeled) timeSeries;
			labeled.setEventLabels(TimeSeriesLabelingTools.cloneEventLabels(source.getEventLabels()));
			labeled.setIntervalLabels(TimeSeriesLabelingTools.cloneIntervalLabels(source.getIntervalLabels()));
			return labeled;
		}

		return clone;
	}

	// -----------------------------------------------------------------------
	// Validation helpers
	// -----------------------------------------------------------------------

	private static void validateParallel(Collection<Long> timestamps, Collection<Double> values) {
		Objects.requireNonNull(timestamps, "timestamps must not be null");
		Objects.requireNonNull(values, "values must not be null");

		if (timestamps.isEmpty())
			throw new IllegalArgumentException("timestamps must not be empty");
		if (values.isEmpty())
			throw new IllegalArgumentException("values must not be empty");
		if (timestamps.size() != values.size())
			throw new IllegalArgumentException(
					"timestamps and values must be the same size (" + timestamps.size() + " vs " + values.size() + ")");
	}
}