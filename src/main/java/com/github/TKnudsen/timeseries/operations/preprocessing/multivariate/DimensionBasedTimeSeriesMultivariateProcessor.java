package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.ITimeSeriesUnivariatePreprocessor;
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
public abstract class DimensionBasedTimeSeriesMultivariateProcessor implements ITimeSeriesMultivariatePreprocessor {

	private ITimeSeriesUnivariatePreprocessor univariateTimeSeriesProcessor;

	protected abstract void initializeUnivariateTimeSeriesProcessor();

	@Override
	public void process(List<ITimeSeriesMultivariate> data) {
		if (univariateTimeSeriesProcessor == null)
			initializeUnivariateTimeSeriesProcessor();

		if (data == null)
			return;

		List<List<ITimeSeriesUnivariate>> univariateTimeSeriesLists = TimeSeriesMultivariateTools.getUnivariateTimeSeriesLists(data);

		for (List<ITimeSeriesUnivariate> timeSeriesUnivariateList : univariateTimeSeriesLists)
			univariateTimeSeriesProcessor.process(timeSeriesUnivariateList);
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return univariateTimeSeriesProcessor.getPreprocessingCategory();
	}

	protected ITimeSeriesUnivariatePreprocessor getUnivariateTimeSeriesProcessor() {
		return univariateTimeSeriesProcessor;
	}

	protected void setUnivariateTimeSeriesProcessor(ITimeSeriesUnivariatePreprocessor univariateTimeSeriesProcessor) {
		this.univariateTimeSeriesProcessor = univariateTimeSeriesProcessor;
	}
}
