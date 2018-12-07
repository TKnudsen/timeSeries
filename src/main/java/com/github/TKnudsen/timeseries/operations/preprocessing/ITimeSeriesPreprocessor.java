package com.github.TKnudsen.timeseries.operations.preprocessing;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.IParameterSupport;

import com.github.TKnudsen.timeseries.data.ITimeSeries;

/**
 * <p>
 * Title: ITimeSeriesPreprocessor
 * </p>
 * 
 * <p>
 * Description: interface for time series processing routines
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.04
 */
public interface ITimeSeriesPreprocessor<TS extends ITimeSeries<?>> extends IDataProcessor<TS>, IParameterSupport<TS> {

}