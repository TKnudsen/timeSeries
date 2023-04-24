package com.github.TKnudsen.timeseries.operations.tools;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.tools.MathFunctions;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

/**
 * <p>
 * Title: TimeQuantizationTools
 * </p>
 * 
 * <p>
 * Description: little helpers that assess differences between time series.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 */
public class TimeSeriesComparisonTools {

	/**
	 * calculates the difference between the mean values of two time series.
	 * 
	 * @param timeSeries1 time series
	 * @param timeSeries2 time series
	 * @return double
	 */
	public static double compareMeanValues(ITimeSeriesUnivariate timeSeries1, ITimeSeriesUnivariate timeSeries2) {
		double means1 = TimeSeriesTools.getMean(timeSeries1);
		double means2 = TimeSeriesTools.getMean(timeSeries2);

		return Math.abs(means2 - means1);
	}

	/**
	 * calculates the average difference of the value domains of two time series for
	 * every common time stamp.
	 * 
	 * @param timeSeries1 time series
	 * @param timeSeries2 time series
	 * @return double
	 */
	public static double compareValueDifferenceOnAverage(ITimeSeriesUnivariate timeSeries1,
			ITimeSeriesUnivariate timeSeries2) {

		List<Double> valueDifferences = new ArrayList<>();
		for (int i = 0; i < timeSeries1.size(); i++) {
			Double v1 = timeSeries1.getValue(i);

			try {
				// second time series may have other time stamps
				Long timeStamp = timeSeries1.getTimestamp(i);
				Double v2 = timeSeries2.getValue(timeStamp, false);

				if (v1 != null && v2 != null && !Double.isNaN(v1) && !Double.isNaN(v2))
					valueDifferences.add(Math.abs(v2 - v1));
			} catch (Exception e) {
			}
		}

		return MathFunctions.getMean(valueDifferences);
	}
}
