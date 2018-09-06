package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.Date;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;
import com.github.TKnudsen.timeseries.operations.tools.TimeQuantizationTools;

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

	private void process(ITimeSeriesMultivariate timeSeries) {
		long quantization = TimeQuantizationTools.guessQuantization(timeSeries);
		long temporalOffset = guessTemporalOffset(quantization, timeSeries);
				
		for(int i = 0; i < timeSeries.getTimestamps().size(); i++) {

			//TODO
			System.out.println(new Date(timeSeries.getTimestamp(i)));
		}
		
		System.out.println("Offset: " + new Date(temporalOffset));
	}
	
	private long guessTemporalOffset(long quantization, ITimeSeriesMultivariate timeSeries) {
		
		long offset = timeSeries.getFirstTimestamp();	
			
		if(timeSeries.getTimestamps().size() > 0) {		
			
			long prevTimeStamp = timeSeries.getFirstTimestamp();
			long prevQuantization = Math.abs(timeSeries.getTimestamp(1) - prevTimeStamp);
			
			int max = 0;
			int counter = 0;
			
			for(int i = 1; i < timeSeries.getTimestamps().size(); i++) {
				long nextTimeStamp = timeSeries.getTimestamp(i);
				long currentQuantization = Math.abs(nextTimeStamp - prevTimeStamp);
				if(prevQuantization == currentQuantization) {
					counter++;
				} else {
					if(prevQuantization == quantization && counter > max) {					
						max = counter;							
						offset = timeSeries.getTimestamp(i - max);						
					}	
					counter = 0;					
				}								
				prevTimeStamp = nextTimeStamp;			
			}
		}
		
		return offset;		
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesMultivariate>> getAlternativeParameterizations(int count) {
		// TODO Auto-generated method stub
		return null;
	}

}
