package com.github.TKnudsen.timeseries.data.uncertainty.univariate;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.IValueUncertainty;
import com.github.TKnudsen.timeseries.data.TimeSeries;

public class UncertaintyTimeSeries<U extends IValueUncertainty> extends TimeSeries<U> {

	public UncertaintyTimeSeries(List<Long> timeStamps, List<U> valueUncertainties) {
		super(timeStamps, valueUncertainties);
	}

	@Override
	protected long valueToHash(U value) {
		return value.hashCode();
	}

	@Override
	protected U interpolateValue(long timeStamp, long lBefore, long lAfter, U vBefore, U vAfter) {
		return vBefore;
	}

}
