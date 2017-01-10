package com.github.TKnudsen.timeseries.operations.preprocessing;

import com.github.TKnudsen.ComplexDataObject.model.preprocessing.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.preprocessing.IParameterSupport;
import com.github.TKnudsen.timeseries.data.ITimeSeries;

/**
 * <p>
 * Title: ITimeSeriesPreprocessor
 * </p>
 * 
 * <p>
 * Description: interface for time series pre-processing routines
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public interface ITimeSeriesPreprocessor<TS extends ITimeSeries<?>> extends IDataProcessor<TS>, IParameterSupport<TS> {

}