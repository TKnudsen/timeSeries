package com.github.TKnudsen.timeseries.operations.transformations.featureExtraction;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeature;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataTransformationCategory;
import com.github.TKnudsen.ComplexDataObject.model.transformations.featureExtraction.IFeatureExtractor;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

/**
 * @author Christian Ritter
 *
 */
public class Value3rdQuartileFeatureExtractor implements IFeatureExtractor<ITimeSeriesUnivariate, NumericalFeature> {

	private Double apply(ITimeSeriesUnivariate t) {
		List<Double> vals = t.getValues();
		double[] da = new double[vals.size()];
		for (int i = 0; i < vals.size(); i++) {
			da[i] = vals.get(i);
		}
		DescriptiveStatistics ds = new DescriptiveStatistics(da);
		return ds.getPercentile(75);
	}

	@Override
	public List<NumericalFeature> transform(ITimeSeriesUnivariate input) {
		List<NumericalFeature> res = new ArrayList<>();
		res.add(new NumericalFeature("3rdQuartile", apply(input)));
		return res;
	}

	@Override
	public List<NumericalFeature> transform(List<ITimeSeriesUnivariate> inputObjects) {
		List<NumericalFeature> res = new ArrayList<>();
		for (ITimeSeriesUnivariate input : inputObjects) {
			res.add(new NumericalFeature("3rdQuartile", apply(input)));
		}
		return res;
	}

	@Override
	public DataTransformationCategory getDataTransformationCategory() {
		return DataTransformationCategory.FEATURE_EXTRACTION;
	}
}