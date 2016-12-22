package com.github.TKnudsen.timeseries.operations.preprocessing.univariate.normalization;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.preprocessing.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.ITimeSeriesPreprocessorUnivariate;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * <p>
 * Title: MinMaxNormalization
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
 * @version 1.0
 */
public class MinMaxNormalization implements ITimeSeriesPreprocessorUnivariate {

	private boolean globalMinMax;

	public MinMaxNormalization() {
		this.globalMinMax = false;
	}

	public MinMaxNormalization(boolean globalMinMax) {
		this.globalMinMax = globalMinMax;
	}

	@Override
	public void process(List<ITimeSeriesUnivariate> data) {

		double globalMin = Double.POSITIVE_INFINITY;
		double globalMax = Double.NEGATIVE_INFINITY;
		if (globalMinMax) {
			for (ITimeSeriesUnivariate timeSeries : data) {
				globalMin = Math.min(globalMin, TimeSeriesTools.getMinValue(timeSeries));
				globalMax = Math.max(globalMax, TimeSeriesTools.getMaxValue(timeSeries));
			}
		}

		for (ITimeSeriesUnivariate timeSeries : data) {
			double min = TimeSeriesTools.getMinValue(timeSeries);
			double max = TimeSeriesTools.getMaxValue(timeSeries);
			for (int i = 0; i < timeSeries.size(); i++)
				if (globalMinMax)
					timeSeries.replaceValue(i, normalize(globalMin, globalMax, timeSeries.getValue(i)));
				else
					timeSeries.replaceValue(i, normalize(min, max, timeSeries.getValue(i)));
		}
	}

	private double normalize(double min, double max, double value) {
		if (!Double.isNaN(value))
			if (max != min)
				return (value - min) / (max - min);
			else if (max != 0)
				return (value - min) / (max);
			else
				return 1.0;
		else
			return Double.NaN;
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_NORMALIZATION;
	}

	public boolean isGlobalMinMax() {
		return globalMinMax;
	}

	public void setGlobalMinMax(boolean globalMinMax) {
		this.globalMinMax = globalMinMax;
	}
}