package com.github.TKnudsen.timeseries.operations.transformations.featureExtraction;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeature;
import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeatureVector;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataTransformationCategory;
import com.github.TKnudsen.ComplexDataObject.model.transformations.descriptors.IDescriptor;
import com.github.TKnudsen.ComplexDataObject.model.transformations.featureExtraction.IFeatureExtractor;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

/**
 * @author Christian Ritter
 *
 */
public class StatisticalTimeSeriesDescriptor
		implements IDescriptor<ITimeSeriesUnivariate, Double, NumericalFeatureVector> {

	private List<IFeatureExtractor<ITimeSeriesUnivariate, NumericalFeature>> featureCreators;

	/**
	 * Creates a new descriptor with a basic set of feature creation routines.
	 */
	public StatisticalTimeSeriesDescriptor() {
		featureCreators = new ArrayList<>();
		featureCreators.add(new ValueMinFeatureExtractor());
		featureCreators.add(new ValueMaxFeatureExtractor());
		featureCreators.add(new ValueMeanFeatureExtractor());
		featureCreators.add(new ValueMedianFeatureExtractor());
		featureCreators.add(new ValueVarianceFeatureExtractor());
		featureCreators.add(new Value1stQuartileFeatureExtractor());
		featureCreators.add(new Value2ndQuartileFeatureExtractor());
		featureCreators.add(new Value3rdQuartileFeatureExtractor());
		featureCreators.add(new PositionMeanFeatureExtractor());
		featureCreators.add(new PositionVarianceFeatureExtractor());
		featureCreators.add(new OscillationsAroundMeanFeatureExtractor());
	}

	@Override
	public String getDescription() {
		return "Creates a statistics based FeatureVector for univariate (equidistant) time series";
	}

	@Override
	public String getName() {
		return "StatisticalTimeSeriesDescriptor";
	}

	@Override
	public List<IDescriptor<ITimeSeriesUnivariate, Double, NumericalFeatureVector>> getAlternativeParameterizations(
			int arg0) {
		return null;
	}

	@Override
	public DataTransformationCategory getDataTransformationCategory() {
		return DataTransformationCategory.DESCRIPTOR;
	}

	@Override
	public List<NumericalFeatureVector> transform(ITimeSeriesUnivariate arg0) {
		List<NumericalFeatureVector> res = new ArrayList<>();
		List<NumericalFeature> features = new ArrayList<>();
		for (IFeatureExtractor<ITimeSeriesUnivariate, NumericalFeature> f : featureCreators) {
			features.addAll(f.transform(arg0));
		}
		res.add(new NumericalFeatureVector(features));
		res.get(0).setName(arg0.getName());
		res.get(0).setDescription(arg0.getDescription());
		return res;
	}

	@Override
	public List<NumericalFeatureVector> transform(List<ITimeSeriesUnivariate> arg0) {
		List<NumericalFeatureVector> res = new ArrayList<>();
		for (ITimeSeriesUnivariate ts : arg0) {
			res.addAll(transform(ts));
		}
		return res;
	}

}
