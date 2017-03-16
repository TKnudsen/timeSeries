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
 * Title: AmplitudeScaling
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.04
 */
public class AmplitudeScaling implements ITimeSeriesUnivariatePreprocessor {

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