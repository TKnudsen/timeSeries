package com.github.TKnudsen.timeseries.data.uncertainty.multivariate;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.IValueUncertainty;
import com.github.TKnudsen.timeseries.data.TimeSeries;

public class UncertaintyMultivariateTimeSeries<U extends IValueUncertainty> extends TimeSeries<List<U>> {

	public UncertaintyMultivariateTimeSeries(List<Long> timeStamps, List<List<U>> valueUncertainties) {
		super(timeStamps, valueUncertainties);
	}

	@Override
	protected long valueToHash(List<U> value) {
		return value.hashCode();
	}

	@Override
	protected List<U> interpolateValue(long timeStamp, long lBefore, long lAfter, List<U> vBefore, List<U> vAfter) {
		return vBefore;
	}

}
