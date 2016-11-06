package com.github.TKnudsen.timeseries.data.dataGeneration;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;

/**
 * <p>
 * Title: SyntheticTimeSeriesGenerator
 * </p>
 * 
 * <p>
 * Description: Generates synthetic time series, e.g., for testing.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class SyntheticTimeSeriesGenerator {
	public static ITimeSeriesUnivariate generateSyntheticTimeSeries(long startDate, long endDate, TimeDuration quantization, boolean equidistant) {

		List<Long> timeStamps = new ArrayList<>();
		List<Double> values = new ArrayList<>();

		long duration = quantization.getDuration();

		double lastValue = 0.5;
		long l = startDate;
		while (l <= endDate) {
			timeStamps.add(new Long(l));
			values.add(lastValue);

			if (equidistant)
				l += duration;
			else
				l += ((long) (duration * Math.random() * 2));

			lastValue = lastValue + Math.random() * 0.1 - 0.05;
		}

		return new TimeSeriesUnivariate(timeStamps, values, Double.NaN);
	}
}
