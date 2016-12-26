package com.github.TKnudsen.timeseries.operations.workflow.univariate;

import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.ITimeSeriesUnivariatePreprocessor;
import com.github.TKnudsen.timeseries.operations.workflow.ITimeSeriesDataMiningWorkflow;

/**
 * <p>
 * Title: ITimeSeriesUnivariateDataMiningWorkflow
 * </p>
 * 
 * <p>
 * Description: data mining workflow interface for univariate time series.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public interface ITimeSeriesUnivariateDataMiningWorkflow extends ITimeSeriesDataMiningWorkflow<TimeSeriesUnivariate, ITimeSeriesUnivariatePreprocessor> {

}
