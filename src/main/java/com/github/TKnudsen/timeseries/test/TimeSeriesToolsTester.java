package com.github.TKnudsen.timeseries.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.github.TKnudsen.timeseries.data.ITimeSeries;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * <p>
 * Title: TimeSeriesToolsTester
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
public class TimeSeriesToolsTester {

	public static void main(String[] args) {

		List<Long> timestamps = new ArrayList<>();
		List<Double> values = new ArrayList<>();

		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			timestamps.add(new Long(i));
			values.add(random.nextDouble());
		}

		TimeSeriesUnivariate ts = new TimeSeriesUnivariate(timestamps, values);
		System.out.println(ts);

		ITimeSeriesUnivariate cloneTimeSeries = TimeSeriesTools.cloneTimeSeries(ts);
		System.out.println(cloneTimeSeries);

		System.out.println("----");
		cloneTimeSeries.replaceValue(cloneTimeSeries.getFirstTimestamp(), 1.0);
		System.out.println(ts);
		System.out.println(cloneTimeSeries);

		cloneTimeSeries.replaceValue(cloneTimeSeries.getFirstTimestamp(), Double.NaN);
		cloneTimeSeries.replaceValue(1, null);

		testRemove(cloneTimeSeries, Double.NaN);
		testRemove(cloneTimeSeries, null);
	}

	private static <T> void testRemove(ITimeSeries<T> timeSeries, T value) {
		TimeSeriesTools.remove(timeSeries, value);
		System.out.println();
	}

}
