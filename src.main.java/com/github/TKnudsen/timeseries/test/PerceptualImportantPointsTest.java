package com.github.TKnudsen.timeseries.test;

import java.util.Arrays;
import java.util.Date;

import com.github.TKnudsen.timeseries.data.dataGeneration.TimeSeriesGenerator;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.primitives.TimeQuantization;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.PerceptuallyImportantPoints;
import com.github.TKnudsen.timeseries.operations.tools.DateTools;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

public class PerceptualImportantPointsTest {

	public static void main(String[] args) {

		Date startDate = DateTools.createDate(2016, 4, 3, 2, 1, 0, 0);
		Date endDate = DateTools.createDate(2016, 4, 3, 3, 1, 0, 0);
		TimeDuration quantization = new TimeDuration(TimeQuantization.MINUTES, 2);

		ITimeSeriesUnivariate ts = TimeSeriesGenerator.generateSyntheticTimeSeriesUnivariate(startDate.getTime(), endDate.getTime(), quantization, true);
		ITimeSeriesUnivariate cloneTimeSeries = TimeSeriesTools.cloneTimeSeries(ts);

		PerceptuallyImportantPoints pip = new PerceptuallyImportantPoints(10);
		pip.process(Arrays.asList(cloneTimeSeries));
		System.out.println(cloneTimeSeries);
	}
}
