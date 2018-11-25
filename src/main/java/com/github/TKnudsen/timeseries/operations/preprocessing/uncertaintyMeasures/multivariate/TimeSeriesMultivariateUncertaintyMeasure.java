package com.github.TKnudsen.timeseries.operations.preprocessing.uncertaintyMeasures.multivariate;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.IValueUncertainty;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.uncertainty.multivariate.ITimeSeriesMultivariateValueUncertaintyCalculationResult;
import com.github.TKnudsen.timeseries.operations.preprocessing.uncertaintyMeasures.TimeSeriesUncertaintyMeasure;

/**
 * <p>
 * timeSeries
 * </p>
 * 
 * <p>
 * Basic class for calculations of value uncertainty information for univariate
 * time series. The measure can be attached to a process that produces
 * uncertainties using the listener concept.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public abstract class TimeSeriesMultivariateUncertaintyMeasure
		extends TimeSeriesUncertaintyMeasure<ITimeSeriesMultivariate, IValueUncertainty, List<IValueUncertainty>> {

	public ITimeSeriesMultivariateValueUncertaintyCalculationResult getTimeSeriesValueUncertainty() {
		return (ITimeSeriesMultivariateValueUncertaintyCalculationResult) timeSeriesValueUncertainty;
	}
}
