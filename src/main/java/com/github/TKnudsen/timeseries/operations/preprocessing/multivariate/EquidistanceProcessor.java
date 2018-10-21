package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;
import com.github.TKnudsen.timeseries.operations.tools.TimeQuantizationTools;
import com.github.TKnudsen.timeseries.operations.tools.TimeQuantizationTools.TimeStampQuantizationTuple;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

public class EquidistanceProcessor extends TimeSeriesProcessor<ITimeSeriesMultivariate> {

	public EquidistanceProcessor() {

	}

	@Override
	public void process(List<ITimeSeriesMultivariate> timeSeriesList) {
		if (timeSeriesList == null)
			return;
		if (timeSeriesList.size() == 0)
			return;
		for (int i = 0; i < timeSeriesList.size(); i++) {
			if (timeSeriesList.get(i) == null)
				continue;
			if (timeSeriesList.get(i).isEmpty())
				continue;
			process(timeSeriesList.get(i));
		}
	}

	/**
	 * 
	 * @param timeSeries
	 */
	private void process(ITimeSeriesMultivariate timeSeries) {

		List<Long> timeStamps = timeSeries.getTimestamps();
		List<Long> quantizationList = TimeQuantizationTools.getQuantizationList(timeStamps);
		List<Long> quantizationGuesses = TimeQuantizationTools.guessQuantization(quantizationList);
		TimeStampQuantizationTuple<Long, Long> timeStampQuantizationTuple = TimeQuantizationTools
				.guessStartTimeStamp(quantizationGuesses, timeStamps);
		Long startTimeStamp = timeStampQuantizationTuple.timeStamp;
		Long quantization = timeStampQuantizationTuple.quantization;
		
		System.out.println("startTimeStamp: " + startTimeStamp);
		System.out.println("quantization: " + quantization);
		
		applyEquidistance(timeSeries, startTimeStamp, quantization);
	}
	
	/**
	 * 
	 * @param timeSeries
	 * @param startTimeStamp
	 * @param quantization
	 */
	private void applyEquidistance(ITimeSeriesMultivariate timeSeries, Long startTimeStamp, Long quantization) {
		
		long timeStamp = startTimeStamp;
		int i = 1;
		
		while (timeStamp < timeSeries.getLastTimestamp()) {
			
			timeStamp = startTimeStamp + i * quantization;
			long prevTimeStamp = timeStamp - quantization;
		
			insertInterpolatedEntry(timeSeries, timeStamp);
			removeEntriesBetween(timeSeries, prevTimeStamp, timeStamp);
					
			i++;
		}
		
		timeStamp = startTimeStamp;
		i = 1;
		
		while (timeStamp > timeSeries.getFirstTimestamp()) {
			
			timeStamp = startTimeStamp - i * quantization;
			long nextTimeStamp = timeStamp + quantization;
			
			insertInterpolatedEntry(timeSeries, timeStamp);
			removeEntriesBetween(timeSeries, timeStamp, nextTimeStamp);
			
			i++;
		}			
	}

	/**
	 * 
	 * @param timeSeries
	 * @param timeStamp
	 */
	private void insertInterpolatedEntry(ITimeSeriesMultivariate timeSeries, Long timeStamp) {

		List<Long> timeStamps = timeSeries.getTimestamps();		
		if (!timeStamps.contains(timeStamp)) {
			List<Double> interpolatedValues = new ArrayList<Double>();
			List<ITimeSeriesUnivariate> timeSeriesUnivariateList = timeSeries.getTimeSeriesList();
			
			List<Long> nearestNeighbors = TimeSeriesTools.getNearestNeighbors(timeSeriesUnivariateList.get(0), timeStamp);			
			
			for (ITimeSeriesUnivariate timeSeriesUnivariate : timeSeriesUnivariateList) {
				
				if (!nearestNeighbors.isEmpty()) {
					interpolatedValues.add(TimeSeriesTools.getInterpolatedValue(timeSeriesUnivariate,
							nearestNeighbors.get(0), nearestNeighbors.get(nearestNeighbors.size() - 1), timeStamp));
				}
			}
			if (!interpolatedValues.isEmpty()) {
				timeSeries.insert(timeStamp, interpolatedValues);				
			}
		}
	}
	
	/**
	 * 
	 * @param timeSeries
	 * @param timeStamp1
	 * @param timeStamp2
	 */
	private void removeEntriesBetween(ITimeSeriesMultivariate timeSeries, Long timeStamp1, Long timeStamp2) {
						
		List<Long> timeStamps = new ArrayList<Long>();
		for(Long timeStamp : timeSeries.getTimestamps()) {
			timeStamps.add(timeStamp);
		}
		for(Long timeStamp : timeStamps) {
			if(timeStamp1 < timeStamp && timeStamp < timeStamp2 && timeStamp != timeSeries.getLastTimestamp()) {							
				timeSeries.removeTimeValue(timeStamp);				
			}			
		}
	}
	
	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_CLEANING;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesMultivariate>> getAlternativeParameterizations(int count) {
		return null;
	}

}
