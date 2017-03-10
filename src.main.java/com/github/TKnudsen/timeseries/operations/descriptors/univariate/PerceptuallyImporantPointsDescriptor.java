package com.github.TKnudsen.timeseries.operations.descriptors.univariate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeature;
import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeatureVector;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataTransformationCategory;
import com.github.TKnudsen.ComplexDataObject.model.transformations.descriptors.IDescriptor;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.descriptors.ITimeSeriesDescriptorInverseFunction;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.PerceptuallyImportantPoints;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * <p>
 * Title: PerceptuallyImporantPointsDescriptor
 * </p>
 * 
 * <p>
 * Description: The perceptually important points algorithm approximates a time
 * series with respect to it's local extreme values (perceptually important
 * points). The algorithm can be seen as a data reduction algorithm reducing
 * less perceptually less important points until the target time series is
 * shrinked to a pre-given number of points (pipCount).
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.04
 */
public class PerceptuallyImporantPointsDescriptor implements ITimeSeriesUnivariateDescriptor, ITimeSeriesDescriptorInverseFunction {

	private int pipCount;
	private PerceptuallyImportantPoints perceptuallyImportantPointsAlgorithm;

	public PerceptuallyImporantPointsDescriptor(int pipCount) {
		this.pipCount = pipCount;

		initialize();
	}

	private void initialize() {
		perceptuallyImportantPointsAlgorithm = new PerceptuallyImportantPoints(pipCount);
	}

	@Override
	public List<NumericalFeatureVector> transform(ITimeSeriesUnivariate timeSeries) {
		List<NumericalFeatureVector> featureVectors = new ArrayList<>();

		ITimeSeriesUnivariate clone = TimeSeriesTools.cloneTimeSeries(timeSeries);
		perceptuallyImportantPointsAlgorithm.process(Arrays.asList(clone));
		List<Double> values = clone.getValues();

		List<NumericalFeature> features = new ArrayList<>();
		for (int i = 0; i < values.size(); i++)
			features.add(new NumericalFeature("Dim " + i, values.get(i)));

		NumericalFeatureVector featureVector = new NumericalFeatureVector(features);
		featureVector.setMaster(timeSeries);
		featureVector.add("Descriptor", getName());
		if (timeSeries.getName() != null)
			featureVector.setName(timeSeries.getName());
		featureVector.add("FirstTimestamp", timeSeries.getFirstTimestamp());
		featureVector.add("LastTimestamp", timeSeries.getLastTimestamp());

		featureVectors.add(featureVector);
		return featureVectors;
	}

	@Override
	public List<NumericalFeatureVector> transform(List<ITimeSeriesUnivariate> inputObjects) {
		List<NumericalFeatureVector> featureVectors = new ArrayList<>();

		for (ITimeSeriesUnivariate timeSeries : inputObjects)
			featureVectors.addAll(transform(timeSeries));

		return featureVectors;
	}

	@Override
	public String getName() {
		return "PerceptuallyImporantPointsDescriptor, pipCount = " + pipCount;
	}

	@Override
	public String getDescription() {
		return "PerceptuallyImporantPointsDescriptor, pipCount = " + pipCount + ", calculates the perceptually most important points of a given time series and transforms these values to a NumericalFeatureVector.";
	}

	public int getPipCount() {
		return pipCount;
	}

	public void setPipCount(int pipCount) {
		this.pipCount = pipCount;
	}

	@Override
	public List<IDescriptor<ITimeSeriesUnivariate, Double, NumericalFeatureVector>> getAlternativeParameterizations(int count) {
		List<Integer> integers = ParameterSupportTools.getAlternativeIntegers(pipCount, count);

		List<IDescriptor<ITimeSeriesUnivariate, Double, NumericalFeatureVector>> processors = new ArrayList<>();
		for (Integer i : integers)
			// TODO improve data model in the interfaces!
			processors.add((IDescriptor<ITimeSeriesUnivariate, Double, NumericalFeatureVector>) new PerceptuallyImportantPoints(i));

		return processors;
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
		List<Double> values = new ArrayList<>();

		for (int i = 0; i < vector.length - 1; i++) {
			timeStamps.add((long) (start + i * scale));
			values.add(vector[i]);
		}
		// end
		timeStamps.add(end);
		values.add(vector[vector.length - 1]);

		return new TimeSeriesUnivariate(timeStamps, values);
	}

	@Override
	public DataTransformationCategory getDataTransformationCategory() {
		return DataTransformationCategory.DESCRIPTOR;
	}
}
