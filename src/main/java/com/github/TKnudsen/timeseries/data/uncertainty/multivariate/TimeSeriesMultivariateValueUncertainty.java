package com.github.TKnudsen.timeseries.data.uncertainty.multivariate;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.IValueUncertainty;
import com.github.TKnudsen.timeseries.data.ITimeSeries;

public class TimeSeriesMultivariateValueUncertainty implements ITimeSeriesMultivariateValueUncertainty {

//	private final ITimeSeries<List<Double>> timeSeries;

	private final ITimeSeries<List<IValueUncertainty>> valueUncertainties;

	private final boolean relative;

	public TimeSeriesMultivariateValueUncertainty(ITimeSeries<List<IValueUncertainty>> valueUncertainties,
			boolean relative) {
		this.valueUncertainties = valueUncertainties;
		this.relative = relative;
	}

	@Override
	public ITimeSeries<List<IValueUncertainty>> getValueUncertainties() {
		return valueUncertainties;
	}

//	@Override
//	public ITimeSeries<List<Double>> getTimeSeries() {
//		return timeSeries;
//	}

	@Override
	public boolean isRelative() {
		return relative;
	}

}
