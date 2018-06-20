package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;

/**
 * <p>
 * Title: Sampling
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
 * @version 1.04
 */
public class SamplingIndexBased extends TimeSeriesMultivariatetimeSeriesProcessor {

	private int timeStampsToBeRemoved;

	@SuppressWarnings("unused")
	private SamplingIndexBased() {
		this(3);
	}

	public SamplingIndexBased(int timeStampsToBeRemoved) {
		this.timeStampsToBeRemoved = timeStampsToBeRemoved;
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
		for (int index = timeSeries.size() - 1; index > 0; index--) {
			if (index % timeStampsToBeRemoved == 0) {
			} else
				timeSeries.removeTimeValue(index);
		}
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_REDUCTION;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesMultivariate>> getAlternativeParameterizations(int count) {
		List<Integer> integers = ParameterSupportTools.getAlternativeIntegers(timeStampsToBeRemoved, count);

		List<IDataProcessor<ITimeSeriesMultivariate>> processors = new ArrayList<>();
		for (Integer i : integers)
			processors.add(new SamplingIndexBased(i));

		return processors;
	}

	public int getTimeStampsToBeRemoved() {
		return timeStampsToBeRemoved;
	}

	public void setTimeStampsToBeRemoved(int timeStampsToBeRemoved) {
		this.timeStampsToBeRemoved = timeStampsToBeRemoved;
	}

}