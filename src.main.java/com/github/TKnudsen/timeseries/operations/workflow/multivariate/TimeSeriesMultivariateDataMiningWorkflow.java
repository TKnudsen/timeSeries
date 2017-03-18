package com.github.TKnudsen.timeseries.operations.workflow.multivariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeatureVector;
import com.github.TKnudsen.ComplexDataObject.model.distanceMeasure.IDistanceMeasure;
import com.github.TKnudsen.ComplexDataObject.model.distanceMeasure.featureVector.EuclideanDistanceMeasure;
import com.github.TKnudsen.ComplexDataObject.model.processors.features.IFeatureVectorProcessor;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.operations.descriptors.multivariate.ITimeSeriesMultivariateDescriptor;
import com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.ITimeSeriesMultivariatePreprocessor;

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
 * Copyright: Copyright (c) 2016-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public class TimeSeriesMultivariateDataMiningWorkflow implements ITimeSeriesMultivariateDataMiningWorkflow {

	List<ITimeSeriesMultivariatePreprocessor> dataProcessors = new ArrayList<>();

	ITimeSeriesMultivariateDescriptor descriptor = null;

	private List<NumericalFeatureVector> featureVectors;

	List<IFeatureVectorProcessor<Double, NumericalFeatureVector>> featureVectorProcessors = new ArrayList<>();
	
	IDistanceMeasure<NumericalFeatureVector> distanceMeasure = new EuclideanDistanceMeasure();

	@Override
	public void addPreProcessor(ITimeSeriesMultivariatePreprocessor processor) {
		dataProcessors.add(processor);
	}

	@Override
	public void setDescriptor(ITimeSeriesMultivariateDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public void setDistanceMeasure(IDistanceMeasure<NumericalFeatureVector> distanceMeasure) {
		this.distanceMeasure = distanceMeasure;
	}

	@Override
	public void addFeatureProcessor(IFeatureVectorProcessor<Double, NumericalFeatureVector> featureProcessor) {
		this.featureVectorProcessors.add(featureProcessor);
	}

	@Override
	public List<NumericalFeatureVector> apply(List<ITimeSeriesMultivariate> data) {
		for (ITimeSeriesMultivariatePreprocessor p : dataProcessors)
			p.process(data);

		if (descriptor != null)
			featureVectors = descriptor.transform(data);

		if (featureVectorProcessors != null && featureVectors != null)
			for (IFeatureVectorProcessor<Double, NumericalFeatureVector> fvProcessor : featureVectorProcessors)
				if (fvProcessor != null)
					fvProcessor.process(featureVectors);

		return featureVectors;
	}

	@Override
	public IDistanceMeasure<NumericalFeatureVector> getDistanceMeasure() {
		return distanceMeasure;
	}
}
