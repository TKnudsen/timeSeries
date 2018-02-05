package com.github.TKnudsen.timeseries.operations.workflow;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeatureVector;
import com.github.TKnudsen.ComplexDataObject.model.workflow.IDataMiningWorkflow;
import com.github.TKnudsen.timeseries.data.ITimeSeries;
import com.github.TKnudsen.timeseries.operations.preprocessing.ITimeSeriesPreprocessor;
import com.github.TKnudsen.timeseries.operations.transformations.descriptors.ITimeSeriesDescriptor;

/**
 * <p>
 * Title: ITimeSeriesDataMiningWorkflow
 * </p>
 * 
 * <p>
 * Description: data mining workflow interface for time series.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public interface ITimeSeriesDataMiningWorkflow<TS extends ITimeSeries<?>, DESC extends ITimeSeriesDescriptor<? super TS>> extends IDataMiningWorkflow<TS, Double, NumericalFeatureVector, DESC> {
}
