package com.github.TKnudsen.timeseries.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.github.TKnudsen.timeseries.data.dataGeneration.TimeSeriesGenerator;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.primitives.TimeQuantization;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.ITimeSeriesPreprocessorUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.normalization.AmplitudeScaling;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.normalization.MinMaxNormalization;
import com.github.TKnudsen.timeseries.operations.tools.DateTools;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

public class NormalizationTester {
	public static void main(String[] args) {

		Date startDate = DateTools.createDate(2016, 4, 3, 2, 1, 0, 0);
		Date endDate = DateTools.createDate(2016, 4, 3, 3, 1, 0, 0);
		TimeDuration quantization = new TimeDuration(TimeQuantization.MINUTES, 2);

		ITimeSeriesUnivariate ts1 = TimeSeriesGenerator.generateSyntheticTimeSeriesUnivariate(startDate.getTime(), endDate.getTime(), quantization, true);
		ITimeSeriesUnivariate ts2 = TimeSeriesGenerator.generateSyntheticTimeSeriesUnivariate(startDate.getTime(), endDate.getTime(), quantization, true);

		ITimeSeriesUnivariate ts1clone = TimeSeriesTools.cloneTimeSeries(ts1);
		ITimeSeriesUnivariate ts2clone = TimeSeriesTools.cloneTimeSeries(ts2);

		ITimeSeriesPreprocessorUnivariate normalization = new AmplitudeScaling(true);
		normalization.process(new ArrayList<ITimeSeriesUnivariate>(Arrays.asList(ts1clone, ts2clone)));
		System.out.println(ts1clone);
		System.out.println(ts2clone);
	}
}
