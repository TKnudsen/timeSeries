package com.github.TKnudsen.timeseries.operations.preprocessing.univariate.normalization;

import java.util.Arrays;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.preprocessing.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.preprocessing.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.ComplexDataObject.model.tools.MathFunctions;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.ITimeSeriesUnivariatePreprocessor;
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
 * Copyright: Copyright (c) 2016-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public class MinMaxNormalization implements ITimeSeriesUnivariatePreprocessor {

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
					timeSeries.replaceValue(i, MathFunctions.linearScale(globalMin, globalMax, timeSeries.getValue(i)));
				else
					timeSeries.replaceValue(i, MathFunctions.linearScale(min, max, timeSeries.getValue(i)));
		}
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

	@Override
	public List<IDataProcessor<ITimeSeriesUnivariate>> getAlternativeParameterizations(int count) {
		return Arrays.asList(new MinMaxNormalization(!globalMinMax));
	}
}