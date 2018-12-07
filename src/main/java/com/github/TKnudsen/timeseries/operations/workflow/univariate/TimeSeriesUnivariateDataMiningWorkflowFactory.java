package com.github.TKnudsen.timeseries.operations.workflow.univariate;

import com.github.TKnudsen.ComplexDataObject.data.enums.NormalizationType;
import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeatureVector;
import com.github.TKnudsen.ComplexDataObject.model.distanceMeasure.IDistanceMeasure;
import com.github.TKnudsen.ComplexDataObject.model.processors.features.numericalData.INumericalFeatureVectorProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.features.numericalData.NormalizationRoutineFactory;
import com.github.TKnudsen.ComplexDataObject.model.transformations.descriptors.numericalFeatures.INumericFeatureVectorDescriptor;

import java.util.List;

import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.ITimeSeriesPreprocessor;

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
 * Copyright: Copyright (c) 2017-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public class TimeSeriesUnivariateDataMiningWorkflowFactory {

	public static TimeSeriesUnivariateDataMiningWorkflow createWorkflow(
			List<ITimeSeriesPreprocessor<ITimeSeriesUnivariate>> preprocessingRoutines,
			INumericFeatureVectorDescriptor<ITimeSeriesUnivariate> descriptor, NormalizationType normalizationType,
			IDistanceMeasure<NumericalFeatureVector> distanceMeasure) {

		TimeSeriesUnivariateDataMiningWorkflow workflow = new TimeSeriesUnivariateDataMiningWorkflow();

		for (ITimeSeriesPreprocessor<ITimeSeriesUnivariate> processor : preprocessingRoutines)
			workflow.addPreProcessor(processor);

		workflow.setDescriptor(descriptor);

		INumericalFeatureVectorProcessor normalizationRoutine = NormalizationRoutineFactory
				.createNormalizationRoutine(normalizationType);
		workflow.addFeatureProcessor(normalizationRoutine);

		workflow.setDistanceMeasure(distanceMeasure);

		return workflow;

	}
}
