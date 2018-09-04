package com.github.TKnudsen.timeseries.operations.preprocessing;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.TKnudsen.timeseries.data.ITimeSeriesListener;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;

/**
 * <p>
 * Title: TimeSeriesMultivariateTimeSeriesProcessor
 * </p>
 * 
 * <p>
 * Description: Basic class for all MultivariateTimeSeries Processors.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.04
 */
public abstract class TimeSeriesMultivariateTimeSeriesProcessor extends TimeSeriesProcessor<ITimeSeriesMultivariate> {

	private final List<ITimeSeriesListener> timeSeriesListeners = new CopyOnWriteArrayList<ITimeSeriesListener>();

}
