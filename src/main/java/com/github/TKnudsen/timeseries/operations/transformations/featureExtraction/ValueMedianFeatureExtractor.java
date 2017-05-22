package com.github.TKnudsen.timeseries.operations.transformations.featureExtraction;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeature;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataTransformationCategory;
import com.github.TKnudsen.ComplexDataObject.model.tools.StatisticsSupport;
import com.github.TKnudsen.ComplexDataObject.model.transformations.featureExtraction.IFeatureExtractor;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

/**
 * @author Christian Ritter, J�rgen Bernard
 *
 */
public class ValueMedianFeatureExtractor implements IFeatureExtractor<ITimeSeriesUnivariate, NumericalFeature> {

	private Double apply(ITimeSeriesUnivariate t) {
		double[] values = new double[t.size()];
		int i = 0;
		for (double d : t.getValues()) {
			values[i] = d;
			i++;
		}

		StatisticsSupport statistics = new StatisticsSupport(values);

		return statistics.getMean();
	}

	@Override
	public List<NumericalFeature> transform(ITimeSeriesUnivariate input) {
		List<NumericalFeature> res = new ArrayList<>();
		res.add(new NumericalFeature("ValueMedian", apply(input)));
		return res;
	}

	@Override
	public List<NumericalFeature> transform(List<ITimeSeriesUnivariate> inputObjects) {
		List<NumericalFeature> res = new ArrayList<>();
		for (ITimeSeriesUnivariate input : inputObjects) {
			res.add(new NumericalFeature("ValueMedian", apply(input)));
		}
		return res;
	}

	@Override
	public DataTransformationCategory getDataTransformationCategory() {
		return DataTransformationCategory.FEATURE_EXTRACTION;
	}
}
