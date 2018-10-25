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

	private boolean allowExtrapolatedTemporalBorders;
	
	public EquidistanceProcessor(boolean allowExtrapolatedTemporalBorders) {
		this.allowExtrapolatedTemporalBorders = allowExtrapolatedTemporalBorders;
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
		
			insertEntry(timeSeries, timeStamp);
			removeEntriesBetween(timeSeries, prevTimeStamp, timeStamp);
					
			i++;
		}
		
		timeStamp = startTimeStamp;
		i = 1;
		
		while (timeStamp > timeSeries.getFirstTimestamp()) {
			
			timeStamp = startTimeStamp - i * quantization;
			long nextTimeStamp = timeStamp + quantization;
			
			insertEntry(timeSeries, timeStamp);
			removeEntriesBetween(timeSeries, timeStamp, nextTimeStamp);
			
			i++;
		}			
	}

	/**
	 * 
	 * @param timeSeries
	 * @param timeStamp
	 */
	private void insertEntry(ITimeSeriesMultivariate timeSeries, Long timeStamp) {

		List<Long> timeStamps = timeSeries.getTimestamps();
		
		if (!timeStamps.contains(timeStamp)) {			
			
			if(allowExtrapolatedTemporalBorders) {
				if(timeStamp > timeSeries.getLastTimestamp()) {
					insertNewBorderEntry(timeSeries, timeStamp, timeSeries.getLastTimestamp());
				} else if(timeStamp < timeSeries.getFirstTimestamp()) {
					insertNewBorderEntry(timeSeries, timeStamp, timeSeries.getFirstTimestamp());
				} else {
					insertInterpolatedEntry(timeSeries, timeStamp);	
				}				
			} else {
				insertInterpolatedEntry(timeSeries, timeStamp);			
			}
		}
	}
	
	private void insertNewBorderEntry(ITimeSeriesMultivariate timeSeries, Long timeStamp, Long timeStampWithValues) {
				
		List<ITimeSeriesUnivariate> timeSeriesUnivariateList = timeSeries.getTimeSeriesList();
		
		List<Double> values = new ArrayList<Double>();
		
		for (ITimeSeriesUnivariate timeSeriesUnivariate : timeSeriesUnivariateList) {		
			values.add(timeSeriesUnivariate.getValue(timeStampWithValues, false));
		}	
		
		if (values.size() == timeSeriesUnivariateList.size()) {
			timeSeries.insert(timeStamp, values);
		}
			
	}
	
	private void insertInterpolatedEntry(ITimeSeriesMultivariate timeSeries, Long timeStamp) {
				
		List<ITimeSeriesUnivariate> timeSeriesUnivariateList = timeSeries.getTimeSeriesList();
		
		List<Long> nearestTimeStampNeighbors = TimeSeriesTools.getNearestTimeStampNeighbors(timeSeriesUnivariateList.get(0), timeStamp);	
		
		List<Double> interpolatedValues = new ArrayList<Double>();
		
		if (nearestTimeStampNeighbors.size() == 2) {
			
			Long neighborPrev = nearestTimeStampNeighbors.get(0);
			Long neighborNext = nearestTimeStampNeighbors.get(nearestTimeStampNeighbors.size() - 1);		
				
			for (ITimeSeriesUnivariate timeSeriesUnivariate : timeSeriesUnivariateList) {					
				double value = TimeSeriesTools.getInterpolatedValue(timeSeriesUnivariate, neighborPrev, neighborNext, timeStamp);
				interpolatedValues.add(value);					
			}
		}
		
		if (interpolatedValues.size() == timeSeriesUnivariateList.size()) {
			timeSeries.insert(timeStamp, interpolatedValues);				
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
			if(timeStamp1 < timeStamp && timeStamp < timeStamp2) {							
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
