package com.github.TKnudsen.timeseries.operations.distance.timeSeriesUnivariate;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.distanceMeasure.IDistanceMeasure;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

/**
 * <p>
 * Title: EuclideanDistanceForTimeSeries
 * </p>
 * 
 * <p>
 * Description: calculates the Euclidean distance between two time series
 * (univariate)
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class EuclideanDistanceForTimeSeries implements IDistanceMeasure<ITimeSeriesUnivariate> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 637960422567339874L;

	@Override
	public String getName() {
		return "Euclidean Distance";
	}

	@Override
	public String getDescription() {
		return "Calculates the Euclidean distance between two univariate time series";
	}

	@Override
	public double getDistance(ITimeSeriesUnivariate o1, ITimeSeriesUnivariate o2) {
		if (o1 == null || o2 == null)
			return Double.NaN;

		if (o1.size() != o2.size())
			throw new IllegalArgumentException(getName() + ": distance calculateion between two time series of different length not possible.");

		List<Double> values1 = o1.getValues();
		List<Double> values2 = o2.getValues();

		double dist = 0;
		for (int i = 0; i < values1.size(); i++)
			dist += Math.pow(values1.get(i) - values2.get(i), 2.0);

		return Math.sqrt(dist);
	}
}
