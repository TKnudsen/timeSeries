package com.github.TKnudsen.timeseries.operations.tools;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariateFactory;
import com.github.TKnudsen.timeseries.data.univariate.TimeValuePairUnivariate;

public class TimeSeriesToolsTest {

	public static void main(String[] args) {

		List<TimeValuePairUnivariate> list = new ArrayList<TimeValuePairUnivariate>();

		int i = 0;
		double d = 6;
		list.add(new TimeValuePairUnivariate(i++, Double.NaN));
		list.add(new TimeValuePairUnivariate(i++, Double.NaN));
		list.add(new TimeValuePairUnivariate(i++, d++));
		i++;
		i++;
		i++;
		i++;
		list.add(new TimeValuePairUnivariate(i++, d++));
		list.add(new TimeValuePairUnivariate(i++, d++));
		list.add(new TimeValuePairUnivariate(i++, d++));
		list.add(new TimeValuePairUnivariate(i++, d++));
		list.add(new TimeValuePairUnivariate(i++, Double.NaN));
		list.add(new TimeValuePairUnivariate(i++, Double.NaN));
		list.add(new TimeValuePairUnivariate(i++, Double.NaN));

		ITimeSeriesUnivariate ts = TimeSeriesUnivariateFactory.newTimeSeries(list, Double.NaN);

		double mean = TimeSeriesTools.getMean(ts);
		System.out.println(mean);
	}

}
