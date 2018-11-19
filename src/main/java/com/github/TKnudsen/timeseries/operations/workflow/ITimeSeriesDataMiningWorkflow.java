package com.github.TKnudsen.timeseries.operations.workflow;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeatureVector;
import com.github.TKnudsen.ComplexDataObject.model.workflow.IDataMiningWorkflow;

import com.github.TKnudsen.timeseries.data.ITimeSeries;

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
 * Copyright: Copyright (c) 2016-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public interface ITimeSeriesDataMiningWorkflow<TS extends ITimeSeries<?>>
		extends IDataMiningWorkflow<TS, Double, NumericalFeatureVector> {
}
