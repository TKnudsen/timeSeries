package com.github.TKnudsen.timeseries.test;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.multivariate.TimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.EquidistanceProcessor;

public class EquidistanceProcessorTester {
	
	public static void main(String[] args) {
		
		List<Long> timeStamps = new ArrayList<>();
		List<ITimeSeriesUnivariate> timeSeriesUnivariateList = new ArrayList<>();
		List<String> timeSeriesNames = new ArrayList<>();
		
		timeStamps.add(new Long(1000000000)); 
		timeStamps.add(new Long(1000000005)); // 5
		timeStamps.add(new Long(1000000010)); // 5
		timeStamps.add(new Long(1000000025)); // 15
		timeStamps.add(new Long(1000000050)); // 25
		timeStamps.add(new Long(1000000075)); // 25
		timeStamps.add(new Long(1000001000)); // 925
		timeStamps.add(new Long(1000002000)); // 1000
		timeStamps.add(new Long(1000002025)); // 25
		timeStamps.add(new Long(1000002050)); // 25
		timeStamps.add(new Long(1000002075)); // 25
		timeStamps.add(new Long(1000004000)); // 1925
		timeStamps.add(new Long(1000005000)); // 1000 <---Offset
		timeStamps.add(new Long(1000006000)); // 1000
		timeStamps.add(new Long(1000007000)); // 1000
		timeStamps.add(new Long(1000008000)); // 1000
		timeStamps.add(new Long(1000009000)); // 1000
		timeStamps.add(new Long(1000010000)); // 1000
		timeStamps.add(new Long(1000010025)); // 25
		timeStamps.add(new Long(1000010050)); // 25
		timeStamps.add(new Long(1000010100)); // 50
		timeStamps.add(new Long(1000010200)); // 100
		timeStamps.add(new Long(1000010300)); // 100
		timeStamps.add(new Long(1000010400)); // 100
		timeStamps.add(new Long(1000010500)); // 100
		timeStamps.add(new Long(1000010600)); // 100
		timeStamps.add(new Long(1000010610)); // 10
		timeStamps.add(new Long(1000010615)); // 5
		timeStamps.add(new Long(1000010620)); // 5
		timeStamps.add(new Long(1000010625)); // 5
		timeStamps.add(new Long(1000010630)); // 5
		timeStamps.add(new Long(1000010635)); // 5
		
		for (int i = 0; i < 5; i++) {
			List<Double> values = new ArrayList<>();
			double lastValue = 0.5;
			for (int j = 0; j < timeStamps.size(); j++) {
				values.add(lastValue);
				lastValue = lastValue + Math.random() * 0.2 - 0.1;
			}
			timeSeriesUnivariateList.add(new TimeSeriesUnivariate(timeStamps, values, Double.NaN));
			timeSeriesNames.add("" + i);
		}

		ITimeSeriesMultivariate timeSeriesMulti =  new TimeSeriesMultivariate(timeSeriesUnivariateList, timeSeriesNames);
			
		List<ITimeSeriesMultivariate> tSList = new ArrayList<ITimeSeriesMultivariate>();
		tSList.add(timeSeriesMulti);
		
		EquidistanceProcessor processor = new EquidistanceProcessor();		
		processor.process(tSList);
	}

}
