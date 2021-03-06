package com.github.TKnudsen.timeseries.data.uncertainty.multivariate;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.IValueUncertainty;
import com.github.TKnudsen.timeseries.data.ITimeSeries;

public class TimeSeriesMultivariateValueUncertaintyCalculationResult
		implements ITimeSeriesMultivariateValueUncertaintyCalculationResult {

	private ITimeSeries<List<IValueUncertainty>> valueUncertainties;
	
	private boolean relative;

	@SuppressWarnings("unused")
	private TimeSeriesMultivariateValueUncertaintyCalculationResult() {
	}

	public TimeSeriesMultivariateValueUncertaintyCalculationResult(
			ITimeSeries<List<IValueUncertainty>> valueUncertainties, boolean relative) {
		this.valueUncertainties = valueUncertainties;
		this.relative = relative;
	}

	@Override
	public ITimeSeries<List<IValueUncertainty>> getValueUncertainties() {
		return valueUncertainties;
	}

	@Override
	public boolean isRelative() {
		return relative;
	}

}
