package com.github.TKnudsen.timeseries.data.univariate.symbolic;

import java.util.Map;

import com.github.TKnudsen.timeseries.data.ITimeSeries;

/**
 * <p>
 * Title: ITimeSeriesSymbolicDistribution
 * </p>
 * 
 * <p>
 * Description: Interface for symbolic univariate time series with Symbol
 * distributions. Use case: uncertainty data modeling, representations of label
 * probabilities over time. modeled with String.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public interface ITimeSeriesSymbolicDistribution extends ITimeSeries<Map<String, Double>> {

}
