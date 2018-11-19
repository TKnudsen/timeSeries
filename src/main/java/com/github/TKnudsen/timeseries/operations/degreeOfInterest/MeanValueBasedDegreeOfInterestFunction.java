package com.github.TKnudsen.timeseries.operations.degreeOfInterest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.transformations.featureExtraction.ValueMeanFeatureExtractor;

/**
 * 
 * timeSeries<br>
 * https://github.com/TKnudsen/timeSeries<br>
 * Copyright: Copyright (c) 2017-2018, <br>
 * <br>
 * 
 * The mean of the value domaines of individual {@link ITimeSeriesUnivariate}
 * are used for the creation of interestingness scores. The value domain is
 * [0...1] as this allows an easy combination of weighted interestingness
 * functions<br>
 * 
 * @version 1.02
 */
public class MeanValueBasedDegreeOfInterestFunction extends TimeSeriesDegreeOfInterestFunction {

	ValueMeanFeatureExtractor meanValueExtractor = new ValueMeanFeatureExtractor();

	@Override
	public Map<ITimeSeriesUnivariate, Double> apply(List<? extends ITimeSeriesUnivariate> timeSeriesList) {

		Map<ITimeSeriesUnivariate, Double> interestingnessScores = new LinkedHashMap<>();

		for (ITimeSeriesUnivariate timeSeries : timeSeriesList)
			interestingnessScores.put(timeSeries, meanValueExtractor.transform(timeSeries).get(0).getFeatureValue());

		normalize(interestingnessScores);

		return interestingnessScores;
	}

	@Override
	public String getName() {
		return "MeanValueBasedDOI";
	}

	@Override
	public String getDescription() {
		return "Interestingness of time series based on their mean values.";
	}

}
