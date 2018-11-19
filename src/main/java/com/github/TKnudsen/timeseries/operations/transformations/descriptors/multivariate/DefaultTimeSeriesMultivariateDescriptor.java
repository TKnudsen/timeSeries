package com.github.TKnudsen.timeseries.operations.transformations.descriptors.multivariate;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeature;
import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeatureVector;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataTransformationCategory;
import com.github.TKnudsen.ComplexDataObject.model.transformations.descriptors.IDescriptor;
import com.github.TKnudsen.ComplexDataObject.model.transformations.descriptors.numericalFeatures.INumericFeatureVectorDescriptor;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.multivariate.TimeSeriesMultivariateLabeled;

/**
 * <p>
 * Title: DefaultTimeSeriesMultivariateDescriptor
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public class DefaultTimeSeriesMultivariateDescriptor
		implements INumericFeatureVectorDescriptor<ITimeSeriesMultivariate> {

	@Override
	public List<NumericalFeatureVector> transform(ITimeSeriesMultivariate timeSeriesMultivariate) {
		if (timeSeriesMultivariate == null)
			return null;

		List<NumericalFeatureVector> returnFVs = new ArrayList<>();

		List<String> attributeNames = timeSeriesMultivariate.getAttributeNames();

		for (int a = 0; a < timeSeriesMultivariate.size(); a++) {

			List<NumericalFeature> features = getFeaturesForIndex(timeSeriesMultivariate, attributeNames, a);

			NumericalFeatureVector featureVector = new NumericalFeatureVector(features);
			featureVector.setName(timeSeriesMultivariate.getName());
			featureVector.setDescription(timeSeriesMultivariate.getDescription());

			if (timeSeriesMultivariate instanceof TimeSeriesMultivariateLabeled) {
				TimeSeriesMultivariateLabeled labeledTimeSeries = (TimeSeriesMultivariateLabeled) timeSeriesMultivariate;

				String value = labeledTimeSeries.getLabelsForTimeStamps().get(a);
				featureVector.add("class", value);
			}

			featureVector.add("TimeStamp", timeSeriesMultivariate.getTimestamp(a));
			featureVector.setMaster(timeSeriesMultivariate);

			returnFVs.add(featureVector);
		}

		return returnFVs;
	}

	protected List<NumericalFeature> getFeaturesForIndex(ITimeSeriesMultivariate timeSeriesMultivariate,
			List<String> attributeNames, int index) {
		List<Double> values = timeSeriesMultivariate.getValue(index);

		List<NumericalFeature> features = new ArrayList<>();
		for (int d = 0; d < values.size(); d++) {
			NumericalFeature feature = new NumericalFeature(attributeNames.get(d), values.get(d));
			features.add(feature);
		}

		return features;
	}

	@Override
	public List<NumericalFeatureVector> transform(List<ITimeSeriesMultivariate> inputObjects) {
		if (inputObjects == null)
			return null;

		List<NumericalFeatureVector> featureVectors = new ArrayList<>();

		for (ITimeSeriesMultivariate timeSeries : inputObjects)
			featureVectors.addAll(transform(timeSeries));

		return featureVectors;
	}

	@Override
	public List<IDescriptor<ITimeSeriesMultivariate, NumericalFeatureVector>> getAlternativeParameterizations(
			int arg0) {
		return null;
	}

	@Override
	public DataTransformationCategory getDataTransformationCategory() {
		return DataTransformationCategory.DESCRIPTOR;
	}

	@Override
	public String getDescription() {
		return "Transforms the multivariate value domain of every timestamp into a NumericalFeatureVector";
	}

	@Override
	public String getName() {
		return "DefaultTimeSeriesMultivariateDescriptor";
	}

}
