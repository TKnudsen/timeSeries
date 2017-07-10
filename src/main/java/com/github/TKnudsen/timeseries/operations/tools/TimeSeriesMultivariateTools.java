package com.github.TKnudsen.timeseries.operations.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.github.TKnudsen.ComplexDataObject.model.tools.MathFunctions;
import com.github.TKnudsen.timeseries.data.ITemporalLabeling;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.multivariate.TimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.multivariate.TimeSeriesMultivariateLabeledWithEventsIntervalsAndDurations;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;

/**
 * <p>
 * Title: TimeSeriesMultivariateTools
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class TimeSeriesMultivariateTools {

	public static double getMinValue(Long timeStamp, ITimeSeriesMultivariate timeSeriesMultivariate) {
		if (timeStamp == null || timeSeriesMultivariate == null)
			return Double.NaN;

		if (!timeSeriesMultivariate.containsTimestamp(timeStamp))
			return Double.NaN;

		double min = Double.POSITIVE_INFINITY;
		for (ITimeSeriesUnivariate ts : timeSeriesMultivariate.getTimeSeriesList())
			min = Math.min(min, ts.getValue(timeStamp, false));

		return min;
	}

	public static double getMaxValue(Long timeStamp, ITimeSeriesMultivariate timeSeriesMultivariate) {
		if (timeStamp == null || timeSeriesMultivariate == null)
			return Double.NaN;

		if (!timeSeriesMultivariate.containsTimestamp(timeStamp))
			return Double.NaN;

		double max = Double.NEGATIVE_INFINITY;
		for (ITimeSeriesUnivariate ts : timeSeriesMultivariate.getTimeSeriesList())
			max = Math.max(max, ts.getValue(timeStamp, false));

		return max;
	}

	public static double getMeanValue(Long timeStamp, ITimeSeriesMultivariate timeSeriesMultivariate) {
		if (timeStamp == null || timeSeriesMultivariate == null)
			return Double.NaN;

		if (!timeSeriesMultivariate.containsTimestamp(timeStamp))
			return Double.NaN;

		double mean = 0;
		double count = 0;
		for (ITimeSeriesUnivariate ts : timeSeriesMultivariate.getTimeSeriesList()) {
			mean += ts.getValue(timeStamp, false);
			count += 1;
		}

		return mean / count;
	}

	/**
	 * creates an univariate timeSeries representing the mean value progression
	 * of all dimensions.
	 * 
	 * @param tsmv
	 * @return mean time series
	 */
	public static ITimeSeriesUnivariate createMeanTimeSeries(ITimeSeriesMultivariate tsmv) {
		List<Long> timeStamps = new ArrayList<>();
		List<Double> means = new ArrayList<>();

		for (Long timeStamp : tsmv.getTimestamps()) {
			timeStamps.add(timeStamp);
			means.add(TimeSeriesMultivariateTools.getMeanValue(timeStamp, tsmv));
		}

		ITimeSeriesUnivariate tsMean = new TimeSeriesUnivariate(timeStamps, means, Double.NaN);
		return tsMean;
	}

	/**
	 * creates an univariate timeSeries representing the mean value progression
	 * of all dimensions.
	 * 
	 * @param tsmv
	 * @return mean time series
	 */
	public static ITimeSeriesUnivariate createMeanTimeSeries(List<ITimeSeriesUnivariate> tsmv) {
		SortedMap<Long, List<Double>> keyValuePairs = new TreeMap<>();

		for (ITimeSeriesUnivariate ts : tsmv)
			if (ts != null)
				for (Long l : ts.getTimestamps()) {
					if (keyValuePairs.get(l) == null)
						keyValuePairs.put(l, new ArrayList<>());
					keyValuePairs.get(l).add(ts.getValue(l, false));
				}

		List<Long> timeStamps = new ArrayList<>();
		List<Double> means = new ArrayList<>();

		for (Long timeStamp : keyValuePairs.keySet()) {
			timeStamps.add(timeStamp);
			means.add(MathFunctions.getMean(keyValuePairs.get(timeStamp)));
		}

		ITimeSeriesUnivariate tsMean = new TimeSeriesUnivariate(timeStamps, means, Double.NaN);
		return tsMean;
	}

	/**
	 * Provides a clone of a given multivariate time series.
	 * 
	 * @param timeSeries
	 * @return
	 */
	public static ITimeSeriesMultivariate cloneTimeSeries(ITimeSeriesMultivariate timeSeries) {
		if (timeSeries == null)
			return null;

		List<ITimeSeriesUnivariate> timeSeriesUnivariateList = new ArrayList<>();
		for (int i = 0; i < timeSeries.getDimensionality(); i++) {
			timeSeriesUnivariateList.add(TimeSeriesTools.cloneTimeSeries(timeSeries.getTimeSeries(i)));
		}

		ITimeSeriesMultivariate returnTimeSeries = new TimeSeriesMultivariate(timeSeriesUnivariateList);
		if (timeSeries.getName() != null)
			returnTimeSeries.setName(new String(timeSeries.getName()));
		else
			returnTimeSeries.setName(null);

		if (timeSeries.getDescription() != null)
			returnTimeSeries.setDescription(new String(timeSeries.getDescription()));
		else
			returnTimeSeries.setDescription(null);

		if (timeSeries instanceof ITemporalLabeling<?>) {
			ITemporalLabeling<?> returnTimeSeriesLabeled = new TimeSeriesMultivariateLabeledWithEventsIntervalsAndDurations(returnTimeSeries);
			returnTimeSeriesLabeled.setEventLabels(TimeSeriesLabelingTools.cloneEventLabels(((TimeSeriesMultivariateLabeledWithEventsIntervalsAndDurations) timeSeries).getEventLabels()));
			returnTimeSeriesLabeled.setIntervalLabels(TimeSeriesLabelingTools.cloneIntervalLabels(((TimeSeriesMultivariateLabeledWithEventsIntervalsAndDurations) timeSeries).getIntervalLabels()));
			return (ITimeSeriesMultivariate) returnTimeSeriesLabeled;
		}

		return returnTimeSeries;
	}

	/**
	 * creates a subsequence on the basis of a cloned time series.
	 * 
	 * @param timeSeriesMultivariate
	 * @param start
	 * @param end
	 * @return
	 */
	public static ITimeSeriesMultivariate getSubSequence(ITimeSeriesMultivariate timeSeriesMultivariate, Date start, Date end) {
		ITimeSeriesMultivariate ret = cloneTimeSeries(timeSeriesMultivariate);

		while (ret.size() != 0 && ret.getFirstTimestamp() < start.getTime())
			ret.removeTimeValue(0);

		while (ret.size() != 0 && ret.getLastTimestamp() > end.getTime())
			ret.removeTimeValue(ret.size() - 1);

		return ret;
	}
}
