package com.github.TKnudsen.timeseries.data.uncertainty.multivariate;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.IValueUncertainty;
import com.github.TKnudsen.timeseries.data.TimeSeries;

public class UncertaintyMultivariateTimeSeries extends TimeSeries<List<IValueUncertainty>> {

	public UncertaintyMultivariateTimeSeries(List<Long> timeStamps, List<List<IValueUncertainty>> valueUncertainties) {
		super(timeStamps, valueUncertainties);
	}

	@Override
	protected long valueToHash(List<IValueUncertainty> value) {
		return value.hashCode();
	}

	@Override
	protected List<IValueUncertainty> interpolateValue(long timeStamp, long lBefore, long lAfter,
			List<IValueUncertainty> vBefore, List<IValueUncertainty> vAfter) {
		return vBefore;
	}

}
