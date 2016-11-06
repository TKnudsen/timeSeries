package com.github.TKnudsen.timeseries.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.MovingAverage;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * <p>
 * Title: MovingAverageTester
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
public class MovingAverageTester {

	public static void main(String[] args) {

		List<Long> timestamps = new ArrayList<>();
		List<Double> values = new ArrayList<>();

		Random random = new Random();
		double d = 0.5;
		for (int i = 0; i < 100; i++) {
			timestamps.add(new Long(i * 10000000L));
			d = d + (random.nextDouble() * 0.06 - 0.03);
			values.add(d);
		}

		TimeSeriesUnivariate ts = new TimeSeriesUnivariate(timestamps, values);
		System.out.println(ts);

		ITimeSeriesUnivariate cloneTimeSeries = TimeSeriesTools.cloneTimeSeries(ts);
		MovingAverage movingAverage = new MovingAverage(3, false);
		for (int i = 0; i < 100; i++)
			movingAverage.process(Arrays.asList(cloneTimeSeries));
		System.out.println(cloneTimeSeries);
	}

}
