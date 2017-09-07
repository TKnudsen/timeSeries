package com.github.TKnudsen.timeseries.data.multivariate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;

/**
 * <p>
 * Title: TimeSeriesMultivariateFactory
 * </p>
 * 
 * <p>
 * Description: 
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public class TimeSeriesMultivariateFactory {

	/**
	 * 
	 * @param pairs
	 * @param missingValueIndicator
	 * @return
	 */
	public static ITimeSeriesMultivariate createTimeSeriesMultivatiate(List<Entry<Long, Double[]>> pairs, double missingValueIndicator) {
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
	 * @param pairs
	 * @param missingValueIndicator
	 * @param labels
	 * @return
	 */
	public static ITimeSeriesMultivariate createTimeSeriesMultivatiate(List<Entry<Long, Double[]>> pairs, double missingValueIndicator, List<String> attributes) {
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

		return createTimeSeriesMultivatiate(timestamps, values, missingValueIndicator, attributes);
	}

	/**
	 * 
	 * @param pairs
	 * @param missingValueIndicator
	 * @param labels
	 * @param descriptions
	 * @return
	 */
	public static ITimeSeriesMultivariate createTimeSeriesMultivatiate(List<Entry<Long, Double[]>> pairs, double missingValueIndicator, List<String> attributes, List<String> descriptions, List<String> labels) {
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

		return createTimeSeriesMultivatiate(timestamps, values, missingValueIndicator, attributes, descriptions, labels);
	}

	/**
	 * Creates a new TimeSeriesMultivariate object from the given data input.
	 * 
	 * @param timestamps
	 * @param values
	 *            List of univariate "Timeseries" Data
	 * @param missingValueIndicator
	 * @return
	 */
	public static ITimeSeriesMultivariate createTimeSeriesMultivatiate(List<Long> timestamps, List<List<Double>> values, double missingValueIndicator) {

		List<ITimeSeriesUnivariate> timeSeriesList = new ArrayList<ITimeSeriesUnivariate>();
		List<String> labels = new ArrayList<>();

		for (int i = 0; i < values.size(); i++) {
			timeSeriesList.add(new TimeSeriesUnivariate(new ArrayList<Long>(timestamps), values.get(i), missingValueIndicator));
			labels.add("none");
		}

		ITimeSeriesMultivariate ret = new TimeSeriesMultivariate(timeSeriesList, labels);
		return ret;
	}

	public static ITimeSeriesMultivariate createTimeSeriesMultivatiate(List<Long> timestamps, List<List<Double>> values, double missingValueIndicator, List<String> attributes) {
		List<ITimeSeriesUnivariate> timeSeriesList = new ArrayList<ITimeSeriesUnivariate>();

		for (int i = 0; i < values.size(); i++) {
			TimeSeriesUnivariate timeSeriesUnivariate = new TimeSeriesUnivariate(new ArrayList<Long>(timestamps), values.get(i), missingValueIndicator);
			timeSeriesUnivariate.setName(attributes.get(i));
			timeSeriesUnivariate.setDescription("none");
			timeSeriesList.add(timeSeriesUnivariate);
		}

		ITimeSeriesMultivariate ret = new TimeSeriesMultivariate(timeSeriesList);
		return ret;

	}

	public static ITimeSeriesMultivariate createTimeSeriesMultivatiate(List<Long> timestamps, List<List<Double>> values, double missingValueIndicator, List<String> attributes, List<String> descriptions, List<String> labels) {
		List<ITimeSeriesUnivariate> timeSeriesList = new ArrayList<ITimeSeriesUnivariate>();

		for (int i = 0; i < values.size(); i++) {
			TimeSeriesUnivariate timeSeriesUnivariate = new TimeSeriesUnivariate(new ArrayList<Long>(timestamps), values.get(i), missingValueIndicator);
			timeSeriesUnivariate.setName(attributes.get(i));
			timeSeriesUnivariate.setDescription(descriptions.get(i));
			timeSeriesList.add(timeSeriesUnivariate);
		}

		ITimeSeriesMultivariate ret = new TimeSeriesMultivariate(timeSeriesList, labels);
		return ret;
	}

	public static ITimeSeriesMultivariate createTimeSeriesMultivatiate(List<Long> timestamps, List<List<Double>> values, double missingValueIndicator, List<String> labels, long id) {
		List<ITimeSeriesUnivariate> timeSeriesList = new ArrayList<ITimeSeriesUnivariate>();

		for (int i = 0; i < values.size(); i++) {
			timeSeriesList.add(new TimeSeriesUnivariate(new ArrayList<Long>(timestamps), values.get(i), missingValueIndicator));
		}

		ITimeSeriesMultivariate ret = new TimeSeriesMultivariate(id, timeSeriesList, labels);
		return ret;
	}

	/**
	 * Creates a new TimeSeriesMultivariate object from the given data input.
	 * 
	 * @param timestamps
	 * @param values
	 *            List of univariate "Timeseries" Data
	 * @param missingValueIndicator
	 * @return
	 */
	public static ITimeSeriesMultivariate createTimeSeriesMultivatiateLabeled(List<Long> timestamps, List<List<Double>> values, double missingValueIndicator, List<String> labels) {

		List<ITimeSeriesUnivariate> timeSeriesList = new ArrayList<ITimeSeriesUnivariate>();
		List<String> attributeNames = new ArrayList<>();

		for (int i = 0; i < values.size(); i++) {
			timeSeriesList.add(new TimeSeriesUnivariate(new ArrayList<Long>(timestamps), values.get(i), missingValueIndicator));
			attributeNames.add("none");
		}

		ITimeSeriesMultivariate ret = new TimeSeriesMultivariateLabeled(timeSeriesList, attributeNames, labels);
		return ret;
	}

}
