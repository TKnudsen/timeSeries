package com.github.TKnudsen.timeseries.operations.transformations.featureExtraction;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeature;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataTransformationCategory;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;
import com.github.TKnudsen.timeseries.operations.transformations.featureExtraction.univariate.ITimeSeriesUnivariateFeatureExtractor;

/**
 * <p>
 * Title: ValueMaxFeatureExtractor
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
public class ValueMaxFeatureExtractor implements ITimeSeriesUnivariateFeatureExtractor {
	
	private DataTransformationCategory category = DataTransformationCategory.FEATURE_EXTRACTION;

	private Double apply(ITimeSeriesUnivariate t) {
		return TimeSeriesTools.getMaxValue(t);
	}

	@Override
	public List<NumericalFeature> transform(ITimeSeriesUnivariate input) {
		List<NumericalFeature> res = new ArrayList<>();
		res.add(new NumericalFeature("ValueMax", apply(input)));
		return res;
	}

	@Override
	public List<NumericalFeature> transform(List<ITimeSeriesUnivariate> inputObjects) {
		List<NumericalFeature> res = new ArrayList<>();
		for (ITimeSeriesUnivariate input : inputObjects) {
			res.add(new NumericalFeature("ValueMax", apply(input)));
		}
		return res;
	}

	@Override
	public DataTransformationCategory getDataTransformationCategory() {
		return category;
	}
}
