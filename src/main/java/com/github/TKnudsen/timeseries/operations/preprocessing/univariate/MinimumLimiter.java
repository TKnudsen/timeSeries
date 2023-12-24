package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;

/**
 * <p>
 * Title: MinimumLimiter
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2023
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class MinimumLimiter extends TimeSeriesProcessor<ITimeSeriesUnivariate> {

	private Number minimum;

	public MinimumLimiter(Number minimum) {
		this.minimum = minimum;
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

		if (minimum == null || Double.isNaN(minimum.doubleValue()))
			throw new IllegalArgumentException("MinimumLimiter: no limit defined");

		for (long l : timeSeries.getTimestamps())
			if (timeSeries.getValue(l, false) < minimum.doubleValue())
				timeSeries.insert(l, minimum.doubleValue());
	}

	public Number getMinimum() {
		return minimum;
	}

	public void setMinimum(Number minimum) {
		this.minimum = minimum;
	}
}
