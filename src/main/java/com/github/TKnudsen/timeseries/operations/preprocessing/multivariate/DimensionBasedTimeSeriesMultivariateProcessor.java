package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.ITimeSeriesUnivariatePreprocessor;

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

	public DimensionBasedTimeSeriesMultivariateProcessor() {
		initializeUnivariateTimeSeriesProcessor();
	}

	protected abstract void initializeUnivariateTimeSeriesProcessor();

	@Override
	public void process(List<ITimeSeriesMultivariate> data) {
		if (univariateTimeSeriesProcessor == null)
			initializeUnivariateTimeSeriesProcessor();

		if (data == null)
			return;

		for (ITimeSeriesMultivariate timeSeriesMultivariate : data) {
			if (timeSeriesMultivariate == null)
				continue;

			for (ITimeSeriesUnivariate timeSeriesUnivariate : timeSeriesMultivariate.getTimeSeriesList())
				univariateTimeSeriesProcessor.process(new ArrayList<>(Arrays.asList(timeSeriesUnivariate)));
		}
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
