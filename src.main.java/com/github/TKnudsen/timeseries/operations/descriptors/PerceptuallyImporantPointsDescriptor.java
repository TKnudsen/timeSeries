package com.github.TKnudsen.timeseries.operations.descriptors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeature;
import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeatureVector;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.descriptors.univariate.ITimeSeriesUnivariateDescriptor;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.PerceptuallyImportantPoints;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

public class PerceptuallyImporantPointsDescriptor implements ITimeSeriesUnivariateDescriptor {

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
		featureVector.add("Descriptor", "PerceptuallyImporantPointsDescriptor, pipCount = " + pipCount);
		if (timeSeries.getName() != null)
			featureVector.setName(timeSeries.getName());

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

}
