package com.github.TKnudsen.timeseries.operations.tools;

import java.util.ArrayList;
import java.util.Collections;
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

	/**
	 * 
	 * @param timeStamps time stamps
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
	 * @param quantizationList list
	 * @return Sorted distribution of timeStamp distances
	 */
	public static SortedMap<Long, Integer> calculateQuantizationDistribution(List<Long> quantizationList) {
		SortedMap<Long, Integer> quantizationDist = new TreeMap<Long, Integer>();
		for (Long quantization : quantizationList) {
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
	 * @param a
	 * @param b
	 * @return long
	 */
	private static Long gcd(Long a, Long b) {
		if (a == 0)
			return b;
		return gcd(b % a, a);
	}

	/**
	 * 
	 * @param quantizationList list
	 * @return long
	 */
	private static Long findGCD(List<Long> quantizationList) {
		Long result = quantizationList.get(0);
		for (int i = 1; i < quantizationList.size(); i++) {
			result = gcd(quantizationList.get(i), result);
		}
		return result;
	}

	/**
	 * 
	 * @param quantizationList list
	 * @return longs
	 */
	private static List<Long> findQuantizationWithMostOccurences(List<Long> quantizationList) {

		List<Long> result = new ArrayList<Long>();

		SortedMap<Long, Integer> quantizationDist = calculateQuantizationDistribution(quantizationList);
		List<Entry<Long, Integer>> quantizationDistEntryList = new ArrayList<>(quantizationDist.entrySet());
		quantizationDistEntryList.sort(Collections.reverseOrder(Entry.comparingByValue()));

		if (quantizationDistEntryList.size() > 0) {
			long firstGuess = quantizationDistEntryList.get(0).getKey();
			long firstGuessOccurences = quantizationDistEntryList.get(0).getValue();
			result.add(firstGuess);
			if (quantizationDistEntryList.size() > 1) {
				int i = 1;
				while (i < quantizationDistEntryList.size()
						&& quantizationDistEntryList.get(i).getValue() == firstGuessOccurences) {
					result.add(quantizationDistEntryList.get(i).getKey());
					i++;
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param quantizationList list
	 * @param gcd              gcd
	 * @return List with quantization suggestions, criterion: most occurrences and
	 *         greatest common divisor
	 */
	public static List<Long> guessQuantization(List<Long> quantizationList, boolean gcd) {

		List<Long> quantizationGuesses = new ArrayList<Long>();

		if (!gcd) {
			quantizationGuesses.addAll(findQuantizationWithMostOccurences(quantizationList));
		} else {
			quantizationGuesses.add(findGCD(quantizationList));
		}
		return quantizationGuesses;
	}

	/**
	 *
	 * @param <X> x
	 * @param <Y> y
	 */
	public static class TimeStampQuantizationTuple<X, Y> {
		public final X timeStamp;
		public final Y quantization;

		public TimeStampQuantizationTuple(X timeStamp, Y quantization) {
			this.timeStamp = timeStamp;
			this.quantization = quantization;
		}
	}

	/**
	 * 
	 * @param quantizationGuesses q
	 * @param timeStamps          time stamps
	 * @return time stamp quantization
	 */
	public static TimeStampQuantizationTuple<Long, Long> guessStartTimeStamp(List<Long> quantizationGuesses,
			List<Long> timeStamps) {

		Long bestStartTimeStamp = timeStamps.get(0);
		Long bestQuantization = quantizationGuesses.get(0);

		int minimalCountAllGuesses = Integer.MAX_VALUE;

		for (Long quantizationCandidate : quantizationGuesses) {

			Long startTimeStampCandidate = bestStartTimeStamp;

			int minimalCountForQuantizationGuess = Integer.MAX_VALUE;

			for (Long startTimeStamp : timeStamps) {

				int opCountForStartTimeStamp = 0;

				Long timeStamp = startTimeStamp;
				int i = 1;

				Long firstTimeStamp = timeStamps.get(0);
				Long lastTimeStamp = timeStamps.get(timeStamps.size() - 1);

				while (timeStamp < lastTimeStamp) {

					timeStamp = startTimeStamp + i * quantizationCandidate;
					long prevTimeStamp = timeStamp - quantizationCandidate;

					if (!timeStamps.contains(timeStamp)) {
						opCountForStartTimeStamp++;
					}
					for (Long tS : timeStamps) {
						if (prevTimeStamp < tS && tS < timeStamp) {
							opCountForStartTimeStamp++;
						}
					}
					i++;
				}

				timeStamp = startTimeStamp;
				i = 1;

				while (timeStamp > firstTimeStamp) {

					timeStamp = startTimeStamp - i * quantizationCandidate;
					long nextTimeStamp = timeStamp + quantizationCandidate;

					if (!timeStamps.contains(timeStamp)) {
						opCountForStartTimeStamp++;
					}
					for (Long tS : timeStamps) {
						if (timeStamp < tS && tS < nextTimeStamp) {
							opCountForStartTimeStamp++;
						}
					}
					i++;
				}

				if (opCountForStartTimeStamp < minimalCountForQuantizationGuess) {
					minimalCountForQuantizationGuess = opCountForStartTimeStamp;
					startTimeStampCandidate = startTimeStamp;
				}
			}

			if (minimalCountForQuantizationGuess < minimalCountAllGuesses) {
				minimalCountAllGuesses = minimalCountForQuantizationGuess;
				bestQuantization = quantizationCandidate;
				bestStartTimeStamp = startTimeStampCandidate;
			}
		}
		return new TimeStampQuantizationTuple<Long, Long>(bestStartTimeStamp, bestQuantization);
	}
}
