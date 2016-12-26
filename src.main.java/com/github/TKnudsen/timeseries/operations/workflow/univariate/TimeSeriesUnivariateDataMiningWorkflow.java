package com.github.TKnudsen.timeseries.operations.workflow.univariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeatureVector;
import com.github.TKnudsen.ComplexDataObject.model.distanceMeasure.IDistanceMeasure;
import com.github.TKnudsen.ComplexDataObject.model.preprocessing.features.IFeatureVectorProcessor;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.descriptors.univariate.ITimeSeriesUnivariateDescriptor;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.ITimeSeriesUnivariatePreprocessor;

/**
 * <p>
 * Title: TimeSeriesUnivariateDataMiningWorkflow
 * </p>
 * 
 * <p>
 * Description: basic implementation for data mining workflows applied on
 * univariate time series.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public class TimeSeriesUnivariateDataMiningWorkflow implements ITimeSeriesUnivariateDataMiningWorkflow {

	List<ITimeSeriesUnivariatePreprocessor> dataProcessors = new ArrayList<>();
	ITimeSeriesUnivariateDescriptor descriptor = null;

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
		// TODO Auto-generated method stub

	}

	@Override
	public void addFeatureProcessor(IFeatureVectorProcessor<Double, NumericalFeatureVector> featureProcessor) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<NumericalFeatureVector> apply(List<TimeSeriesUnivariate> t) {
		// TODO Auto-generated method stub
		return null;
	}

}
