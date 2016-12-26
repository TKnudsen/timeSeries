package com.github.TKnudsen.timeseries.operations.workflow.multivariate;

import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.operations.descriptors.multivariate.ITimeSeriesMultivariateDescriptor;
import com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.ITimeSeriesMultivariatePreprocessor;
import com.github.TKnudsen.timeseries.operations.workflow.ITimeSeriesDataMiningWorkflow;

/**
 * <p>
 * Title: ITimeSeriesMultivariateDataMiningWorkflow
 * </p>
 * 
 * <p>
 * Description: data mining workflow interface for multivariate time series.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public interface ITimeSeriesMultivariateDataMiningWorkflow extends ITimeSeriesDataMiningWorkflow<ITimeSeriesMultivariate, ITimeSeriesMultivariatePreprocessor, ITimeSeriesMultivariateDescriptor> {

}
