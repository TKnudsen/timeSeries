package com.github.TKnudsen.timeseries.operations.transformations.descriptors.univariate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeature;
import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeatureVector;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.primitives.TimeQuantization;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;

/**
 * <p>
 * Title: PiecewiseLinarSegmentsDescriptor
 * </p>
 * 
 * <p>
 * Description: Piecewise Linear Segments Descriptor implementation combining
 * Keoghs Piecewise Linear Segments representatino with the Perceptually
 * Important Points algorithm.
 * 
 * EJ Keogh, MJ Pazzani: An enhanced representation of time series which allows
 * fast and accurate classification, clustering and relevance feedback, KDD,
 * 1998.
 * 
 * Based on the result of the PIP descriptor linear segments are used to create
 * the descriptor. Each linear segment is represented by it's temporal trend.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class PiecewiseLinarSegmentsDescriptor extends PerceptuallyImporantPointsDescriptor {

	private Long temporalReference = new TimeDuration(TimeQuantization.DAYS, 1).getDuration();

	public PiecewiseLinarSegmentsDescriptor(int numberOfSegments) {
		super(numberOfSegments + 1);
	}

	/**
	 * The transformation from time series to features. Separated from the
	 * eariler time series processing for inheritance reasons.
	 * 
	 * @param processedTimeSeries
	 * @param originalTimeSeries
	 * @return
	 */
	@Override
	protected NumericalFeatureVector createFeatureVector(ITimeSeriesUnivariate processedTimeSeries, ITimeSeriesUnivariate originalTimeSeries) {
		List<Long> timeStamps = processedTimeSeries.getTimestamps();
		List<Double> values = processedTimeSeries.getValues();

		if (timeStamps.size() != values.size())
			throw new IllegalArgumentException("Timestamps and values of unequal size");

		List<NumericalFeature> features = new ArrayList<>();
		for (int i = 0; i < values.size() - 1; i++) {
			double value = (values.get(i + 1) - values.get(i)) / (double) temporalReference;
			features.add(new NumericalFeature("Dim " + i, value));
		}

		NumericalFeatureVector featureVector = new NumericalFeatureVector(features);
		featureVector.setMaster(originalTimeSeries);
		featureVector.add("Descriptor", getName());
		if (originalTimeSeries.getName() != null)
			featureVector.setName(originalTimeSeries.getName());
		featureVector.add("FirstTimestamp", originalTimeSeries.getFirstTimestamp());
		featureVector.add("LastTimestamp", originalTimeSeries.getLastTimestamp());

		return featureVector;
	}

	@Override
	public ITimeSeriesUnivariate invertFunction(NumericalFeatureVector featureVector) {
		if (featureVector == null)
			return null;

		// start
		Long start = 0L;
		if (featureVector.getAttribute("FirstTimestamp") != null)
			try {
				start = (Long) featureVector.getAttribute("FirstTimestamp");
			} catch (Exception e) {

			}

		// end
		Long end = 0L;
		if (featureVector.getAttribute("LastTimestamp") != null)
			try {
				end = (Long) featureVector.getAttribute("LastTimestamp");
			} catch (Exception e) {

			}

		double[] vector = featureVector.getVector();
		if (vector == null)
			return null;

		double scale = (end - start) / (double) (vector.length - 1);

		List<Long> timeStamps = new ArrayList<>();
		Double[] valueArray = new Double[vector.length];
		List<Double> values = new ArrayList<>();

		for (int i = 0; i < vector.length - 1; i++) {
			timeStamps.add((long) (start + i * scale));
			double v = vector[i] * temporalReference;
			valueArray[i] -= v * 0.5;
			valueArray[i + 1] += v * 0.5;
		}
		// end
		timeStamps.add(end);

		values = Arrays.asList(valueArray);

		return new TimeSeriesUnivariate(timeStamps, values);
	}

	@Override
	public String getName() {
		return "PiecewiseLinarSegmentsDescriptor";
	}

	@Override
	public String getDescription() {
		return "PiecewiseLinarSegmentsDescriptor, number of segments = " + (getPipCount() - 1);
	}
}
