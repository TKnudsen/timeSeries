package com.github.TKnudsen.timeseries.operations.preprocessing.univariate.normalization;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.preprocessing.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.ITimeSeriesPreprocessorUnivariate;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * <p>
 * Title: OffsetTranslation
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class OffsetTranslation implements ITimeSeriesPreprocessorUnivariate {

	private boolean globalMeans;

	public OffsetTranslation() {
		this.globalMeans = false;
	}

	public OffsetTranslation(boolean globalMeans) {
		this.globalMeans = globalMeans;
	}

	@Override
	public void process(List<ITimeSeriesUnivariate> data) {

		double globalMean = 0;
		if (globalMeans) {
			for (ITimeSeriesUnivariate timeSeries : data)
				globalMean += TimeSeriesTools.getMean(timeSeries);
			globalMean /= (double) data.size();
		}

		for (ITimeSeriesUnivariate timeSeries : data) {
			double means = TimeSeriesTools.getMean(timeSeries);
			for (int i = 0; i < timeSeries.size(); i++)
				if (globalMeans)
					timeSeries.replaceValue(i, normalize(globalMean, timeSeries.getValue(i)));
				else
					timeSeries.replaceValue(i, normalize(means, timeSeries.getValue(i)));
		}
	}

	private double normalize(double means, double value) {
		return value - means;
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_NORMALIZATION;
	}

	public boolean isGlobalMeans() {
		return globalMeans;
	}

	public void setGlobalMeans(boolean globalMeans) {
		this.globalMeans = globalMeans;
	}
}