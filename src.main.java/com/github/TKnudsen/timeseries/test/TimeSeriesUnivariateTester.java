package com.github.TKnudsen.timeseries.test;

import java.util.Date;

import com.github.TKnudsen.timeseries.data.dataGeneration.SyntheticTimeSeriesGenerator;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.primitives.TimeQuantization;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.tools.DateTools;

public class TimeSeriesUnivariateTester {

	public static void main(String[] args) {
		ITimeSeriesUnivariate timeSeries = SyntheticTimeSeriesGenerator.generateSyntheticTimeSeries(DateTools.createDate(2015, 3, 13, 13, 0, 0, 0).getTime(), DateTools.createDate(2015, 3, 13, 13, 0, 10, 0).getTime(),
				new TimeDuration(TimeQuantization.SECONDS, 1), true);

		testFindDate(timeSeries);
		System.out.println("Test finished.");
	}

	private static boolean testFindDate(ITimeSeriesUnivariate timeSeries) {
		try {
			Date findDate = DateTools.createDate(2015, 3, 13, 13, 0, 5, 0);
			int findByDate = timeSeries.findByDate(findDate.getTime(), true);
			if (findByDate >= 0)
				return true;
		} catch (IllegalArgumentException e) {

		}
		return false;
	}
}
