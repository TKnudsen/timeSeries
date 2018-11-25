package com.github.TKnudsen.timeseries.operations.preprocessing.univariate.normalization;

import java.util.Arrays;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * <p>
 * Title: AmplitudeScaling
 * </p>
 * 
 * *
 * <p>
 * Description: Normalization routine that subtracts the time series's mean
 * value; afterwards the time series is divided by the (original) std.
 * 
 * The routine is one of the classical normalization approaches used in many of
 * Eamonn Keogh's works, including his tutorial "Introduction to Time Series
 * Mining".
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2010-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.07
 */
public class AmplitudeScaling extends TimeSeriesProcessor<ITimeSeriesUnivariate> {

	private boolean globalMeans;

	public AmplitudeScaling() {
		this.globalMeans = false;
	}

	public AmplitudeScaling(boolean globalMeans) {
		this.globalMeans = globalMeans;
	}

	@Override
	public void process(List<ITimeSeriesUnivariate> data) {

		double globalMean = 0;
		double globalStd = 0;
		if (globalMeans) {
			for (ITimeSeriesUnivariate timeSeries : data) {
				globalMean += TimeSeriesTools.getMean(timeSeries);
				globalStd += TimeSeriesTools.getStdDeviation(timeSeries);
			}
			globalMean /= (double) data.size();
			globalStd /= (double) data.size();
		}

		for (ITimeSeriesUnivariate timeSeries : data) {
			double means = TimeSeriesTools.getMean(timeSeries);
			double std = TimeSeriesTools.getStdDeviation(timeSeries);
			for (int i = 0; i < timeSeries.size(); i++)
				if (globalMeans)
					timeSeries.replaceValue(i, normalize(globalMean, globalStd, timeSeries.getValue(i)));
				else
					timeSeries.replaceValue(i, normalize(means, std, timeSeries.getValue(i)));
		}

	}

	private double normalize(double means, double std, double value) {
		double v = value - means;
		if (Double.isNaN(std))
			return 0.0;
		else if (std != 0)
			v /= std;
		return v;
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
		return Arrays.asList(new AmplitudeScaling(!globalMeans));
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof AmplitudeScaling))
			return false;

		AmplitudeScaling other = (AmplitudeScaling) o;

		return other.globalMeans == globalMeans;
	}

}