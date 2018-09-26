package com.github.TKnudsen.timeseries.test;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.dataGeneration.TimeSeriesGenerator;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.multivariate.TimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.primitives.TimeQuantization;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.EquidistanceProcessor;
import com.github.TKnudsen.timeseries.operations.tools.DateTools;

public class EquidistanceProcessorTester {
	
	public static void main(String[] args) {
				
		List<ITimeSeriesMultivariate> tSList1 = generateCustomNonEquiMultivariateTimeSeriesList();
		List<ITimeSeriesMultivariate> tSList2 = generateRandomNonEquiMultivariateTimeSeriesList();
		List<ITimeSeriesMultivariate> tSList3 = generateRandomEquiMultivariateTimeSeriesList();
		
		EquidistanceProcessor processor = new EquidistanceProcessor();	
		
		System.out.println("Test 1 - Before:" + "\n");
		printTimeStamps(tSList1);
		
		processor.process(tSList1);	
		
		System.out.println("\n" + "Test 1 - After:");
		printTimeStamps(tSList1);
		
		System.out.println("\n" + "Test 2 - Before:");
		printTimeStamps(tSList2);
		
		processor.process(tSList2);	
		
		System.out.println("\n" + "Test 2 - After:");
		printTimeStamps(tSList2);
		
		System.out.println("\n" + "Test 3 - Before:");
		printTimeStamps(tSList3);
		
		processor.process(tSList3);	
		
		System.out.println("\n" + "Test 3 - After:");
		printTimeStamps(tSList3);
	}
		
	private static List<ITimeSeriesMultivariate> generateCustomNonEquiMultivariateTimeSeriesList() {		
		List<Long> timeStamps = new ArrayList<>();
		List<ITimeSeriesUnivariate> timeSeriesUnivariateList = new ArrayList<>();
		List<String> timeSeriesNames = new ArrayList<>();		
		timeStamps.add(new Long(1000000000)); // 5
		timeStamps.add(new Long(1000000005)); // 5
		timeStamps.add(new Long(1000000010)); // 15
		timeStamps.add(new Long(1000000025)); // 25
		timeStamps.add(new Long(1000000050)); // 25
		timeStamps.add(new Long(1000000075)); // 925
		timeStamps.add(new Long(1000001000)); // 1000
		timeStamps.add(new Long(1000002000)); // 25
		timeStamps.add(new Long(1000002025)); // 25
		timeStamps.add(new Long(1000002050)); // 25
		timeStamps.add(new Long(1000002075)); // 1925
		timeStamps.add(new Long(1000004000)); // 1000 <---offset should be here
		timeStamps.add(new Long(1000005000)); // 1000 
		timeStamps.add(new Long(1000006000)); // 1000
		timeStamps.add(new Long(1000007000)); // 1000
		timeStamps.add(new Long(1000008000)); // 1000
		timeStamps.add(new Long(1000009000)); // 1000
		timeStamps.add(new Long(1000010000)); // 25
		timeStamps.add(new Long(1000010025)); // 25
		timeStamps.add(new Long(1000010050)); // 50
		timeStamps.add(new Long(1000010100)); // 100
		timeStamps.add(new Long(1000010200)); // 100
		timeStamps.add(new Long(1000010300)); // 100
		timeStamps.add(new Long(1000010400)); // 100
		timeStamps.add(new Long(1000010500)); // 100
		timeStamps.add(new Long(1000010600)); // 10
		timeStamps.add(new Long(1000010610)); // 5
		timeStamps.add(new Long(1000010615)); // 5
		timeStamps.add(new Long(1000010620)); // 5
		timeStamps.add(new Long(1000010625)); // 5
		timeStamps.add(new Long(1000010630)); // 5
		timeStamps.add(new Long(1000010635));		
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
		return tSList;
	}
	
	private static List<ITimeSeriesMultivariate> generateRandomNonEquiMultivariateTimeSeriesList() {				
		List<ITimeSeriesMultivariate> tSList = new ArrayList<ITimeSeriesMultivariate>();
		tSList.add(TimeSeriesGenerator.generateSyntheticTimeSeriesMultivariate(DateTools.createDate(2015, 3, 13, 13, 0, 0, 0).getTime(), DateTools.createDate(2015, 3, 13, 13, 0, 30, 0).getTime(), 3,
				new TimeDuration(TimeQuantization.SECONDS, 1), false));			
		return tSList;		
	}
	
	private static List<ITimeSeriesMultivariate> generateRandomEquiMultivariateTimeSeriesList() {				
		List<ITimeSeriesMultivariate> tSList = new ArrayList<ITimeSeriesMultivariate>();
		tSList.add(TimeSeriesGenerator.generateSyntheticTimeSeriesMultivariate(DateTools.createDate(2015, 3, 13, 13, 0, 0, 0).getTime(), DateTools.createDate(2015, 3, 13, 13, 0, 30, 0).getTime(), 3,
				new TimeDuration(TimeQuantization.SECONDS, 1), true));			
		return tSList;		
	}
	
	private static void printTimeStamps(List<ITimeSeriesMultivariate> tSList) {
		for(ITimeSeriesMultivariate timeSeries : tSList) {
			for(int i = 0; i < timeSeries.getTimestamps().size() - 1; i++) {						
				System.out.println("index: " + i + ", timeStamp: " + timeSeries.getTimestamp(i) + ", distance: " + (timeSeries.getTimestamp(i+1)-timeSeries.getTimestamp(i)));				
			}
			int lastIndex = (timeSeries.getTimestamps().size()-1);
			System.out.println("index: " + lastIndex + ", timeStamp: " + timeSeries.getTimestamp(lastIndex));	
		}
	}

}