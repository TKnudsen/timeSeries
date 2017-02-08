package com.github.TKnudsen.timeseries.operations.workflow.univariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeatureVector;
import com.github.TKnudsen.ComplexDataObject.model.distanceMeasure.IDistanceMeasure;
import com.github.TKnudsen.ComplexDataObject.model.preprocessing.features.IFeatureVectorProcessor;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.descriptors.univariate.ITimeSeriesUnivariateDescriptor;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.ITimeSeriesUnivariatePreprocessor;

/**
 * <p>
 * Title: TimeSeriesUnivariateDataMiningWorkflow
 * </p>
 * 
 * <p>
 * Description: general implementation for data mining workflows applied on
 * univariate time series.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.05
 */
public class TimeSeriesUnivariateDataMiningWorkflow implements ITimeSeriesUnivariateDataMiningWorkflow {

	private List<ITimeSeriesUnivariatePreprocessor> dataProcessors = new ArrayList<>();
	private ITimeSeriesUnivariateDescriptor descriptor = null;

	private List<NumericalFeatureVector> featureVectors;

	private List<IFeatureVectorProcessor<Double, NumericalFeatureVector>> featureVectorProcessors = new ArrayList<>();
	private IDistanceMeasure<NumericalFeatureVector> distanceMeasure;

	@Override
	public void addPreProcessor(ITimeSeriesUnivariatePreprocessor processor) {
		dataProcessors.add(processor);
	}

	@Override
	public void setDescriptor(ITimeSeriesUnivariateDescriptor descriptor) {
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
	public List<NumericalFeatureVector> apply(List<ITimeSeriesUnivariate> data) {
		for (ITimeSeriesUnivariatePreprocessor p : dataProcessors)
			p.process(data);

		if (descriptor != null)
			featureVectors = descriptor.transform(data);

		if (featureVectors != null)
			for (IFeatureVectorProcessor<Double, NumericalFeatureVector> fvProcessor : featureVectorProcessors)
				if (fvProcessor != null)
					fvProcessor.process(featureVectors);

		return featureVectors;
	}

	@Override
	public IDistanceMeasure<NumericalFeatureVector> getDistanceMeasure() {
		return distanceMeasure;
	}

	public List<ITimeSeriesUnivariatePreprocessor> getDataProcessors() {
		return dataProcessors;
	}

	public ITimeSeriesUnivariateDescriptor getDescriptor() {
		return descriptor;
	}

	public List<IFeatureVectorProcessor<Double, NumericalFeatureVector>> getFeatureVectorProcessors() {
		return featureVectorProcessors;
	}
}
