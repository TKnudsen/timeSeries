package com.github.TKnudsen.timeseries.operations.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.github.TKnudsen.timeseries.data.ITimeSeries;
import com.github.TKnudsen.timeseries.data.primitives.TimeQuantization;

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

	public static boolean isQuantizationValid(long quantization, TimeQuantization qType, ITimeSeries<?> tsd) {
		long binCount = tsd.size();
		long startTime = tsd.getFirstTimestamp();
		long endTime = tsd.getLastTimestamp();
		long newBinCount = (endTime - startTime) / quantization / quantizationTypeToLong(qType);
		return newBinCount < binCount * 10;
	}
	
	public static SortedMap<Long, Integer> calculateQuantizationDistribution(ITimeSeries<?> tsd) {		
		SortedMap<Long, Integer> quantizationDist = new TreeMap<Long, Integer>();
		long prevTimeStamp = tsd.getFirstTimestamp();
		for(int i = 1; i < tsd.getTimestamps().size(); i++) {
			long nextTimeStamp = tsd.getTimestamp(i);			
			long quantization = Math.abs(nextTimeStamp - prevTimeStamp);
			if(quantizationDist.containsKey(quantization)) {
				quantizationDist.put(quantization, quantizationDist.get(quantization) + 1);
			} else {
				quantizationDist.put(quantization, 1);
			}
			prevTimeStamp = nextTimeStamp;
		}
		return quantizationDist;		
	}
	
	public static long guessQuantization(ITimeSeries<?> tsd) {				
		SortedMap<Long, Integer> quantizationDist = calculateQuantizationDistribution(tsd);		
		List<Entry<Long, Integer>> entrySet = new ArrayList<>(quantizationDist.entrySet());
		entrySet.sort(Entry.comparingByValue());
		for(Entry<Long, Integer> entry : entrySet) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
		long quantizationGuess = 0;
		if(entrySet.size() > 0) {
			quantizationGuess = entrySet.get(0).getKey();
		}			
		return quantizationGuess;		
	}
}
