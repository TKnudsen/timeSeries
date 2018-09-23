package com.github.TKnudsen.timeseries.operations.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	/**
	 * 
	 * @param timeStamps
	 * @return List of timeStamp distances
	 */
	public static List<Long> getQuantizationList(List<Long> timeStamps) {
		List<Long> quantizationList = new ArrayList<Long>();
		if (timeStamps.size() > 0) {
			long prevTimeStamp = timeStamps.get(0);
			for (int i = 1; i < timeStamps.size(); i++) {
				long nextTimeStamp = timeStamps.get(i);
				quantizationList.add(Math.abs(nextTimeStamp - prevTimeStamp));
				prevTimeStamp = nextTimeStamp;
			}
		}
		return quantizationList;
	}

	/**
	 * 
	 * @param quantizationList
	 * @return Sorted distribution of timeStamp distances
	 */
	public static SortedMap<Long, Integer> calculateQuantizationDistribution(List<Long> quantizationList) {
		SortedMap<Long, Integer> quantizationDist = new TreeMap<Long, Integer>();		
		for (long quantization : quantizationList) {
			if (quantizationDist.containsKey(quantization)) {
				quantizationDist.put(quantization, quantizationDist.get(quantization) + 1);
			} else {
				quantizationDist.put(quantization, 1);
			}
		}
		return quantizationDist;
	}

	/**
	 * 
	 * @param quantizationList
	 * @return List containing the best timeStamp distances for equidistance (most occurrences). 
	 * This is a list because there could be several timeStamp distances with the same number of occurrences.
	 */
	public static List<Long> guessQuantization(List<Long> quantizationList) {
		SortedMap<Long, Integer> quantizationDist = calculateQuantizationDistribution(quantizationList);
		List<Entry<Long, Integer>> quantizationDistEntryList = new ArrayList<>(quantizationDist.entrySet());
		quantizationDistEntryList.sort(Collections.reverseOrder(Entry.comparingByValue()));
				
		List<Long> quantizationGuesses = new ArrayList<Long>();
		if (quantizationDistEntryList.size() > 0) {
			long firstGuess = quantizationDistEntryList.get(0).getKey();
			long firstGuessOccurences = quantizationDistEntryList.get(0).getValue();
			quantizationGuesses.add(firstGuess);
			if (quantizationDistEntryList.size() > 1) {
				int i = 1; 
				while (i < quantizationDistEntryList.size() && quantizationDistEntryList.get(i).getValue() == firstGuessOccurences) {
					quantizationGuesses.add(quantizationDistEntryList.get(i).getKey());
					i++;
				}
			}
		}				
		return quantizationGuesses;
	}
	
	/**
	 * 
	 * @param timeStampIndex
	 * @param quantizationList
	 * @return timeStamp distance for the given timeStamp index
	 */
	public static long getQuantizationFromTimeStampIndex(int timeStampIndex, List<Long> quantizationList) {		
		if(timeStampIndex > -1 && timeStampIndex < quantizationList.size()) {
			return quantizationList.get(timeStampIndex);			
		}
		return 0;		
	}

	/**
	 * 
	 * @param quantizationGuesses
	 * @param quantizationList
	 * @return timeStamp offset index for equidistance.
	 * The start of the timeStamp distance series with the most consecutive occurrences will be used
	 */
	public static int guessTemporalOffset(List<Long> quantizationGuesses, List<Long> quantizationList) {

		int maxConsecutive = 0;
		int offsetIndex = 0;

		for (long quantizationGuess : quantizationGuesses) {

			List<Integer> startIndexList = new ArrayList<Integer>();

			if (quantizationList.get(0).equals(quantizationGuess)) {
				startIndexList.add(0);
			}
			for (int i = 1; i < quantizationList.size(); i++) {
				if (quantizationList.get(i).equals(quantizationGuess)) {
					if (!quantizationList.get(i - 1).equals(quantizationGuess)) {
						startIndexList.add(i);
					}
				}
			}

			Map<Integer, Integer> startIndexCount = new HashMap<Integer, Integer>();
			
			for (int startIndex : startIndexList) {
				startIndexCount.put(startIndex, 0);
				for (int i = startIndex; i < quantizationList.size(); i++) {
					if (quantizationList.get(i).equals(quantizationGuess)) {
						startIndexCount.put(startIndex, startIndexCount.get(startIndex) + 1);
					} else {
						break;
					}
				}
			}

			for (Entry<Integer, Integer> entry : startIndexCount.entrySet()) {
				if (maxConsecutive < entry.getValue()) {
					maxConsecutive = entry.getValue();
					offsetIndex = entry.getKey();
				}
			}
		}

		return offsetIndex;
	}
}
