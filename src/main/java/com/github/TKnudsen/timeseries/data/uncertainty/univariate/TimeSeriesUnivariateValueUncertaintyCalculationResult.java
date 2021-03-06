package com.github.TKnudsen.timeseries.data.uncertainty.univariate;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.IValueUncertainty;
import com.github.TKnudsen.timeseries.data.ITimeSeries;

public class TimeSeriesUnivariateValueUncertaintyCalculationResult
		implements ITimeSeriesUnivariateValueUncertaintyCalculationResult {

	private ITimeSeries<IValueUncertainty> uncertaintyValues;
	private boolean relative;

	@SuppressWarnings("unused")
	private TimeSeriesUnivariateValueUncertaintyCalculationResult() {
	}

	public TimeSeriesUnivariateValueUncertaintyCalculationResult(ITimeSeries<IValueUncertainty> uncertaintyValues,
			boolean relative) {
		this.uncertaintyValues = uncertaintyValues;
		this.relative = relative;
	}

	@Override
	public ITimeSeries<IValueUncertainty> getValueUncertainties() {
		return uncertaintyValues;
	}

	@Override
	public boolean isRelative() {
		return relative;
	}

}
