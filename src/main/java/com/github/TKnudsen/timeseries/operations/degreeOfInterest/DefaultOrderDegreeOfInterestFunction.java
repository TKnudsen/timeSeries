package com.github.TKnudsen.timeseries.operations.degreeOfInterest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

/**
 * 
 * timeSeries<br>
 * https://github.com/TKnudsen/timeSeries<br>
 * Copyright: Copyright (c) 2017-2018, <br>
 * <br>
 * 
 * The index of any {@link ITimeSeriesUnivariate} in the List is used for the
 * creation of interestingness scores. The value domain is [0...1] as this
 * allows an easy combination of weighted interestingness functions.<br>
 * 
 * @version 1.01
 */
public class DefaultOrderDegreeOfInterestFunction extends TimeSeriesDegreeOfInterestFunction {

	@Override
	public Map<ITimeSeriesUnivariate, Double> apply(List<? extends ITimeSeriesUnivariate> timeSeriesList) {

		Map<ITimeSeriesUnivariate, Double> interestingnessScores = new LinkedHashMap<>();

		int interestingness = timeSeriesList.size();
		for (ITimeSeriesUnivariate timeSeries : timeSeriesList)
			interestingnessScores.put(timeSeries, new Double(interestingness--));

		normalize(interestingnessScores);

		return interestingnessScores;
	}

	@Override
	public String getName() {
		return "DefaultOrderDOI";
	}

	@Override
	public String getDescription() {
		return "Interestingness of time series based on their order in the given list.";
	}

}
