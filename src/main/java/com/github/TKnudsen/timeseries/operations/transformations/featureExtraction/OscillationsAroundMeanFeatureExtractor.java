package com.github.TKnudsen.timeseries.operations.transformations.featureExtraction;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeature;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataTransformationCategory;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;
import com.github.TKnudsen.timeseries.operations.transformations.featureExtraction.univariate.ITimeSeriesUnivariateFeatureExtractor;

/**
 * <p>
 * Title: OscillationsAroundMeanFeatureExtractor
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Christian Ritter, Juergen Bernard
 * @version 1.01
 */
public class OscillationsAroundMeanFeatureExtractor implements ITimeSeriesUnivariateFeatureExtractor {

	private DataTransformationCategory category = DataTransformationCategory.FEATURE_EXTRACTION;

	private Double apply(ITimeSeriesUnivariate t) {
		List<Double> values = t.getValues();
		double mean = TimeSeriesTools.getMean(t);
		double oscillations = 0;
		boolean overMean = false;
		for (int i = 0; i < values.size(); i++) {
			if (!overMean && values.get(i) > mean) {
				oscillations++;
				overMean = true;
			}
			if (overMean && values.get(i) < mean) {
				overMean = false;
			}
		}
		return oscillations;
	}

	@Override
	public List<NumericalFeature> transform(ITimeSeriesUnivariate input) {
		List<NumericalFeature> res = new ArrayList<>();
		res.add(new NumericalFeature("OscillationsAroundMean", apply(input)));
		return res;
	}

	@Override
	public List<NumericalFeature> transform(List<ITimeSeriesUnivariate> inputObjects) {
		List<NumericalFeature> res = new ArrayList<>();
		for (ITimeSeriesUnivariate input : inputObjects) {
			res.add(new NumericalFeature("OscillationsAroundMean", apply(input)));
		}
		return res;
	}

	@Override
	public DataTransformationCategory getDataTransformationCategory() {
		return category;
	}

}