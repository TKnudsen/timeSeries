package com.github.TKnudsen.timeseries.test;

import java.util.Arrays;
import java.util.Date;

import com.github.TKnudsen.timeseries.data.dataGeneration.TimeSeriesGenerator;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.primitives.TimeQuantization;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.TemporalQuantization;
import com.github.TKnudsen.timeseries.operations.tools.DateTools;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * <p>
 * Title: TimeQuantizationTester
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
 * @version 1.02
 */
public class TimeQuantizationTester {

	public static void main(String[] args) {

		Date startDate = DateTools.createDate(2016, 4, 3, 2, 1, 0, 0);
		Date endDate = DateTools.createDate(2016, 4, 19, 3, 1, 0, 0);
		TimeDuration quantization = new TimeDuration(TimeQuantization.MINUTES, 301);

		ITimeSeriesUnivariate ts = TimeSeriesGenerator.generateSyntheticTimeSeriesUnivariate(startDate.getTime(), endDate.getTime(), quantization, true);
		ITimeSeriesUnivariate cloneTimeSeries = TimeSeriesTools.cloneTimeSeries(ts);

		TemporalQuantization temporalQuantization = new TemporalQuantization(new TimeDuration(TimeQuantization.DAYS, 1));
		temporalQuantization.process(Arrays.asList(cloneTimeSeries));
		System.out.println(cloneTimeSeries);
	}
}