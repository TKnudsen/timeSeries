package com.github.TKnudsen.timeseries.operations.transformations.featureExtraction;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeature;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataTransformationCategory;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.transformations.featureExtraction.univariate.ITimeSeriesUnivariateFeatureExtractor;

/**
 * <p>
 * Title: PositionMeanFeatureExtractor
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
public class PositionMeanFeatureExtractor implements ITimeSeriesUnivariateFeatureExtractor {

	public Double apply(ITimeSeriesUnivariate t) {
		List<Double> vals = t.getValues();
		double sum = 0;
		int l = vals.size();
		for (int i = 0; i < l; i++) {
			sum += vals.get(i) * i / l;
		}
		return sum / l;
	}

	@Override
	public List<NumericalFeature> transform(ITimeSeriesUnivariate input) {
		List<NumericalFeature> res = new ArrayList<>();
		res.add(new NumericalFeature("PositionMean", apply(input)));
		return res;
	}

	@Override
	public List<NumericalFeature> transform(List<ITimeSeriesUnivariate> inputObjects) {
		List<NumericalFeature> res = new ArrayList<>();
		for (ITimeSeriesUnivariate input : inputObjects) {
			res.add(new NumericalFeature("PositionMean", apply(input)));
		}
		return res;
	}

	@Override
	public DataTransformationCategory getDataTransformationCategory() {
		return DataTransformationCategory.FEATURE_EXTRACTION;
	}
}
