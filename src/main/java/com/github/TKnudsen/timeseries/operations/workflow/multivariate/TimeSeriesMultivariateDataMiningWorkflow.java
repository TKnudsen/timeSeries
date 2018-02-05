package com.github.TKnudsen.timeseries.operations.workflow.multivariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeatureVector;
import com.github.TKnudsen.ComplexDataObject.model.distanceMeasure.IDistanceMeasure;
import com.github.TKnudsen.ComplexDataObject.model.distanceMeasure.featureVector.EuclideanDistanceMeasure;
import com.github.TKnudsen.ComplexDataObject.model.processors.features.IFeatureVectorProcessor;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.ITimeSeriesMultivariatePreprocessor;
import com.github.TKnudsen.timeseries.operations.transformations.descriptors.multivariate.ITimeSeriesMultivariateDescriptor;

/**
 * <p>
 * Title: TimeSeriesMultivariateDataMiningWorkflow
 * </p>
 * 
 * <p>
 * Description: general implementation for data mining workflows to be applied
 * on multivariate time series.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public class TimeSeriesMultivariateDataMiningWorkflow implements ITimeSeriesMultivariateDataMiningWorkflow {

	/**
	 * Routines to be applied on the input multivariate time series. Preprocessing,
	 * etc.
	 */
	List<ITimeSeriesMultivariatePreprocessor> dataProcessors = new ArrayList<>();

	/**
	 * Transformation of the multivariate time series into the feature space
	 */
	ITimeSeriesMultivariateDescriptor descriptor = null;

	/**
	 * Feature vector representation of the multivariate time series. Output of the
	 * descriptor.
	 */
	private List<NumericalFeatureVector> featureVectors;

	/**
	 * Routines to be applied on the feature vectors. Normalizations, etc.
	 */
	List<IFeatureVectorProcessor<NumericalFeatureVector>> featureVectorProcessors = new ArrayList<>();

	/**
	 * Distance measure for the resulting feature vectors.
	 */
	IDistanceMeasure<NumericalFeatureVector> distanceMeasure = new EuclideanDistanceMeasure();

	@Override
	public void addPreProcessor(ITimeSeriesMultivariatePreprocessor processor) {
		dataProcessors.add(processor);
	}

	public void addPreProcessor(ITimeSeriesMultivariatePreprocessor processor, boolean firstPosition) {
		if (firstPosition)
			dataProcessors.add(0, processor);
		else
			addPreProcessor(processor);
	}

	@Override
	public void setDescriptor(ITimeSeriesMultivariateDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public IDistanceMeasure<NumericalFeatureVector> getDistanceMeasure() {
		return distanceMeasure;
	}

	@Override
	public void setDistanceMeasure(IDistanceMeasure<NumericalFeatureVector> distanceMeasure) {
		this.distanceMeasure = distanceMeasure;
	}

	@Override
	public void addFeatureProcessor(IFeatureVectorProcessor<NumericalFeatureVector> featureProcessor) {
		this.featureVectorProcessors.add(featureProcessor);
	}

	@Override
	public List<NumericalFeatureVector> apply(List<ITimeSeriesMultivariate> data) {
		for (ITimeSeriesMultivariatePreprocessor p : dataProcessors)
			p.process(data);

		if (descriptor != null)
			featureVectors = descriptor.transform(data);

		if (featureVectorProcessors != null && featureVectors != null)
			for (IFeatureVectorProcessor<NumericalFeatureVector> fvProcessor : featureVectorProcessors)
				if (fvProcessor != null)
					fvProcessor.process(featureVectors);

		return featureVectors;
	}
}
