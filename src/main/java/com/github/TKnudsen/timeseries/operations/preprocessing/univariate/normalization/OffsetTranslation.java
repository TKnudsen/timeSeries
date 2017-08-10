package com.github.TKnudsen.timeseries.operations.preprocessing.univariate.normalization;

import java.util.Arrays;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.ITimeSeriesUnivariatePreprocessor;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * <p>
 * Title: OffsetTranslation
 * </p>
 * 
 * *
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
 * @version 1.03
 */
public class OffsetTranslation implements ITimeSeriesUnivariatePreprocessor {

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

	@Override
	public List<IDataProcessor<ITimeSeriesUnivariate>> getAlternativeParameterizations(int count) {
		return Arrays.asList(new OffsetTranslation(!globalMeans));
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof OffsetTranslation))
			return false;

		OffsetTranslation other = (OffsetTranslation) o;

		return other.globalMeans == globalMeans;
	}

}