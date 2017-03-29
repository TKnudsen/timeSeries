package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

public class EquidistanceProvider implements ITimeSeriesUnivariatePreprocessor{

	private TimeDuration equidistance;
	
	//TODO enum: interpolation (linear), last existing time stamp
	
	
	
	@Override
	public void process(List<ITimeSeriesUnivariate> data) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_CLEANING;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesUnivariate>> getAlternativeParameterizations(int count) {
		return null;
	}
	
}
