package com.github.TKnudsen.timeseries.data.uncertainty.univariate;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.IValueUncertainty;
import com.github.TKnudsen.timeseries.data.ITimeSeries;

public class TimeSeriesUnivariateValueUncertainty implements ITimeSeriesUnivariateValueUncertainty {

//	private final ITimeSeries<Double> timeSeries;
	private final ITimeSeries<IValueUncertainty> uncertaintyValues;
	private final boolean relative;

	public TimeSeriesUnivariateValueUncertainty(ITimeSeries<IValueUncertainty> uncertaintyValues, boolean relative) {
		this.uncertaintyValues = uncertaintyValues;
		this.relative = relative;
	}

	@Override
	public ITimeSeries<IValueUncertainty> getValueUncertainties() {
		return uncertaintyValues;
	}

//	@Override
//	public ITimeSeries<Double> getTimeSeries() {
//		return timeSeries;
//	}

	@Override
	public boolean isRelative() {
		return relative;
	}

}
