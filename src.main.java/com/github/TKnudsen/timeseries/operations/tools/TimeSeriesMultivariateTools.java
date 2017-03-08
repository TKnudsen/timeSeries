package com.github.TKnudsen.timeseries.operations.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.math3.exception.MathArithmeticException;

import com.github.TKnudsen.ComplexDataObject.model.tools.MathFunctions;
import com.github.TKnudsen.timeseries.data.ITemporalLabeling;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.multivariate.TimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.multivariate.TimeSeriesMultivariateLabeled;
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
	public static ITimeSeriesUnivariate createMinMaxMeanTimeSeriesBundle(ITimeSeriesMultivariate tsmv) {
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
	public static ITimeSeriesUnivariate createMinMaxMeanTimeSeriesBundle(List<ITimeSeriesUnivariate> tsmv) {
		SortedMap<Long, List<Double>> keyValuePairs = new TreeMap<>();

		for (ITimeSeriesUnivariate ts : tsmv)
			for (Long l : ts.getTimestamps()) {
				if (keyValuePairs.get(l) == null)
					keyValuePairs.put(l, new ArrayList<>());
				keyValuePairs.get(l).add(ts.getValue(l, false));
			}

		List<Long> timeStamps = new ArrayList<>();
		List<Double> means = new ArrayList<>();

		for (Long timeStamp : keyValuePairs.keySet()) {
			timeStamps.add(timeStamp);
			means.add(getMean(keyValuePairs.get(timeStamp)));
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
			ITemporalLabeling<?> returnTimeSeriesLabeled = new TimeSeriesMultivariateLabeled(returnTimeSeries);
			returnTimeSeriesLabeled.setEventLabels(TimeSeriesLabelingTools.cloneEventLabels(((TimeSeriesMultivariateLabeled) timeSeries).getEventLabels()));
			returnTimeSeriesLabeled.setIntervalLabels(TimeSeriesLabelingTools.cloneIntervalLabels(((TimeSeriesMultivariateLabeled) timeSeries).getIntervalLabels()));
			return (ITimeSeriesMultivariate) returnTimeSeriesLabeled;
		}

		return returnTimeSeries;
	}

	/**
	 * Calculates the mean value for a given series of values. Ignores
	 * Double.NAN
	 * 
	 * @param values
	 * @return
	 */
	@Deprecated // use lib in future!
	private static double getMean(List<Double> values) {
		if (values == null)
			return Double.NaN;

		double sum = 0;
		double count = 0;
		for (double d : values)
			if (!Double.isNaN(d)) {
				sum += d;
				count++;
			}

		return sum / count;
	}
}
