package com.github.TKnudsen.timeseries.operations.degreeOfInterest;

import java.util.Map;

import com.github.TKnudsen.ComplexDataObject.data.entry.EntryWithComparableKey;
import com.github.TKnudsen.ComplexDataObject.data.ranking.Ranking;
import com.github.TKnudsen.ComplexDataObject.model.transformations.normalization.LinearNormalizationFunction;
import com.github.TKnudsen.ComplexDataObject.model.transformations.normalization.NormalizationFunction;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

/**
 * 
 * timeSeries
 *
 * Copyright: (c) 2017-2018 Juergen Bernard,
 * https://github.com/TKnudsen/timeSeries<br>
 * <br>
 * 
 * A function that computes an "interestingness" for a collection of values.<br>
 * <br>
 * The default value domain is [0...1] as this allows an easy combination of
 * several (weighted) interestingness functions.<br>
 * 
 * @version 1.03
 */

public abstract class TimeSeriesDegreeOfInterestFunction implements IDegreeOfInterestFunction<ITimeSeriesUnivariate> {

	public Map<ITimeSeriesUnivariate, Double> apply(ITimeSeriesMultivariate timeSeriesMultivariate) {
		return apply(timeSeriesMultivariate.getTimeSeriesList());
	}

	protected void normalize(Map<ITimeSeriesUnivariate, Double> interestingnessScores) {
		NormalizationFunction normalizationFunction = new LinearNormalizationFunction(interestingnessScores.values());
		for (ITimeSeriesUnivariate timeSeries : interestingnessScores.keySet())
			interestingnessScores.put(timeSeries,
					normalizationFunction.apply(interestingnessScores.get(timeSeries)).doubleValue());
	}

	public Ranking<EntryWithComparableKey<Double, ITimeSeriesUnivariate>> getRanking(
			Map<ITimeSeriesUnivariate, Double> interestingnessScores) {

		Ranking<EntryWithComparableKey<Double, ITimeSeriesUnivariate>> ranking = new Ranking<>();

		for (ITimeSeriesUnivariate timeSeries : interestingnessScores.keySet())
			ranking.add(new EntryWithComparableKey<Double, ITimeSeriesUnivariate>(interestingnessScores.get(timeSeries),
					timeSeries));

		return ranking;
	}
}