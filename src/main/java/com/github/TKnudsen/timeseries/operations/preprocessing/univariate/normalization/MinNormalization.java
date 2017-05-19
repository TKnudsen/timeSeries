package com.github.TKnudsen.timeseries.operations.preprocessing.univariate.normalization;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.ITimeSeriesUnivariatePreprocessor;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * <p>
 * Title: MinNormalization
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
 * @version 1.03
 */
public class MinNormalization implements ITimeSeriesUnivariatePreprocessor {

	@Override
	public void process(List<ITimeSeriesUnivariate> data) {
		for (ITimeSeriesUnivariate timeSeries : data) {
			double min = TimeSeriesTools.getMinValue(timeSeries);
			for (int i = 0; i < timeSeries.size(); i++)
				timeSeries.replaceValue(i, normalize(min, timeSeries.getValue(i)));
		}
	}

	private double normalize(double min, double value) {
		if (!Double.isNaN(value))
			return value - min;
		else
			return Double.NaN;
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_NORMALIZATION;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesUnivariate>> getAlternativeParameterizations(int count) {
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof MinNormalization))
			return false;
		return true;
	}

}