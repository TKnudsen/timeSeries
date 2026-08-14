package com.github.TKnudsen.timeseries.test;

import java.util.Date;
import java.util.SortedMap;

import com.github.TKnudsen.ComplexDataObject.data.time.TimeDuration;
import com.github.TKnudsen.ComplexDataObject.data.time.TimeInterval;
import com.github.TKnudsen.ComplexDataObject.data.time.TimeQuantization;
import com.github.TKnudsen.ComplexDataObject.model.tools.DateTools;
import com.github.TKnudsen.timeseries.data.ITemporalLabeling;
import com.github.TKnudsen.timeseries.data.dataGeneration.TimeSeriesGenerator;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariateLabeled;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesLabelingTools;

public class TimeSeriesLabelingTester {

	public static void main(String[] args) {
		Date startDate = DateTools.createDate(2016, 4, 2);
		Date endDate = DateTools.createDate(2016, 4, 13);
		TimeDuration quantization = new TimeDuration(TimeQuantization.MINUTES, 30);

		ITimeSeriesUnivariate ts = TimeSeriesGenerator.generateSyntheticTimeSeriesUnivariate(startDate.getTime(), endDate.getTime(), quantization, true);

		ITemporalLabeling<String> timeSeries = new TimeSeriesUnivariateLabeled(ts);
		timeSeries.addEventLabel(DateTools.createDate(2016, 4, 5).getTime(), "2");
		timeSeries.addTimeDurationLabel(DateTools.createDate(2016, 4, 4).getTime(), new TimeDuration(TimeQuantization.DAYS, 3), "1");
		timeSeries.addTimeIntervalLabel(new TimeInterval(DateTools.createDate(2016, 4, 9).getTime(), DateTools.createDate(2016, 4, 11).getTime()), "3");

		SortedMap<Long, String> labelChangeEvents = TimeSeriesLabelingTools.getLabelChangeEvents(timeSeries);
		for (Long l : labelChangeEvents.keySet())
			System.out.println(new Date(l) + ", " + labelChangeEvents.get(l));
	}

}
