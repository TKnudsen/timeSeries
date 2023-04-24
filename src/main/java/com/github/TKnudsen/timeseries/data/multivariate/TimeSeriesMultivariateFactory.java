package com.github.TKnudsen.timeseries.data.multivariate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;

/**
 * <p>
 * timeSeries
 * </p>
 * 
 * <p>
 * Creates ITimeSeriesMultivariate instances for different types of compatible
 * data.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2019
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.04
 */
public class TimeSeriesMultivariateFactory {

	/**
	 * 
	 * @param pairs                 pairs
	 * @param missingValueIndicator indicator
	 * @return time series
	 */
	public static ITimeSeriesMultivariate createTimeSeriesMultivatiate(List<? extends Entry<Long, Double[]>> pairs,
			double missingValueIndicator) {
		List<List<Double>> values = new ArrayList<List<Double>>();
		List<Long> timestamps = new ArrayList<Long>();

		for (int i = 0; i < pairs.size(); i++) {
			timestamps.add(pairs.get(i).getKey());
		}

		for (int j = 0; j < pairs.get(0).getValue().length; j++) {
			List<Double> vals = new ArrayList<Double>();
			for (int i = 0; i < pairs.size(); i++) {
				vals.add(pairs.get(i).getValue()[j]);
			}
			values.add(vals);
		}

		return createTimeSeriesMultivatiate(timestamps, values, missingValueIndicator);
	}

	/**
	 * 
	 * @param pairs                 pairs
	 * @param missingValueIndicator indicator
	 * @param timeSeriesNames       names
	 * @return time series
	 */
	public static ITimeSeriesMultivariate createTimeSeriesMultivatiate(List<? extends Entry<Long, Double[]>> pairs,
			double missingValueIndicator, List<String> timeSeriesNames) {
		List<List<Double>> values = new ArrayList<List<Double>>();
		List<Long> timestamps = new ArrayList<Long>();

		for (int i = 0; i < pairs.size(); i++) {
			timestamps.add(pairs.get(i).getKey());
		}

		for (int j = 0; j < pairs.get(0).getValue().length; j++) {
			List<Double> vals = new ArrayList<Double>();
			for (int i = 0; i < pairs.size(); i++) {
				vals.add(pairs.get(i).getValue()[j]);
			}
			values.add(vals);
		}

		return createTimeSeriesMultivatiate(timestamps, values, missingValueIndicator, timeSeriesNames);
	}

	/**
	 * Creates a new TimeSeriesMultivariate object from the given data input.
	 * 
	 * @param timestamps            time stamps
	 * @param values                List of univariate "Timeseries" Data
	 * @param missingValueIndicator missing value indicator
	 * @return time series
	 */
	public static ITimeSeriesMultivariate createTimeSeriesMultivatiate(List<Long> timestamps, List<List<Double>> values,
			double missingValueIndicator) {

		List<ITimeSeriesUnivariate> timeSeriesList = new ArrayList<ITimeSeriesUnivariate>();
		List<String> labels = new ArrayList<>();

		for (int i = 0; i < values.size(); i++) {
			timeSeriesList.add(
					new TimeSeriesUnivariate(new ArrayList<Long>(timestamps), values.get(i), missingValueIndicator));
			labels.add("none");
		}

		ITimeSeriesMultivariate ret = new TimeSeriesMultivariate(timeSeriesList, labels);
		return ret;
	}

	public static ITimeSeriesMultivariate createTimeSeriesMultivatiate(List<Long> timestamps, List<List<Double>> values,
			double missingValueIndicator, List<String> attributes) {
		List<ITimeSeriesUnivariate> timeSeriesList = new ArrayList<ITimeSeriesUnivariate>();

		for (int i = 0; i < values.size(); i++) {
			TimeSeriesUnivariate timeSeriesUnivariate = new TimeSeriesUnivariate(new ArrayList<Long>(timestamps),
					values.get(i), missingValueIndicator);
			timeSeriesUnivariate.setName(attributes.get(i));
			timeSeriesUnivariate.setDescription("none");
			timeSeriesList.add(timeSeriesUnivariate);
		}

		ITimeSeriesMultivariate ret = new TimeSeriesMultivariate(timeSeriesList);
		return ret;

	}

	/**
	 * 
	 * @param timestamps            time stamps
	 * @param values                values
	 * @param missingValueIndicator missing value indicator
	 * @param timeSeriesNames       time series
	 * @param descriptions          descriptions
	 * @return time series
	 */
	public static ITimeSeriesMultivariate createTimeSeriesMultivatiate(List<Long> timestamps, List<List<Double>> values,
			double missingValueIndicator, List<String> timeSeriesNames, List<String> descriptions) {
		List<ITimeSeriesUnivariate> timeSeriesList = new ArrayList<ITimeSeriesUnivariate>();

		for (int i = 0; i < values.size(); i++) {
			TimeSeriesUnivariate timeSeriesUnivariate = new TimeSeriesUnivariate(new ArrayList<Long>(timestamps),
					values.get(i), missingValueIndicator);
			timeSeriesUnivariate.setName(timeSeriesNames.get(i));
			timeSeriesUnivariate.setDescription(descriptions.get(i));
			timeSeriesList.add(timeSeriesUnivariate);
		}

		ITimeSeriesMultivariate ret = new TimeSeriesMultivariate(timeSeriesList);
		return ret;
	}

	/**
	 * 
	 * @param timestamps            time stamps
	 * @param values                values
	 * @param missingValueIndicator indicators
	 * @param timeSeriesNames       names
	 * @param id                    id
	 * @return time series
	 */
	public static ITimeSeriesMultivariate createTimeSeriesMultivatiate(List<Long> timestamps, List<List<Double>> values,
			double missingValueIndicator, List<String> timeSeriesNames, long id) {
		List<ITimeSeriesUnivariate> timeSeriesList = new ArrayList<ITimeSeriesUnivariate>();

		for (int i = 0; i < values.size(); i++) {
			TimeSeriesUnivariate timeSeriesUnivariate = new TimeSeriesUnivariate(new ArrayList<Long>(timestamps),
					values.get(i), missingValueIndicator);
			timeSeriesUnivariate.setName(timeSeriesNames.get(i));
			timeSeriesUnivariate.setDescription(timeSeriesNames.get(i));
			timeSeriesList.add(timeSeriesUnivariate);
		}

		ITimeSeriesMultivariate ret = new TimeSeriesMultivariate(id, timeSeriesList);
		return ret;
	}

	/**
	 * 
	 * @param pairs                 pairs
	 * @param missingValueIndicator indicator
	 * @param labelsForTimeStamps   labels
	 * @param timeSeriesNames       timeSeriesNames
	 * @param descriptions          descriptions
	 * @return time series
	 */
	public static ITimeSeriesMultivariate createTimeSeriesMultivatiateLabeled(List<Entry<Long, Double[]>> pairs,
			double missingValueIndicator, List<String> timeSeriesNames, List<String> descriptions,
			List<String> labelsForTimeStamps) {
		List<List<Double>> values = new ArrayList<List<Double>>();
		List<Long> timestamps = new ArrayList<Long>();

		for (int i = 0; i < pairs.size(); i++) {
			timestamps.add(pairs.get(i).getKey());
		}

		for (int j = 0; j < pairs.get(0).getValue().length; j++) {
			List<Double> vals = new ArrayList<Double>();
			for (int i = 0; i < pairs.size(); i++) {
				vals.add(pairs.get(i).getValue()[j]);
			}
			values.add(vals);
		}

		ITimeSeriesMultivariate labeledTimeSeries = createTimeSeriesMultivatiateLabeled(timestamps, values,
				missingValueIndicator, labelsForTimeStamps);

		for (int i = 0; i < labeledTimeSeries.getDimensionality(); i++) {
			ITimeSeriesUnivariate timeSeriesUnivariate = labeledTimeSeries.getTimeSeries(i);
			timeSeriesUnivariate.setName(timeSeriesNames.get(i));
			timeSeriesUnivariate.setDescription(descriptions.get(i));
		}

		return labeledTimeSeries;
	}

	/**
	 * Creates a new TimeSeriesMultivariate object from the given data input.
	 * 
	 * @param timestamps            time stamps
	 * @param values                List of univariate "Timeseries" Data
	 * @param missingValueIndicator indicator
	 * @param labelsForTimeStamps   labelsForTimeStamps
	 * @return time series
	 */
	public static ITimeSeriesMultivariate createTimeSeriesMultivatiateLabeled(List<Long> timestamps,
			List<List<Double>> values, double missingValueIndicator, List<String> labelsForTimeStamps) {

		List<ITimeSeriesUnivariate> timeSeriesList = new ArrayList<ITimeSeriesUnivariate>();
		List<String> attributeNames = new ArrayList<>();

		for (int i = 0; i < values.size(); i++) {
			timeSeriesList.add(
					new TimeSeriesUnivariate(new ArrayList<Long>(timestamps), values.get(i), missingValueIndicator));
			attributeNames.add("none");
		}

		ITimeSeriesMultivariate ret = new TimeSeriesMultivariateLabeled(timeSeriesList, attributeNames,
				labelsForTimeStamps);
		return ret;
	}

}
