package com.github.TKnudsen.timeseries.operations.transformations;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeature;
import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeatureVector;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataTransformationCategory;
import com.github.TKnudsen.ComplexDataObject.model.transformations.IDataTransformation;
import com.github.TKnudsen.ComplexDataObject.model.transformations.dimensionReduction.features.numeric.PrincipalComponentAnalysis;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.multivariate.TimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;

/**
 * Applies PCA Dimension Reduction to a (list of) time series. To initially
 * build the PCA model a list of time series is required. Every subsequent call
 * to either {@link DimensionReduction#transform(ITimeSeriesMultivariate)} or
 * {@link DimensionReduction#transform(List)} uses this existing model.
 * 
 * @author Christian Ritter
 *
 */
public class DimensionReduction implements IDataTransformation<ITimeSeriesMultivariate, ITimeSeriesMultivariate> {

	private double minimumRemainingVariance;
	private boolean normalize;
	private int outputDimensionality;
	private PrincipalComponentAnalysis pca;

	public DimensionReduction() {
		normalize = true;
		minimumRemainingVariance = 0.95;
		outputDimensionality = 0;
	}

	public DimensionReduction(boolean normalize, double minimumRemainingVariance, int outputDimensionality) {
		this.normalize = normalize;
		this.minimumRemainingVariance = minimumRemainingVariance;
		this.outputDimensionality = outputDimensionality;
	}

	@Override
	public DataTransformationCategory getDataTransformationCategory() {
		return DataTransformationCategory.DIMENSION_REDUCTION;
	}

	private ITimeSeriesMultivariate listOfFVtoTS(List<NumericalFeatureVector> fvs, List<Long> timestamps) {
		int d = fvs.get(0).getDimensions();
		List<ITimeSeriesUnivariate> uts = new ArrayList<>();
		for (int i = 0; i < d; i++) {
			List<Double> values = new ArrayList<>();
			for (NumericalFeatureVector fv : fvs) {
				values.add(fv.getFeature(i).getFeatureValue());
			}
			uts.add(new TimeSeriesUnivariate(timestamps, values));
		}
		return new TimeSeriesMultivariate(uts);
	}

	private NumericalFeatureVector listToFV(List<Double> lst) {
		List<NumericalFeature> features = new ArrayList<>();
		int i = 0;
		for (Double d : lst) {
			features.add(new NumericalFeature(String.valueOf(i), d));
			i++;
		}
		return new NumericalFeatureVector(features);
	}

	@Override
	public List<ITimeSeriesMultivariate> transform(ITimeSeriesMultivariate input) {
		if (pca == null)
			return null;
		List<Long> timestamps = new ArrayList<>();
		List<NumericalFeatureVector> fvs = new ArrayList<>();
		// save timestamps for later
		timestamps = input.getTimestamps();
		// fill list with feature vectors representing a single timestamp
		// each
		for (int i = 0; i < input.size(); i++) {
			fvs.add(listToFV(input.getValue(i)));
		}
		List<NumericalFeatureVector> reducedFVs = pca.transform(fvs);
		List<ITimeSeriesMultivariate> res = new ArrayList<>();
		res.add(listOfFVtoTS(reducedFVs, timestamps));
		return res;
	}

	@Override
	public List<ITimeSeriesMultivariate> transform(List<ITimeSeriesMultivariate> inputObjects) {
		List<List<Long>> timestamps = new ArrayList<>();
		List<NumericalFeatureVector> fvs = new ArrayList<>();
		for (ITimeSeriesMultivariate ts : inputObjects) {
			// save timestamps for later
			timestamps.add(ts.getTimestamps());
			// fill list with feature vectors representing a single timestamp
			// each
			for (int i = 0; i < ts.size(); i++) {
				fvs.add(listToFV(ts.getValue(i)));
			}
		}
		if (pca == null) {
			pca = new PrincipalComponentAnalysis(normalize, minimumRemainingVariance, outputDimensionality);
		}
		List<NumericalFeatureVector> reducedFVs = pca.transform(fvs);
		List<ITimeSeriesMultivariate> res = new ArrayList<>();
		int currentIndex = 0;
		for (List<Long> ts : timestamps) {
			res.add(listOfFVtoTS(reducedFVs.subList(currentIndex, currentIndex + ts.size()), ts));
			currentIndex += ts.size();
		}
		return res;
	}

}
