package com.github.TKnudsen.timeseries.data.multivariate.symbolic;

import com.github.TKnudsen.ComplexDataObject.model.tools.MathFunctions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.TKnudsen.timeseries.data.TimeSeriesWithTimeValuePairs;

/**
 * <p>
 * Title: TimeSeriesMultivariateSymbolic
 * </p>
 * 
 * <p>
 * Description: General implementation for symbolic multivariate time series
 * modeled with List<String>.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class TimeSeriesMultivariateSymbolic extends TimeSeriesWithTimeValuePairs<List<String>> implements ITimeSeriesMultivariateSymbolic {

	/**
	 * used for JSON, reflection, serialization & stuff
	 */
	@SuppressWarnings("unused")
	private TimeSeriesMultivariateSymbolic() {
		this(MathFunctions.randomLong(), new ArrayList<Long>(), new ArrayList<List<String>>(), "");
	}

	public TimeSeriesMultivariateSymbolic(List<Long> timeStamps, List<List<String>> values) {
		this(MathFunctions.randomLong(), timeStamps, values, null);
	}

	public TimeSeriesMultivariateSymbolic(List<Long> timeStamps, List<List<String>> values, String missingValueIndicator) {
		this(MathFunctions.randomLong(), timeStamps, values, missingValueIndicator);
	}

	public TimeSeriesMultivariateSymbolic(long id, List<Long> timeStamps, List<List<String>> values) {
		this(id, timeStamps, values, null);
	}

	public TimeSeriesMultivariateSymbolic(long id, List<Long> timeStamps, List<List<String>> values, String missingValueIndicator) {
		super(id, timeStamps, values, Arrays.asList(new String[] { missingValueIndicator }));
	}

	@Override
	public List<String> getValue(long timeStamp, boolean allowInterpolation) throws IndexOutOfBoundsException, IllegalArgumentException {
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