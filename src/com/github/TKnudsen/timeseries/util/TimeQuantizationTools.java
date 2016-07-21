package com.github.TKnudsen.timeseries.util;

import com.github.TKnudsen.timeseries.data.TimeQuantization;
import com.github.TKnudsen.timeseries.data.TimeSeries;

/**
 * <p>
 * Title: TimeQuantizationTools
 * </p>
 * 
 * <p>
 * Description: 
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012-2016
 * </p>
 * 
 * @author Juergen Bernard
 */
public class TimeQuantizationTools {
	public static long quantizationTypeToLong(TimeQuantization quantizationType) {
		switch (quantizationType) {
		case MILLISECONDS:
			return 1;
		case SECONDS:
			return 1000;
		case MINUTES:
			return 60 * 1000;
		case HOURS:
			return 3600 * 1000;
		case DAYS:
			return 24 * 3600 * 1000;
		case WEEKS:
			return 7 * 24 * 3600 * 1000;
		case MONTHS:
			return 30 * 24 * 3600 * 1000;
		case QUARTER:
			return 3 * 30 * 24 * 3600 * 1000;
		case YEARS:
			return 365 * 24 * 3600 * 1000;
		case DECADES:
			return 10 * 365 * 24 * 3600 * 1000;
		default:
			return 0;
		}
	}

	public static boolean isQuantizationValid(long quantization, TimeQuantization qType, TimeSeries<?> tsd) {
		long binCount = tsd.size();
		long startTime = tsd.getFirstTimestamp();
		long endTime = tsd.getLastTimestamp();
		long newBinCount = (endTime - startTime) / quantization / quantizationTypeToLong(qType);
		return newBinCount < binCount * 10;
	}
}
