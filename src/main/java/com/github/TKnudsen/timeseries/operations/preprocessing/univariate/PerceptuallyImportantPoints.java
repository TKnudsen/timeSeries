package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import com.github.TKnudsen.ComplexDataObject.data.entry.EntryWithComparableKey;
import com.github.TKnudsen.ComplexDataObject.data.ranking.Ranking;
import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;

/**
 * <p>
 * Title: PerceptualImportantPoints
 * </p>
 * 
 * <p>
 * Description: Perceptual important points preprocessing routine wich can be
 * used for data reduction purposes. The timeseries to be processed is
 * subsequently reduced to the size of a given pipCount. The criterion of
 * reduction is based on the preservation of perceived points (min/max values).
 * 
 * The implementation of the algorithm is in accordance to the publication
 * 
 * Chung, F.L., Fu, T.C., Luk, R., Ng, V., Flexible Time Series Pattern Matching
 * Based on Perceptually Important Points. In: Workshop on Learning from
 * Temporal and Spatial Data at IJCAI (2001) 1-7
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.08
 */
public class PerceptuallyImportantPoints extends TimeSeriesProcessor<ITimeSeriesUnivariate> {

	private int pipCount;

	@SuppressWarnings("unused")
	private PerceptuallyImportantPoints() {
	}

	public PerceptuallyImportantPoints(int pipCount) {
		if (pipCount < 2)
			throw new IllegalArgumentException("PIP: parameter value <2");

		this.pipCount = pipCount;
	}

	public int getPipCount() {
		return pipCount;
	}

	public void setPipCount(int pipCount) {
		if (pipCount < 2)
			throw new IllegalArgumentException("PIP: parameter value <2");

		this.pipCount = pipCount;
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_REDUCTION;
	}

	@Override
	public void process(List<ITimeSeriesUnivariate> data) {
		for (ITimeSeriesUnivariate timeSeries : data)
			process(timeSeries);
	}

	public void process(ITimeSeriesUnivariate data) {
		if (data == null)
			return;
		if (data.size() < pipCount)
			return;

		SortedSet<Long> pipTimeStamps = new TreeSet<>();
		pipTimeStamps.add(data.getFirstTimestamp());
		pipTimeStamps.add(data.getLastTimestamp());

		// identify additional pips until pipCount is reached
		for (int i = 0; i < getPipCount() - 2; i++) {
			pipTimeStamps.add(calculateNextPip(data, pipTimeStamps));
		}

		// remove all data not matching the pipTmp result
		for (int i = 0; i < data.getTimestamps().size(); i++)
			if (!pipTimeStamps.contains(data.getTimestamp(i)))
				data.removeTimeValue(i--);
	}

	/**
	 * identifies the next pip. For that purpose all intervals between existing pips
	 * are investigated.
	 * 
	 * @param data
	 * @param pipTimeStamps
	 * @return
	 */
	public static Long calculateNextPip(ITimeSeriesUnivariate data, SortedSet<Long> pipTimeStamps) {

		if (data.size() <= pipTimeStamps.size())
			throw new IllegalArgumentException(
					"PIP: impossible to calculate another pip if given time series does not contain more time-value-pairs than current pip count");

		Iterator<Long> timeSeriesIterator = data.getTimestamps().iterator();
		timeSeriesIterator.next(); // first time stamp is always included

		Iterator<Long> pipTimeStampIterator = pipTimeStamps.iterator();
		Long lastPiPTimeStamp = pipTimeStampIterator.next(); // first existing pip

		Long nextPip = -1L;
		double pipYOffsetCurrent = Double.NEGATIVE_INFINITY;

		while (pipTimeStampIterator.hasNext()) {

			// address a subSequence between two existing pips (pipTimeStampsSorted)
			Long nextPiPTimeStamp = pipTimeStampIterator.next();

			// calculate reference gradient and xAxisIntercept
			double gradient = (data.getValue(nextPiPTimeStamp, false) - data.getValue(lastPiPTimeStamp, false))
					/ (nextPiPTimeStamp - lastPiPTimeStamp);
			double xAxisIntercept = data.getValue(nextPiPTimeStamp, false) - (nextPiPTimeStamp * gradient);

			// identify the most applicable point within the particular interval
			while (timeSeriesIterator.hasNext()) {
				Long timeStamp = timeSeriesIterator.next();
				if (timeStamp >= nextPiPTimeStamp)
					break;
				else {
					double dist = Math.abs(gradient * timeStamp + xAxisIntercept - data.getValue(timeStamp, false));

					if (dist > pipYOffsetCurrent) {
						pipYOffsetCurrent = dist;
						nextPip = timeStamp;
					}
				}
			}

			lastPiPTimeStamp = nextPiPTimeStamp;
		}

		return nextPip;
	}

	/**
	 * calculates the interestingness value of every remaining time stamp to be the
	 * next pip.
	 * 
	 * @param data
	 * @param pipTimeStamps
	 * @param rankCount
	 *            parameter that limits the length of the ranking. Can be used to
	 *            cope with scalability issues.
	 * 
	 * @return ranking of timestamps
	 */
	public static Ranking<EntryWithComparableKey<Double, Long>> calculateNextPipCandidates(ITimeSeriesUnivariate data,
			SortedSet<Long> pipTimeStamps, int rankCount) {

		if (data.size() <= pipTimeStamps.size())
			throw new IllegalArgumentException(
					"PIP: impossible to calculate another pip if given time series does not contain more time-value-pairs than current pip count");

		Ranking<EntryWithComparableKey<Double, Long>> ranking = new Ranking<>();

		Iterator<Long> timeSeriesIterator = data.getTimestamps().iterator();
		timeSeriesIterator.next(); // first time stamp is always included

		Iterator<Long> pipTimeStampIterator = pipTimeStamps.iterator();
		Long lastPiPTimeStamp = pipTimeStampIterator.next(); // first existing pip

		while (pipTimeStampIterator.hasNext()) {

			// address a subSequence between two existing pips (pipTimeStampsSorted)
			Long nextPiPTimeStamp = pipTimeStampIterator.next();

			// calculate reference gradient and xAxisIntercept
			double gradient = (data.getValue(nextPiPTimeStamp, false) - data.getValue(lastPiPTimeStamp, false))
					/ (nextPiPTimeStamp - lastPiPTimeStamp);
			double xAxisIntercept = data.getValue(nextPiPTimeStamp, false) - (nextPiPTimeStamp * gradient);

			// identify the most applicable point within the particular interval
			while (timeSeriesIterator.hasNext()) {
				Long timeStamp = timeSeriesIterator.next();
				if (timeStamp >= nextPiPTimeStamp)
					break;
				else {
					double dist = Math.abs(gradient * timeStamp + xAxisIntercept - data.getValue(timeStamp, false));

					// avoid insert/delete for weak candidates
					if (ranking.size() == rankCount && ranking.getFirst().getKey() > dist)
						continue;

					ranking.add(new EntryWithComparableKey<Double, Long>(dist, timeStamp));

					// stick to the maximum length defined with rankCount
					if (ranking.size() > rankCount)
						ranking.removeFirst();
				}
			}

			lastPiPTimeStamp = nextPiPTimeStamp;
		}

		return ranking;
	}

	/**
	 * calculates the interestingness value of every remaining time stamp to be the
	 * next pip.
	 * 
	 * @param data
	 * @param pipTimeStamps
	 * @return
	 */
	public static Ranking<EntryWithComparableKey<Double, Long>> calculateNextPipCandidates(ITimeSeriesUnivariate data,
			SortedSet<Long> pipTimeStamps) {

		return calculateNextPipCandidates(data, pipTimeStamps, data.size());
	}

	@Override
	public List<IDataProcessor<ITimeSeriesUnivariate>> getAlternativeParameterizations(int count) {
		List<Integer> integers = ParameterSupportTools.getAlternativeIntegers(pipCount, count);

		List<IDataProcessor<ITimeSeriesUnivariate>> processors = new ArrayList<>();
		for (Integer i : integers)
			if (i >= 2)
				processors.add(new PerceptuallyImportantPoints(i));

		return processors;
	}
}
