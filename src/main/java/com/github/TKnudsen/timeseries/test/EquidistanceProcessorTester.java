package com.github.TKnudsen.timeseries.test;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.dataGeneration.TimeSeriesGenerator;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.primitives.TimeQuantization;
import com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.EquidistanceProcessor;
import com.github.TKnudsen.timeseries.operations.tools.DateTools;

public class EquidistanceProcessorTester {
	
	public static void main(String[] args) {
		
		ITimeSeriesMultivariate timeSeriesNonEqui = TimeSeriesGenerator.generateSyntheticTimeSeriesMultivariate(DateTools.createDate(2018, 9, 6, 14, 42, 0, 0).getTime(), DateTools.createDate(2018, 9, 7, 14, 42, 0, 0).getTime(), 5,
				new TimeDuration(TimeQuantization.HOURS, 1), false);
		
		ITimeSeriesMultivariate timeSeriesEqui = TimeSeriesGenerator.generateSyntheticTimeSeriesMultivariate(DateTools.createDate(2018, 9, 6, 14, 42, 0, 0).getTime(), DateTools.createDate(2018, 9, 7, 14, 42, 0, 0).getTime(), 5,
				new TimeDuration(TimeQuantization.HOURS, 1), true);
			
		List<ITimeSeriesMultivariate> tSList = new ArrayList<ITimeSeriesMultivariate>();
		tSList.add(timeSeriesNonEqui);
		tSList.add(timeSeriesEqui);
		
		EquidistanceProcessor processor = new EquidistanceProcessor();		
		processor.process(tSList);
	}

}
