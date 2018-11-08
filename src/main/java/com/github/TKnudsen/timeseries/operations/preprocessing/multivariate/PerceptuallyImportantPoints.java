package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.github.TKnudsen.ComplexDataObject.data.entry.EntryWithComparableKey;
import com.github.TKnudsen.ComplexDataObject.data.ranking.Ranking;
import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;

/**
 * 
 * Routine that considers extreme points in a time series most relevant.
 * According to the principle of human perception. Can be used for data
 * reduction purposes. The timeseries is subsequently reduced to the size of a
 * given pipCount.
 * 
 * </p>
 * In accordance to Chung, F.L., Fu, T.C., Luk, R., Ng, V., Flexible Time Series
 * Pattern Matching Based on Perceptually Important Points. In: Workshop on
 * Learning from Temporal and Spatial Data at IJCAI (2001) 1-7
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public class PerceptuallyImportantPoints extends TimeSeriesProcessor<ITimeSeriesMultivariate> {

	private int pipCount;

	public PerceptuallyImportantPoints(int pipCount) {
		if (pipCount < 2)
			throw new IllegalArgumentException("PIP: parameter value <2");

		this.pipCount = pipCount;
	}

	public void setPipCount(int pipCount) {
		if (pipCount < 2)
			throw new IllegalArgumentException("PIP: parameter value <2");

		this.pipCount = pipCount;
	}

	@Override
	public void process(List<ITimeSeriesMultivariate> data) {
		for (ITimeSeriesMultivariate timeSeries : data)
			process(timeSeries);
	}

	public void process(ITimeSeriesMultivariate data) {
		// retrieve the recommendations for pips from the univariate time series and
		// perform a voting among the dimensions' interestingness values.
		// Criterion for voting: Borda count.

		if (data == null)
			return;
		if (data.size() < pipCount)
			return;

		SortedSet<Long> pipTimeStamps = new TreeSet<>();
		pipTimeStamps.add(data.getFirstTimestamp());
		pipTimeStamps.add(data.getLastTimestamp());

		// identify additional pips until pipCount is reached
		for (int index = 0; index < getPipCount() - 2; index++) {

			Map<Long, Integer> electionCounts = new HashMap<>();

			for (int dim = 0; dim < data.getDimensionality(); dim++) {
				Ranking<EntryWithComparableKey<Double, Long>> dimensionRanking = com.github.TKnudsen.timeseries.operations.preprocessing.univariate.PerceptuallyImportantPoints
						.calculateNextPipCandidates(data.getTimeSeries(dim), pipTimeStamps);

				// assign points to every time stamp according to the given dimension-based
				// ranking

				int i = 0;
				for (EntryWithComparableKey<Double, Long> pipCandidate : dimensionRanking) {
					if (electionCounts.get(pipCandidate.getValue()) == null)
						electionCounts.put(pipCandidate.getValue(), 0);

					electionCounts.put(pipCandidate.getValue(), electionCounts.get(pipCandidate.getValue()) + i++);
				}
			}

			// identify winner
			int currentMax = 0;
			Long winningTimeStamp = -1L;
			for (Long pipTimeStampCandidate : electionCounts.keySet())
				if (electionCounts.get(pipTimeStampCandidate) > currentMax) {
					winningTimeStamp = pipTimeStampCandidate;
					currentMax = electionCounts.get(pipTimeStampCandidate);
				}

			pipTimeStamps.add(winningTimeStamp);
		}

		// remove all data not matching the pipTmp result
		for (int i = 0; i < data.getTimestamps().size(); i++)
			if (!pipTimeStamps.contains(data.getTimestamp(i)))
				data.removeTimeValue(i--);
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_REDUCTION;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesMultivariate>> getAlternativeParameterizations(int count) {
		List<Integer> integers = ParameterSupportTools.getAlternativeIntegers(pipCount, count);

		List<IDataProcessor<ITimeSeriesMultivariate>> processors = new ArrayList<>();
		for (Integer i : integers)
			if (i >= 2)
				processors.add(new PerceptuallyImportantPoints(i));

		return processors;
	}

	public int getPipCount() {
		return pipCount;
	}

}
