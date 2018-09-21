package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

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
		
		List<Long> quantizationList = TimeQuantizationTools.getQuantizationList(timeSeries.getTimestamps());
		List<Long> quantizationGuesses = TimeQuantizationTools.guessQuantization(quantizationList);
		int offsetIndex = TimeQuantizationTools.guessTemporalOffset(quantizationGuesses, quantizationList);		
		long quantization = TimeQuantizationTools.getQuantizationFromTimeStampIndex(offsetIndex, quantizationList);
				
		for(int i = 0; i < timeSeries.getTimestamps().size(); i++) {						
			System.out.println("index: " + i + ", timeStamp: " + timeSeries.getTimestamp(i));				
		}
		System.out.println("offsetIndex: " + offsetIndex);
		System.out.println("quantization: " + quantization);
				
		//long offsetTimeStamp = timeSeries.getTimestamp(offsetIndex);
		
		// TODO	process
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
