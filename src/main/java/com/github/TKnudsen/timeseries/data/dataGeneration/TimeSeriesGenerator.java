package com.github.TKnudsen.timeseries.data.dataGeneration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
public class TimeSeriesGenerator {
	public static ITimeSeriesUnivariate generateSyntheticTimeSeriesUnivariate(long startDate, long endDate,
			TimeDuration quantization, boolean equidistant) {

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

			lastValue = lastValue + Math.random() * 0.2 - 0.1;
		}

		return new TimeSeriesUnivariate(timeStamps, values, Double.NaN);
	}

	public static ITimeSeriesUnivariate generateSyntheticTimeSeriesUnivariateRandomWalk(double startValue) {

		List<Long> timeStamps = new ArrayList<>();
		List<Double> values = new ArrayList<>();

		@SuppressWarnings("deprecation")
		Date start = new Date(2016 - 1900, 04, 01);
		@SuppressWarnings("deprecation")
		Date end = new Date(2016 - 1900, 04, 20);
		double d = startValue;
		for (long l = start.getTime(); l < end.getTime(); l += 1000 * 60 * 60) {
			d = Math.min(100, Math.max(0, d + (Math.random() - 0.5) * 10));
			timeStamps.add(l);
			values.add(d);
		}

		return new TimeSeriesUnivariate(timeStamps, values, Double.NaN);
	}

	public static ITimeSeriesMultivariate generateSyntheticTimeSeriesMultivariate(long startDate, long endDate,
			int dimensionality, TimeDuration quantization, boolean equidistant) {

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

			List<Long> timeStampsCopy = new ArrayList<>();
			timeStampsCopy.addAll(timeStamps);

			List<Double> values = new ArrayList<>();
			double lastValue = 0.5;

			for (int j = 0; j < timeStampsCopy.size(); j++) {
				values.add(lastValue);
				lastValue = lastValue + Math.random() * 0.2 - 0.1;
			}

			timeSeriesUnivariateList.add(new TimeSeriesUnivariate(timeStampsCopy, values, Double.NaN));
			timeSeriesNames.add("" + i);
		}

		return new TimeSeriesMultivariate(timeSeriesUnivariateList, timeSeriesNames);
	}

	public static ITimeSeriesMultivariate createRandomTSMV(int dim, int timeStamps) {
		List<ITimeSeriesUnivariate> timeSeriesUnivariateList = new ArrayList<>();
		List<Long> timeStampsList;
		List<Double> values;

		for (int i = 0; i < dim; i++) {
			timeStampsList = new ArrayList<>();
			values = new ArrayList<>();
			Random rand = new Random();
			int r = rand.nextInt(10) + 1;
			for (int j = 0; j < timeStamps; j++) {
				double e = rand.nextDouble();
				timeStampsList.add((long) j);
				values.add(Math.sin(r * j * e));
			}

			TimeSeriesUnivariate ts = new TimeSeriesUnivariate(timeStampsList, values);
			timeSeriesUnivariateList.add(ts);
		}

		return new TimeSeriesMultivariate(timeSeriesUnivariateList);
	}

	public static ITimeSeriesMultivariate createSineTSMV(int dim, int timeStamps) {
		List<ITimeSeriesUnivariate> timeSeriesUnivariateList = new ArrayList<>();
		List<Long> timeStampsList;
		List<Double> values;

		for (int i = 0; i < dim; i++) {
			timeStampsList = new ArrayList<>();
			values = new ArrayList<>();
			Random rand = new Random();
			int r = rand.nextInt(10);
			for (int j = 0; j < timeStamps; j++) {
				timeStampsList.add((long) j);
				values.add(Math.sin(r * j));
			}

			TimeSeriesUnivariate ts = new TimeSeriesUnivariate(timeStampsList, values);
			timeSeriesUnivariateList.add(ts);
		}

		return new TimeSeriesMultivariate(timeSeriesUnivariateList);
	}
}
