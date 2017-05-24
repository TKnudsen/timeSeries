package com.github.TKnudsen.timeseries.operations.transformations.featureExtraction;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeature;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataTransformationCategory;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.transformations.featureExtraction.univariate.ITimeSeriesUnivariateFeatureExtractor;

/**
 * <p>
 * Title: PositionVarianceFeatureExtractor
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
public class PositionVarianceFeatureExtractor implements ITimeSeriesUnivariateFeatureExtractor {

	private Double apply(ITimeSeriesUnivariate t) {
		double mean = new PositionMeanFeatureExtractor().apply(t);
		List<Double> vals = t.getValues();
		int l = vals.size();
		double var = 0;
		for (int i = 0; i < l; i++) {
			var += vals.get(i) * Math.pow((i / l - mean), 2);
		}
		return var / l;
	}

	@Override
	public List<NumericalFeature> transform(ITimeSeriesUnivariate input) {
		List<NumericalFeature> res = new ArrayList<>();
		res.add(new NumericalFeature("PositionVariance", apply(input)));
		return res;
	}

	@Override
	public List<NumericalFeature> transform(List<ITimeSeriesUnivariate> inputObjects) {
		List<NumericalFeature> res = new ArrayList<>();
		for (ITimeSeriesUnivariate input : inputObjects) {
			res.add(new NumericalFeature("PositionVariance", apply(input)));
		}
		return res;
	}

	@Override
	public DataTransformationCategory getDataTransformationCategory() {
		return DataTransformationCategory.FEATURE_EXTRACTION;
	}
}
