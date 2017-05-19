package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.List;

import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.ITimeSeriesPreprocessor;

/**
 * <p>
 * Title: ITimeSeriesUnivariatePreprocessor
 * </p>
 * 
 * <p>
 * Description: interface for preprocessing univariate time series.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.04
 */
public interface ITimeSeriesUnivariatePreprocessor extends ITimeSeriesPreprocessor<ITimeSeriesUnivariate> {

	public void process(List<ITimeSeriesUnivariate> data);
}
