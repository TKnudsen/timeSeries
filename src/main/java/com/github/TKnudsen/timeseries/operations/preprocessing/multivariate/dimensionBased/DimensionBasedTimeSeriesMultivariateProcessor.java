package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.dimensionBased;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesMultivariateTools;

/**
 * <p>
 * Title: DimensionBasedTimeSeriesMultivariateProcessor
 * </p>
 * 
 * <p>
 * Description: Helper class for all MultivariateTimeSeries Processors that
 * process the dimensions of the MVTS (the TimeSeriesUnivariate) individually.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public abstract class DimensionBasedTimeSeriesMultivariateProcessor
		extends TimeSeriesProcessor<ITimeSeriesMultivariate> {

	private TimeSeriesProcessor<ITimeSeriesUnivariate> univariateTimeSeriesProcessor;

	protected abstract void initializeUnivariateTimeSeriesProcessor();

	@Override
	public void process(List<ITimeSeriesMultivariate> data) {
		if (univariateTimeSeriesProcessor == null)
			initializeUnivariateTimeSeriesProcessor();

		if (data == null)
			return;

		List<List<ITimeSeriesUnivariate>> univariateTimeSeriesLists = TimeSeriesMultivariateTools
				.getUnivariateTimeSeriesLists(data);

		for (List<ITimeSeriesUnivariate> timeSeriesUnivariateList : univariateTimeSeriesLists)
			univariateTimeSeriesProcessor.process(timeSeriesUnivariateList);
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		if (univariateTimeSeriesProcessor == null)
			initializeUnivariateTimeSeriesProcessor();

		return univariateTimeSeriesProcessor.getPreprocessingCategory();
	}

	protected TimeSeriesProcessor<ITimeSeriesUnivariate> getTimeSeriesProcessor() {
		return univariateTimeSeriesProcessor;
	}

	protected void setTimeSeriesProcessor(TimeSeriesProcessor<ITimeSeriesUnivariate> univariateTimeSeriesProcessor) {
		this.univariateTimeSeriesProcessor = univariateTimeSeriesProcessor;
	}

}
