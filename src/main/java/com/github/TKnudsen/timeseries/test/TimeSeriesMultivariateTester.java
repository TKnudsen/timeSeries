package com.github.TKnudsen.timeseries.test;

import java.util.Date;

import com.github.TKnudsen.timeseries.data.dataGeneration.TimeSeriesGenerator;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.primitives.TimeQuantization;
import com.github.TKnudsen.timeseries.operations.tools.DateTools;

/**
 * <p>
 * Title: TimeSeriesMultivariateTester
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class TimeSeriesMultivariateTester {

	public static void main(String[] args) {
		ITimeSeriesMultivariate timeSeries = TimeSeriesGenerator.generateSyntheticTimeSeriesMultivariate(DateTools.createDate(2015, 3, 13, 13, 0, 0, 0).getTime(), DateTools.createDate(2015, 3, 13, 13, 0, 10, 0).getTime(), 3,
				new TimeDuration(TimeQuantization.SECONDS, 1), true);

		testFindDate(timeSeries);
		System.out.println("Test finished.");
	}

	private static boolean testFindDate(ITimeSeriesMultivariate timeSeries) {
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