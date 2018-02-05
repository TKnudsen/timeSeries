package com.github.TKnudsen.timeseries.operations.workflow.multivariate;

import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.operations.transformations.descriptors.multivariate.ITimeSeriesMultivariateDescriptor;
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
 * Copyright: Copyright (c) 2016-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.04
 */
public interface ITimeSeriesMultivariateDataMiningWorkflow
		extends ITimeSeriesDataMiningWorkflow<ITimeSeriesMultivariate, ITimeSeriesMultivariateDescriptor> {

}
