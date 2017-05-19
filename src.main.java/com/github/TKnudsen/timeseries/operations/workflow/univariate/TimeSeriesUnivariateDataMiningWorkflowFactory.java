package com.github.TKnudsen.timeseries.operations.workflow.univariate;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.enums.NormalizationType;
import com.github.TKnudsen.ComplexDataObject.model.distanceMeasure.featureVector.INumericalFeatureVectorDistanceMeasure;
import com.github.TKnudsen.ComplexDataObject.model.processors.features.numericalData.INumericalFeatureVectorProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.features.numericalData.NormalizationRoutineFactory;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.ITimeSeriesUnivariatePreprocessor;
import com.github.TKnudsen.timeseries.operations.transformations.descriptors.univariate.ITimeSeriesUnivariateDescriptor;

/**
 * <p>
 * Title: TimeSeriesUnivariateDataMiningWorkflowFactory
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
 * @author Juergen Bernard
 * @version 1.01
 */
public class TimeSeriesUnivariateDataMiningWorkflowFactory {

	public static TimeSeriesUnivariateDataMiningWorkflow createWorkflow(List<ITimeSeriesUnivariatePreprocessor> preprocessingRoutines, ITimeSeriesUnivariateDescriptor descriptor, NormalizationType normalizationType,
			INumericalFeatureVectorDistanceMeasure distanceMeasure) {

		TimeSeriesUnivariateDataMiningWorkflow workflow = new TimeSeriesUnivariateDataMiningWorkflow();

		for (ITimeSeriesUnivariatePreprocessor processor : preprocessingRoutines)
			workflow.addPreProcessor(processor);

		workflow.setDescriptor(descriptor);

		INumericalFeatureVectorProcessor normalizationRoutine = NormalizationRoutineFactory.createNormalizationRoutine(normalizationType);
		workflow.addFeatureProcessor(normalizationRoutine);

		workflow.setDistanceMeasure(distanceMeasure);

		return workflow;

	}
}
