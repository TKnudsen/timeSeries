package com.github.TKnudsen.timeseries.data.symbolic.univariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.tools.MathFunctions;
import com.github.TKnudsen.timeseries.data.TimeSeries;

public class TimeSeriesUnivariateSymbolic extends TimeSeries<String> implements ITimeSeriesUnivariateSymbolic {

	/**
	 * used for JSON, reflection, serialization & stuff
	 */
	@SuppressWarnings("unused")
	private TimeSeriesUnivariateSymbolic() {
		this(MathFunctions.randomLong(), new ArrayList<Long>(), new ArrayList<String>(), "");
	}

	public TimeSeriesUnivariateSymbolic(List<Long> timeStamps, List<String> values) {
		this(MathFunctions.randomLong(), timeStamps, values, null);
	}

	public TimeSeriesUnivariateSymbolic(List<Long> timeStamps, List<String> values, String missingValueIndicator) {
		this(MathFunctions.randomLong(), timeStamps, values, missingValueIndicator);
	}

	public TimeSeriesUnivariateSymbolic(long id, List<Long> timeStamps, List<String> values) {
		this(id, timeStamps, values, null);
	}

	public TimeSeriesUnivariateSymbolic(long id, List<Long> timeStamps, List<String> values, String missingValueIndicator) {
		super(id, timeStamps, values, missingValueIndicator);
	}

	@Override
	public String getValue(long timeStamp, boolean allowInterpolation) throws IndexOutOfBoundsException, IllegalArgumentException {
		if (allowInterpolation)
			throw new UnsupportedOperationException("Symbolic time series cannot be addressed with interpolation call.");

		try {
			int index = findByDate(timeStamp, true);
			return getValue(index);
		} catch (IllegalArgumentException e) {

		}

		throw new IllegalArgumentException("TimeSeriesUnivariate.getValue(long): time stamp does not exist");
	}

}
