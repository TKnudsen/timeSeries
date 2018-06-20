package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.List;

import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.ITimeSeriesPreprocessor;

/**
 * <p>
 * Title: ITimeSeriesMultivariatePreprocessor
 * </p>
 * 
 * <p>
 * Description: interface for preprocessing multivariate time series.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.04
 */
public interface ITimeSeriesMultivariatePreprocessor extends ITimeSeriesPreprocessor<ITimeSeriesMultivariate> {

	public void process(List<ITimeSeriesMultivariate> data);

}