package com.github.TKnudsen.timeseries.data.univariate;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import com.github.TKnudsen.timeseries.data.ITimeValuePair;

/**
 * <p>
 * Title: TimeSeriesUnivariateFactory
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2024
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public class TimeSeriesUnivariateFactory {

	public static ITimeSeriesUnivariate newTimeSeries(List<ITimeValuePair<Double>> tsList) {
		List<Long> timeStamps = new ArrayList<>();
		List<Double> values = new ArrayList<>();

		for (int i = 0; i < tsList.size(); i++) {
			timeStamps.add(tsList.get(i).getTimestamp());
			values.add(tsList.get(i).getValue());
		}

		return new TimeSeriesUnivariate(timeStamps, values, Double.NaN);
	}

	public static ITimeSeriesUnivariate newTimeSeries(List<TimeValuePairUnivariate> tsList,
			Double missingValueIndicator) {
		List<Long> timeStamps = new ArrayList<>();
		List<Double> values = new ArrayList<>();

		for (int i = 0; i < tsList.size(); i++) {
			timeStamps.add(tsList.get(i).getTimestamp());
			values.add(tsList.get(i).getValue());
		}

		return new TimeSeriesUnivariate(timeStamps, values, missingValueIndicator);
	}

	public static ITimeSeriesUnivariate newTimeSeries(SortedMap<Long, Double> data, Double missingValueIndicator) {
		return new TimeSeriesUnivariate(new ArrayList<Long>(data.keySet()), new ArrayList<>(data.values()),
				missingValueIndicator);
	}
}
