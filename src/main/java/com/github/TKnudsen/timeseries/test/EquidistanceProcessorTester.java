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

		List<ITimeSeriesMultivariate> tSList1 = generateCustomNonEquiMultivariateTimeSeriesList(0);
		List<ITimeSeriesMultivariate> tSList2 = generateCustomNonEquiMultivariateTimeSeriesList(1);
		List<ITimeSeriesMultivariate> tSList3 = generateRandomNonEquiMultivariateTimeSeriesList();		
		List<ITimeSeriesMultivariate> tSList4 = generateRandomEquiMultivariateTimeSeriesList();
		List<ITimeSeriesMultivariate> tSList5 = generateRandomNonEquiMultivariateTimeSeriesList();
				
		EquidistanceProcessor processor = new EquidistanceProcessor(false);

		System.out.println("Test 1 (custom time series) before processing:");
		printTimeStamps(tSList1);	
		Long firstOld1 = tSList1.get(0).getFirstTimestamp();
		Long lastOld1 = tSList1.get(0).getLastTimestamp();
		processor.process(tSList1);
		Long firstNew1 = tSList1.get(0).getFirstTimestamp();
		Long lastNew1 = tSList1.get(0).getLastTimestamp();
		System.out.println("\n" + "Test 1 (custom time series) after processing:");
		printTimeStamps(tSList1);
		printFirstLastTimeStampComparison(firstOld1, firstNew1, lastOld1, lastNew1);

		System.out.println("\n" + "Test 2 (custom time series)  before processing:");
		printTimeStamps(tSList2);
		Long firstOld2 = tSList2.get(0).getFirstTimestamp();
		Long lastOld2 = tSList2.get(0).getLastTimestamp();
		processor.process(tSList2);
		Long firstNew2 = tSList2.get(0).getFirstTimestamp();
		Long lastNew2 = tSList2.get(0).getLastTimestamp();
		System.out.println("\n" + "Test 2 (custom time series) after processing:");
		printTimeStamps(tSList2);
		printFirstLastTimeStampComparison(firstOld2, firstNew2, lastOld2, lastNew2);

		System.out.println("\n" + "Test 3 (random time series) before processing:");
		printTimeStamps(tSList3);
		Long firstOld3 = tSList3.get(0).getFirstTimestamp();
		Long lastOld3 = tSList3.get(0).getLastTimestamp();
		processor.process(tSList3);
		Long firstNew3 = tSList3.get(0).getFirstTimestamp();
		Long lastNew3 = tSList3.get(0).getLastTimestamp();
		System.out.println("\n" + "Test 3 (random time series) after processing:");
		printTimeStamps(tSList3);
		printFirstLastTimeStampComparison(firstOld3, firstNew3, lastOld3, lastNew3);
		
		System.out.println("\n" + "Test 4 (equidistant time series) before processing:");
		printTimeStamps(tSList4);
		Long firstOld4 = tSList4.get(0).getFirstTimestamp();
		Long lastOld4 = tSList4.get(0).getLastTimestamp();
		processor.process(tSList4);
		Long firstNew4 = tSList4.get(0).getFirstTimestamp();
		Long lastNew4 = tSList4.get(0).getLastTimestamp();
		System.out.println("\n" + "Test 4 (equidistant time series) after processing:");
		printTimeStamps(tSList4);
		printFirstLastTimeStampComparison(firstOld4, firstNew4, lastOld4, lastNew4);
		
		EquidistanceProcessor processor2 = new EquidistanceProcessor(true);
		
		System.out.println("\n" + "Test 5 (random time series with extrapolation of temp borders) before processing:");
		printTimeStamps(tSList5);
		Long firstOld5 = tSList5.get(0).getFirstTimestamp();
		Long lastOld5 = tSList5.get(0).getLastTimestamp();
		processor2.process(tSList5);
		Long firstNew5 = tSList5.get(0).getFirstTimestamp();
		Long lastNew5 = tSList5.get(0).getLastTimestamp();
		System.out.println("\n" + "Test 5 (random time series with extrapolation of temp borders) after processing:");
		printTimeStamps(tSList5);
		printFirstLastTimeStampComparison(firstOld5, firstNew5, lastOld5, lastNew5);
	}
	
	private static void printFirstLastTimeStampComparison(Long firstOld, Long firstNew, Long lastOld, Long lastNew) {
		 
		String firstDiffStr = "";
		Long firstDiff = firstNew - firstOld;
		if(firstDiff > 0) {
			firstDiffStr = ", difference: " + (firstDiff)  + " (inside old borders)";
		} else if(firstDiff < 0) {
			firstDiffStr = ", difference: " + (firstDiff * -1)  + " (outside old borders)";
		} else {
			firstDiffStr = ", difference: " + firstDiff;
		}
		System.out.println("first timestamp before processing: " + firstOld);
		System.out.println("first timestamp after processing: " + firstNew + firstDiffStr);
		
		String lastDiffStr = "";
		Long lastDiff = lastNew - lastOld;
		if(lastDiff > 0) {
			lastDiffStr = ", difference: " + (lastDiff)  + " (outside old borders)";
		} else if(lastDiff < 0) {
			lastDiffStr = ", difference: " + (lastDiff * -1)  + " (inside old borders)";
		} else {
			lastDiffStr = ", difference: " + lastDiff;
		}		
		System.out.println("last timestamp before processing: " + lastOld);
		System.out.println("last timestamp after processing: " + lastNew + lastDiffStr);
	}

	private static List<ITimeSeriesMultivariate> generateCustomNonEquiMultivariateTimeSeriesList(int number) {
		
		List<ITimeSeriesUnivariate> timeSeriesUnivariateList = new ArrayList<>();
		List<String> timeSeriesNames = new ArrayList<>();					
		for (int i = 0; i < 5; i++) {
			List<Double> values = new ArrayList<>();
			List<Long> timeStamps = genTimeStamps(number);
			double lastValue = 0.5;			
			for (int j = 0; j < timeStamps.size(); j++) {
				values.add(lastValue);
				lastValue = lastValue + Math.random() * 0.2 - 0.1;
			}
			timeSeriesUnivariateList.add(new TimeSeriesUnivariate(timeStamps, values, Double.NaN));
			timeSeriesNames.add("" + i);
		}
		ITimeSeriesMultivariate timeSeriesMulti = new TimeSeriesMultivariate(timeSeriesUnivariateList, timeSeriesNames);
		List<ITimeSeriesMultivariate> tSList = new ArrayList<ITimeSeriesMultivariate>();
		tSList.add(timeSeriesMulti);
		return tSList;
	}
	
	private static List<Long> genTimeStamps(int number) {
		List<Long> timeStamps = new ArrayList<>();
		if(number == 0) {
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
			timeStamps.add(new Long(1000004000)); // 1000
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
		} else if(number == 1) {
			timeStamps.add(new Long(1001));
			timeStamps.add(new Long(1005));
			timeStamps.add(new Long(1007));
			timeStamps.add(new Long(1010));
			timeStamps.add(new Long(1013));
			timeStamps.add(new Long(1015));
			timeStamps.add(new Long(1016));
			timeStamps.add(new Long(1030));
			timeStamps.add(new Long(1034));
			timeStamps.add(new Long(1040));
			timeStamps.add(new Long(1041));
			timeStamps.add(new Long(1045));			
		}
		return timeStamps;
	}

	private static List<ITimeSeriesMultivariate> generateRandomNonEquiMultivariateTimeSeriesList() {
		List<ITimeSeriesMultivariate> tSList = new ArrayList<ITimeSeriesMultivariate>();
		tSList.add(TimeSeriesGenerator.generateSyntheticTimeSeriesMultivariate(
				DateTools.createDate(2015, 3, 13, 13, 0, 0, 0).getTime(),
				DateTools.createDate(2015, 3, 13, 13, 0, 30, 0).getTime(), 3,
				new TimeDuration(TimeQuantization.SECONDS, 1), false));
		return tSList;
	}

	private static List<ITimeSeriesMultivariate> generateRandomEquiMultivariateTimeSeriesList() {
		List<ITimeSeriesMultivariate> tSList = new ArrayList<ITimeSeriesMultivariate>();
		tSList.add(TimeSeriesGenerator.generateSyntheticTimeSeriesMultivariate(
				DateTools.createDate(2015, 3, 13, 13, 0, 0, 0).getTime(),
				DateTools.createDate(2015, 3, 13, 13, 0, 30, 0).getTime(), 3,
				new TimeDuration(TimeQuantization.SECONDS, 1), true));
		return tSList;
	}

	private static void printTimeStamps(List<ITimeSeriesMultivariate> tSList) {
		for (ITimeSeriesMultivariate timeSeries : tSList) {
			for (int i = 0; i < timeSeries.getTimestamps().size() - 1; i++) {
				for (int j = 0; j < timeSeries.getDimensionality(); j++) {
					ITimeSeriesUnivariate tSUni = timeSeries.getTimeSeries(j);
					System.out.print(String.format("%.2f", tSUni.getValue(i)) + ", ");
				}
				System.out.println(" i: " + i + ", tS: " + timeSeries.getTimestamp(i) + ", quantization: "
						+ (timeSeries.getTimestamp(i + 1) - timeSeries.getTimestamp(i)));
			}

			int lastIndex = (timeSeries.getTimestamps().size() - 1);
			for (int j = 0; j < timeSeries.getDimensionality(); j++) {
				ITimeSeriesUnivariate tSUni = timeSeries.getTimeSeries(j);
				System.out.print(String.format("%.2f", tSUni.getValue(lastIndex)) + ", ");
			}
			System.out.println(" i: " + lastIndex + ", tS: " + timeSeries.getTimestamp(lastIndex));
		}
	}

}
