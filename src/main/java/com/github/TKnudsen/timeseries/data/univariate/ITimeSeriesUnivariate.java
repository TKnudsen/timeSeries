package com.github.TKnudsen.timeseries.data.univariate;

import com.github.TKnudsen.ComplexDataObject.data.interfaces.IKeyValueProvider;

import com.github.TKnudsen.timeseries.data.ITimeSeries;

/**
 * <p>
 * Title: TimeSeriesUnivariate
 * </p>
 * 
 * <p>
 * Description: Interface for numerical univariate time series.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.05
 */
public interface ITimeSeriesUnivariate extends ITimeSeries<Double>, IKeyValueProvider<Object> {

}
