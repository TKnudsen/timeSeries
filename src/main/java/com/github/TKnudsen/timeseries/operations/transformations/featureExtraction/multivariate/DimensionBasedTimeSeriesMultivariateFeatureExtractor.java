package com.github.TKnudsen.timeseries.operations.transformations.featureExtraction.multivariate;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeature;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataTransformationCategory;
import com.github.TKnudsen.ComplexDataObject.model.tools.MathFunctions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.transformations.featureExtraction.univariate.ITimeSeriesUnivariateFeatureExtractor;

/**
 * <p>
 * Title: DimensionBasedTimeSeriesMultivariateFeatureExtractor
 * </p>
 * 
 * <p>
 * Description: Helper class for all MultivariateTimeSeries FeatureExtractors
 * that first extract values from all dimensions of the MVTS (the
 * TimeSeriesUnivariate).
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public abstract class DimensionBasedTimeSeriesMultivariateFeatureExtractor implements ITimeSeriesMultivariateFeatureExtractor {

	private ITimeSeriesUnivariateFeatureExtractor univariateTimeSeriesFeatureExtractor;

	protected abstract void initializeUnivariateTimeSeriesFeatureExtractor();

	public abstract String getName();

	@Override
	public List<NumericalFeature> transform(ITimeSeriesMultivariate input) {
		return transform(Arrays.asList(new ITimeSeriesMultivariate[] { input }));
	}

	@Override
	public List<NumericalFeature> transform(List<ITimeSeriesMultivariate> inputObjects) {
		if (univariateTimeSeriesFeatureExtractor == null)
			initializeUnivariateTimeSeriesFeatureExtractor();

		if (inputObjects == null)
			return null;

		Map<ITimeSeriesMultivariate, List<NumericalFeature>> featuresForEveryTimeSeries = new HashMap<>();
		for (ITimeSeriesMultivariate timeSeriesMultivariate : inputObjects) {
			if (timeSeriesMultivariate == null)
				continue;

			for (ITimeSeriesUnivariate timeSeriesUnivariate : timeSeriesMultivariate.getTimeSeriesList()) {
				List<NumericalFeature> transform = univariateTimeSeriesFeatureExtractor.transform(new ArrayList<>(Arrays.asList(timeSeriesUnivariate)));
				featuresForEveryTimeSeries.put(timeSeriesMultivariate, transform);
			}
		}

		// aggregate the features of every dimension to a single value
		// representing the entire multivariate time series
		List<NumericalFeature> features = new ArrayList<>();
		for (ITimeSeriesMultivariate timeSeriesMultivariate : inputObjects) {
			if (timeSeriesMultivariate == null)
				features.add(null);
			else {
				List<NumericalFeature> transform = featuresForEveryTimeSeries.get(timeSeriesMultivariate);
				if (transform == null)
					features.add(null);
				else {
					List<Double> values = new ArrayList<>();
					for (NumericalFeature feature : transform)
						if (feature != null && !Double.isNaN(feature.getFeatureValue()))
							values.add(feature.getFeatureValue());
					features.add(new NumericalFeature("feature " + getName(), MathFunctions.getMean(values)));
				}
			}
		}

		return features;
	}

	@Override
	public DataTransformationCategory getDataTransformationCategory() {
		return DataTransformationCategory.FEATURE_EXTRACTION;
	}

	protected ITimeSeriesUnivariateFeatureExtractor getUnivariateTimeSeriesFeatureExtractor() {
		return univariateTimeSeriesFeatureExtractor;
	}

	protected void setUnivariateTimeSeriesFeatureExtractor(ITimeSeriesUnivariateFeatureExtractor univariateTimeSeriesFeatureExtractor) {
		this.univariateTimeSeriesFeatureExtractor = univariateTimeSeriesFeatureExtractor;
	}
}
