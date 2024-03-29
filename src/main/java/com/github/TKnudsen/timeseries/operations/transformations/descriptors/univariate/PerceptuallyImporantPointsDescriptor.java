package com.github.TKnudsen.timeseries.operations.transformations.descriptors.univariate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeature;
import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeatureVector;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataTransformationCategory;
import com.github.TKnudsen.ComplexDataObject.model.transformations.descriptors.IDescriptor;
import com.github.TKnudsen.ComplexDataObject.model.transformations.descriptors.numericalFeatures.INumericFeatureVectorDescriptor;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.PerceptuallyImportantPoints;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;
import com.github.TKnudsen.timeseries.operations.transformations.descriptors.ITimeSeriesDescriptorInverseFunction;

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
 * Copyright: Copyright (c) 2016-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.06
 */
public class PerceptuallyImporantPointsDescriptor
		implements INumericFeatureVectorDescriptor<ITimeSeriesUnivariate>, ITimeSeriesDescriptorInverseFunction {

	protected int pipCount;
	protected PerceptuallyImportantPoints perceptuallyImportantPointsAlgorithm;

	/**
	 * for serialization purposes
	 */
	protected PerceptuallyImporantPointsDescriptor() {
		this.pipCount = 5;

		initialize();
	}

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

		featureVectors.add(createFeatureVector(clone, timeSeries));
		return featureVectors;
	}

	@Override
	public List<NumericalFeatureVector> transform(List<ITimeSeriesUnivariate> inputObjects) {
		List<NumericalFeatureVector> featureVectors = new ArrayList<>();

		for (ITimeSeriesUnivariate timeSeries : inputObjects)
			featureVectors.addAll(transform(timeSeries));

		return featureVectors;
	}

	/**
	 * The transformation from time series to features. Separated from the eariler
	 * time series processing for inheritance reasons.
	 * 
	 * @param processedTimeSeries processed
	 * @param originalTimeSeries  original
	 * @return FV
	 */
	protected NumericalFeatureVector createFeatureVector(ITimeSeriesUnivariate processedTimeSeries,
			ITimeSeriesUnivariate originalTimeSeries) {
		List<Double> values = processedTimeSeries.getValues();

		List<NumericalFeature> features = new ArrayList<>();
		for (int i = 0; i < values.size(); i++)
			features.add(new NumericalFeature("Dim " + i, values.get(i)));

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
	public String getName() {
		return "PerceptuallyImporantPointsDescriptor, pipCount = " + pipCount;
	}

	@Override
	public String getDescription() {
		return "PerceptuallyImporantPointsDescriptor, pipCount = " + pipCount
				+ ", calculates the perceptually most important points of a given time series and transforms these values to a NumericalFeatureVector.";
	}

	public int getPipCount() {
		return pipCount;
	}

	public void setPipCount(int pipCount) {
		this.pipCount = pipCount;
	}

	@Override
	public List<IDescriptor<ITimeSeriesUnivariate, NumericalFeatureVector>> getAlternativeParameterizations(int count) {
		List<Integer> integers = ParameterSupportTools.getAlternativeIntegers(pipCount, count);

		List<IDescriptor<ITimeSeriesUnivariate, NumericalFeatureVector>> processors = new ArrayList<>();
		for (Integer i : integers)
			// TODO improve data model in the interfaces!
			processors.add(
					(IDescriptor<ITimeSeriesUnivariate, NumericalFeatureVector>) new PerceptuallyImportantPoints(i));

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

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof PerceptuallyImporantPointsDescriptor))
			return false;

		PerceptuallyImporantPointsDescriptor other = (PerceptuallyImporantPointsDescriptor) o;
		return other.pipCount == pipCount;
	}

}
