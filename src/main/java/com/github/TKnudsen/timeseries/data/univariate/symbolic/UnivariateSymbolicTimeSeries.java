package com.github.TKnudsen.timeseries.data.univariate.symbolic;

import com.github.TKnudsen.ComplexDataObject.model.tools.MathFunctions;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.TimeSeriesWithTimeValuePairs;

/**
 * <p>
 * Title: UnivariateSymbolicTimeSeries
 * </p>
 * 
 * <p>
 * Description: General implementation for symbolic univariate time series
 * modeled with String.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public class UnivariateSymbolicTimeSeries extends TimeSeriesWithTimeValuePairs<String> implements ISymbolicTimeSeries {

	/**
	 * used for JSON, reflection, serialization & stuff
	 */
	@SuppressWarnings("unused")
	private UnivariateSymbolicTimeSeries() {
		this(MathFunctions.randomLong(), new ArrayList<Long>(), new ArrayList<String>(), "");
	}

	public UnivariateSymbolicTimeSeries(List<Long> timeStamps, List<String> values) {
		this(MathFunctions.randomLong(), timeStamps, values, null);
	}

	public UnivariateSymbolicTimeSeries(List<Long> timeStamps, List<String> values, String missingValueIndicator) {
		this(MathFunctions.randomLong(), timeStamps, values, missingValueIndicator);
	}

	public UnivariateSymbolicTimeSeries(long id, List<Long> timeStamps, List<String> values) {
		this(id, timeStamps, values, null);
	}

	public UnivariateSymbolicTimeSeries(long id, List<Long> timeStamps, List<String> values,
			String missingValueIndicator) {
		super(id, timeStamps, values, missingValueIndicator);
	}

	@Override
	public String getValue(long timeStamp, boolean allowInterpolation)
			throws IndexOutOfBoundsException, IllegalArgumentException {
		if (allowInterpolation)
			throw new UnsupportedOperationException(
					"Symbolic time series cannot be addressed with interpolation call.");

		try {
			int index = findByDate(timeStamp, true);
			return getValue(index);
		} catch (IllegalArgumentException e) {

		}

		throw new IllegalArgumentException("TimeSeriesUnivariate.getValue(long): time stamp does not exist");
	}
}
