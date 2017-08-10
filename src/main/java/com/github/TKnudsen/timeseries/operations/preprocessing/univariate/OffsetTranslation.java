package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * <p>
 * Title: OffsetTranslation
 * </p>
 * 
 * <p>
 * Description: Normalization routine that subtracts the time series's mean
 * value.
 * 
 * The routine is one of the classical normalization approaches used in many of
 * Eamonn Keogh's works, including his tutorial "Introduction to Time Series
 * Mining".
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2010-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class OffsetTranslation implements ITimeSeriesUnivariatePreprocessor {

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_NORMALIZATION;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesUnivariate>> getAlternativeParameterizations(int count) {
		return new ArrayList<>();
	}

	@Override
	public void process(List<ITimeSeriesUnivariate> data) {
		if (data == null)
			return;

		for (ITimeSeriesUnivariate timeSeries : data)
			process(timeSeries);
	}

	protected void process(ITimeSeriesUnivariate timeSeries) {
		if (timeSeries == null)
			return;

		double mean = TimeSeriesTools.getMean(timeSeries);

		for (int i = 0; i < timeSeries.size(); i++)
			timeSeries.replaceValue(i, timeSeries.getValue(i) - mean);
	}
}
