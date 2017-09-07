package com.github.TKnudsen.timeseries.data.primitives;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.ITimeValuePair;

/**
 * <p>
 * Title: TimeValuePairTools
 * </p>
 * 
 * <p>
 * Description: Little helpers for time-value pairs.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class TimeValuePairTools {

	public static <T> List<Long> getTimeStamps(List<ITimeValuePair<T>> timeValuePairs) {
		if (timeValuePairs == null)
			return null;

		List<Long> timeStamps = new ArrayList<>();
		for (ITimeValuePair<T> pair : timeValuePairs)
			timeStamps.add(pair.getTimestamp());

		return timeStamps;
	}

	public static <T> List<T> getValues(List<ITimeValuePair<T>> timeValuePairs) {
		if (timeValuePairs == null)
			return null;

		List<T> values = new ArrayList<>();
		for (ITimeValuePair<T> pair : timeValuePairs)
			values.add(pair.getValue());

		return values;
	}
}
