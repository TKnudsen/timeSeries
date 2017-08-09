package com.github.TKnudsen.timeseries.operations.tools;

import com.github.TKnudsen.timeseries.data.ITimeSeries;

/**
 * 
 * @author Christian Ritter
 *
 */
public class TimeSeriesCharacteristicsTools {

	public static <O> boolean isEquidistant(ITimeSeries<O> timeseries) {
		if (timeseries.size() < 3)
			return true;
		long d = Math.abs(timeseries.getTimestamp(0) - timeseries.getTimestamp(1));
		for (int i = 1; i < timeseries.size() - 1; i++) {
			if (d != Math.abs(timeseries.getTimestamp(i) - timeseries.getTimestamp(i - 1)))
				return false;
		}
		return true;
	}

	public static <O> boolean hasSortedTimeStamps(ITimeSeries<O> timeseries) {
		if (timeseries.size() < 2)
			return true;
		for (int i = 1; i < timeseries.size(); i++) {
			if (timeseries.getTimestamp(i - 1) > timeseries.getTimestamp(i))
				return false;
		}
		return true;
	}

}
