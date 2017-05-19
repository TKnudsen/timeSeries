package com.github.TKnudsen.timeseries.operations.io.json.test;

import java.util.Date;

import com.github.TKnudsen.timeseries.data.ITimeSeries;
import com.github.TKnudsen.timeseries.data.dataGeneration.TimeSeriesGenerator;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.multivariate.TimeSeriesMultivariateLabeled;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.primitives.TimeInterval;
import com.github.TKnudsen.timeseries.data.primitives.TimeQuantization;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.io.json.JSONLoader;
import com.github.TKnudsen.timeseries.operations.io.json.JSONWriter;
import com.github.TKnudsen.timeseries.operations.tools.DateTools;

/**
 * <p>
 * Title: JSONIOTester
 * </p>
 * 
 * <p>
 * Description: tests JSON output-inout.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.0
 */
public class JSONIOTester {

	public static void main(String[] args) {

		// create dummy time series
		Date startDate = DateTools.createDate(2016, 4, 3, 2, 1, 0, 0);
		Date endDate = DateTools.createDate(2016, 4, 3, 3, 1, 0, 0);
		TimeDuration quantization = new TimeDuration(TimeQuantization.MINUTES, 10);
		ITimeSeriesUnivariate timeSeries = TimeSeriesGenerator.generateSyntheticTimeSeriesUnivariate(startDate.getTime(), endDate.getTime(), quantization, true);
		System.out.println(timeSeries);

		// write
		String file = "ts.json";
		String create = JSONWriter.writeToString(timeSeries);
		JSONWriter.writeToFile(timeSeries, file);
		System.out.println(create);

		// load
		ITimeSeriesUnivariate loadConfigsFromString = JSONLoader.loadConfigsFromString(create);
		System.out.println(loadConfigsFromString);
		ITimeSeriesUnivariate loadConfigsFromFile = JSONLoader.loadConfigsFromFile(file);
		System.out.println(loadConfigsFromFile);
		
		
		
	
		
		Date startDateMV = DateTools.createDate(2016, 4, 3, 2, 1, 0, 0);
		Date endDateMV = DateTools.createDate(2016, 4, 3, 3, 1, 0, 0);
		TimeDuration quantizationMV = new TimeDuration(TimeQuantization.MINUTES, 10);
		ITimeSeries timeSeriesMV = TimeSeriesGenerator.generateSyntheticTimeSeriesMultivariate(startDateMV.getTime(), endDateMV.getTime(),10, quantizationMV, true);
		TimeSeriesMultivariateLabeled TSML = new TimeSeriesMultivariateLabeled((ITimeSeriesMultivariate) timeSeriesMV);
		
		TSML.addEventLabel(0, "TestEvent");
		TSML.addTimeIntervalLabel(new TimeInterval(0, 5),"TestTimeInterval");
		
		System.out.println(TSML);
		
		System.out.println(TSML.getAttributeName(0));
		System.out.println(TSML.getEventLabels());
		System.out.println(TSML.getIntervalLabels());
		
		// write
		String fileMV = "ts.json";
		String createMV = JSONWriter.writeToString(TSML);
		JSONWriter.writeToFile(TSML, fileMV);
		System.out.println(createMV);
		
		// load
		TimeSeriesMultivariateLabeled loadConfigsFromStringMV = JSONLoader.loadTSMVLabeledFromString(createMV);
		System.out.println(loadConfigsFromStringMV);
		TimeSeriesMultivariateLabeled loadConfigsFromFileMV = JSONLoader.loadTSMVLabeledFromFile(fileMV);
		System.out.println(loadConfigsFromFileMV);
		
		System.out.println(loadConfigsFromStringMV.getAttributeName(0));
		System.out.println(loadConfigsFromStringMV.getEventLabels());
		System.out.println(loadConfigsFromStringMV.getIntervalLabels());
		
		System.out.println(loadConfigsFromFileMV.getAttributeName(0));
		System.out.println(loadConfigsFromFileMV.getEventLabels());
		System.out.println(loadConfigsFromFileMV.getIntervalLabels());
		
	}
}
