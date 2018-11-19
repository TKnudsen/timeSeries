package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;

/**
 * <p>
 * Title: SamplingIndexBased
 * </p>
 * 
 * <p>
 * Description: applies sampling based on a given number of indices. Thus, if
 * the time series is non-equidistant the durations to be removed may differ.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public class SamplingIndexBased extends TimeSeriesProcessor<ITimeSeriesUnivariate> {

	private int timeStampsToBeRemoved;

	@SuppressWarnings("unused")
	private SamplingIndexBased() {
		this(3);
	}

	public SamplingIndexBased(int timeStampsToBeRemoved) {
		this.timeStampsToBeRemoved = timeStampsToBeRemoved;
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

		for (int index = timeSeries.size() - 1; index > 0; index--) {
			if (index % timeStampsToBeRemoved == 0) {
			} else
				timeSeries.removeTimeValue(index);
		}
	}

	@Override
	public List<IDataProcessor<ITimeSeriesUnivariate>> getAlternativeParameterizations(int count) {
		List<IDataProcessor<ITimeSeriesUnivariate>> processors = new ArrayList<>();

		List<Integer> alternativeLongs = ParameterSupportTools.getAlternativeIntegers(timeStampsToBeRemoved, count);

		for (Integer integer : alternativeLongs)
			if (integer > 0)
				processors.add(new SamplingIndexBased(integer));

		return processors;
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_REDUCTION;
	}

	public int getTimeStampsToBeRemoved() {
		return timeStampsToBeRemoved;
	}

	public void setTimeStampsToBeRemoved(int timeStampsToBeRemoved) {
		this.timeStampsToBeRemoved = timeStampsToBeRemoved;
	}
}
