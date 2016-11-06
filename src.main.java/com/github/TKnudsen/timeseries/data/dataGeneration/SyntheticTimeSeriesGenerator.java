package com.github.TKnudsen.timeseries.data.dataGeneration;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.multivariate.TimeSeriesMultivariate;
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
	public static ITimeSeriesUnivariate generateSyntheticTimeSeriesUnivariate(long startDate, long endDate, TimeDuration quantization, boolean equidistant) {

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

	public static ITimeSeriesMultivariate generateSyntheticTimeSeriesMultivariate(long startDate, long endDate, int dimensionality, TimeDuration quantization, boolean equidistant) {

		List<Long> timeStamps = new ArrayList<>();
		List<ITimeSeriesUnivariate> timeSeriesUnivariateList = new ArrayList<>();
		List<String> timeSeriesNames = new ArrayList<>();

		long duration = quantization.getDuration();

		long l = startDate;
		while (l <= endDate) {
			timeStamps.add(new Long(l));

			if (equidistant)
				l += duration;
			else
				l += ((long) (duration * Math.random() * 2));
		}

		for (int i = 0; i < dimensionality; i++) {
			List<Double> values = new ArrayList<>();
			double lastValue = 0.5;

			for (int j = 0; j < timeStamps.size(); j++) {
				values.add(lastValue);
				lastValue = lastValue + Math.random() * 0.1 - 0.05;
			}

			timeSeriesUnivariateList.add(new TimeSeriesUnivariate(timeStamps, values, Double.NaN));
			timeSeriesNames.add("" + i);
		}

		return new TimeSeriesMultivariate(timeSeriesUnivariateList, timeSeriesNames);
	}
}
