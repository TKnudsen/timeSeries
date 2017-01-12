package com.github.TKnudsen.timeseries.operations.preprocessing;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.preprocessing.IDataProcessor;
import com.github.TKnudsen.timeseries.data.ITimeSeries;

/**
 * <p>
 * Title: ITimeSeriesPreprocessing
 * </p>
 * 
 * <p>
 * Description: interface for time series preprocessor routines
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public interface ITimeSeriesPreprocessing<TS extends ITimeSeries<?>> extends IDataProcessor<TS> {

	public void process(List<TS> data);
}
