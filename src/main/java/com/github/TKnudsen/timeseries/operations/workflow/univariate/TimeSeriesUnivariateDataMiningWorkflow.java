package com.github.TKnudsen.timeseries.operations.workflow.univariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeatureVector;
import com.github.TKnudsen.ComplexDataObject.model.distanceMeasure.IDistanceMeasure;
import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.transformations.descriptors.IDescriptor;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.workflow.ITimeSeriesDataMiningWorkflow;

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
 * Copyright: Copyright (c) 2016-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.09
 */
public class TimeSeriesUnivariateDataMiningWorkflow implements ITimeSeriesDataMiningWorkflow<ITimeSeriesUnivariate> {

	private List<IDataProcessor<ITimeSeriesUnivariate>> dataProcessors = new ArrayList<>();
	private IDescriptor<ITimeSeriesUnivariate, NumericalFeatureVector> descriptor = null;

	private List<NumericalFeatureVector> featureVectors;

	private List<IDataProcessor<NumericalFeatureVector>> featureVectorProcessors = new ArrayList<>();
	private IDistanceMeasure<NumericalFeatureVector> distanceMeasure;

	@Override
	public void addPreProcessor(IDataProcessor<ITimeSeriesUnivariate> processor) {
		addPreProcessor(processor, false);
	}

	@Override
	public void addPreProcessor(IDataProcessor<ITimeSeriesUnivariate> processor, boolean firstPosition) {
		if (firstPosition)
			dataProcessors.add(0, processor);
		else
			dataProcessors.add(processor);
	}

	@Override
	public void setDescriptor(IDescriptor<ITimeSeriesUnivariate, NumericalFeatureVector> descriptor) {
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
	public void addFeatureProcessor(IDataProcessor<NumericalFeatureVector> featureProcessor) {
		this.featureVectorProcessors.add(featureProcessor);
	}

	@Override
	public List<NumericalFeatureVector> apply(List<ITimeSeriesUnivariate> data) {
		for (IDataProcessor<ITimeSeriesUnivariate> p : dataProcessors)
			p.process(data);

		if (descriptor != null)
			featureVectors = descriptor.transform(data);

		if (featureVectors != null)
			for (IDataProcessor<NumericalFeatureVector> fvProcessor : featureVectorProcessors)
				if (fvProcessor != null)
					fvProcessor.process(featureVectors);

		return featureVectors;
	}

	public List<IDataProcessor<ITimeSeriesUnivariate>> getDataProcessors() {
		return dataProcessors;
	}

	public IDescriptor<ITimeSeriesUnivariate, NumericalFeatureVector> getDescriptor() {
		return descriptor;
	}

	public List<IDataProcessor<NumericalFeatureVector>> getFeatureVectorProcessors() {
		return featureVectorProcessors;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof TimeSeriesUnivariateDataMiningWorkflow))
			return false;

		TimeSeriesUnivariateDataMiningWorkflow other = (TimeSeriesUnivariateDataMiningWorkflow) o;

		return other.dataProcessors.equals(dataProcessors) && other.descriptor.equals(descriptor)
				&& other.featureVectorProcessors.equals(featureVectorProcessors)
				&& other.distanceMeasure.equals(distanceMeasure);
	}
}
