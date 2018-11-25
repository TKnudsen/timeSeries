package com.github.TKnudsen.timeseries.operations.preprocessing.uncertaintyMeasures.univariate;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.IValueUncertainty;
import com.github.TKnudsen.timeseries.data.uncertainty.univariate.ITimeSeriesUnivariateValueUncertainty;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
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
 * @version 1.04
 */
public abstract class TimeSeriesUnivariateUncertaintyMeasure
		extends TimeSeriesUncertaintyMeasure<ITimeSeriesUnivariate, IValueUncertainty, IValueUncertainty> {

	protected ITimeSeriesUnivariateValueUncertainty timeSeriesValueUncertainty;

	public ITimeSeriesUnivariateValueUncertainty getTimeSeriesValueUncertainty() {
		return timeSeriesValueUncertainty;
	}
}
