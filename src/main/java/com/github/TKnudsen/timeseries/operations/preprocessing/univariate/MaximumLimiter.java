package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;

/**
 * <p>
 * Title: MaximumLimiter
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2023
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class MaximumLimiter extends TimeSeriesProcessor<ITimeSeriesUnivariate> {

	private Number maximum;

	public MaximumLimiter(Number maximum) {
		this.maximum = maximum;
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_CLEANING;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesUnivariate>> getAlternativeParameterizations(int count) {
		List<IDataProcessor<ITimeSeriesUnivariate>> processors = new ArrayList<>();

		return processors;
	}

	@Override
	public void process(List<ITimeSeriesUnivariate> data) {
		if (data == null)
			return;

		for (ITimeSeriesUnivariate timeSeries : data)
			process(timeSeries);
	}

	private void process(ITimeSeriesUnivariate timeSeries) {
		if (timeSeries == null || timeSeries.size() == 0)
			return;

		if (maximum == null || Double.isNaN(maximum.doubleValue()))
			throw new IllegalArgumentException("MaximumLimiter: no limit defined");

		for (long l : timeSeries.getTimestamps())
			if (timeSeries.getValue(l, false) > maximum.doubleValue())
				timeSeries.insert(l, maximum.doubleValue());
	}

	public Number getMaximum() {
		return maximum;
	}

	public void setMaximum(Number maximum) {
		this.maximum = maximum;
	}
}
