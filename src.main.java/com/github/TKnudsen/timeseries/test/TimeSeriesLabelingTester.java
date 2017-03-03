package com.github.TKnudsen.timeseries.test;

import java.util.Date;
import java.util.SortedMap;

import com.github.TKnudsen.timeseries.data.ITemporalLabeling;
import com.github.TKnudsen.timeseries.data.dataGeneration.TimeSeriesGenerator;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.primitives.TimeInterval;
import com.github.TKnudsen.timeseries.data.primitives.TimeQuantization;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariateLabelable;
import com.github.TKnudsen.timeseries.operations.tools.DateTools;

import TimeSeriesLabelingTools.TimeSeriesLabelingTools;

public class TimeSeriesLabelingTester {

	public static void main(String[] args) {
		Date startDate = DateTools.createDate(2016, 4, 2, 0, 0, 0, 0);
		Date endDate = DateTools.createDate(2016, 4, 13, 0, 0, 0, 0);
		TimeDuration quantization = new TimeDuration(TimeQuantization.MINUTES, 30);

		ITimeSeriesUnivariate ts = TimeSeriesGenerator.generateSyntheticTimeSeriesUnivariate(startDate.getTime(), endDate.getTime(), quantization, true);

		ITemporalLabeling<String> timeSeries = new TimeSeriesUnivariateLabelable(ts);
		timeSeries.addEventLabel(DateTools.createDate(2016, 4, 5, 0, 0, 0, 0).getTime(), "2");
		timeSeries.addTimeDurationLabel(DateTools.createDate(2016, 4, 4, 0, 0, 0, 0).getTime(), new TimeDuration(TimeQuantization.DAYS, 3), "1");
		timeSeries.addTimeIntervalLabel(new TimeInterval(DateTools.createDate(2016, 4, 9, 0, 0, 0, 0).getTime(), DateTools.createDate(2016, 4, 11, 0, 0, 0, 0).getTime()), "3");

		SortedMap<Long, String> labelChangeEvents = TimeSeriesLabelingTools.getLabelChangeEvents(timeSeries);
		for (Long l : labelChangeEvents.keySet())
			System.out.println(new Date(l) + ", " + labelChangeEvents.get(l));
	}

}
