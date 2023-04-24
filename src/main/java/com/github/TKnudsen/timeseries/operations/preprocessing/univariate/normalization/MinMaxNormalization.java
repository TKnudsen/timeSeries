package com.github.TKnudsen.timeseries.operations.preprocessing.univariate.normalization;

import java.util.Arrays;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.ComplexDataObject.model.tools.MathFunctions;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;
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
 * Copyright: Copyright (c) 2016-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.05
 */
public class MinMaxNormalization extends TimeSeriesProcessor<ITimeSeriesUnivariate> {

	private boolean globalMinMax;

	private double globalMin = Double.NaN;
	private double globalMax = Double.NaN;

	public MinMaxNormalization() {
		this.globalMinMax = false;
	}

	public MinMaxNormalization(boolean globalMinMax) {
		this.globalMinMax = globalMinMax;
	}

	public MinMaxNormalization(double globalMin, double globalMax) {
		this.globalMinMax = true;
		this.globalMin = globalMin;
		this.globalMax = globalMax;
	}

	@Override
	public void process(List<ITimeSeriesUnivariate> data) {

		double globalMin = Double.POSITIVE_INFINITY;
		double globalMax = Double.NEGATIVE_INFINITY;

		if (!Double.isNaN(this.globalMin) && !Double.isNaN(this.globalMax)) {
			globalMin = this.globalMin;
			globalMax = this.globalMax;
		} else if (globalMinMax)
			for (ITimeSeriesUnivariate timeSeries : data) {
				globalMin = Math.min(globalMin, TimeSeriesTools.getMinValue(timeSeries));
				globalMax = Math.max(globalMax, TimeSeriesTools.getMaxValue(timeSeries));
			}

		for (ITimeSeriesUnivariate timeSeries : data) {
			double min = TimeSeriesTools.getMinValue(timeSeries);
			double max = TimeSeriesTools.getMaxValue(timeSeries);
			for (int i = 0; i < timeSeries.size(); i++)
				if (globalMinMax) // also true when this.globalmin/max are set
					timeSeries.replaceValue(i,
							MathFunctions.linearScale(globalMin, globalMax, timeSeries.getValue(i).doubleValue()));
				else
					timeSeries.replaceValue(i,
							MathFunctions.linearScale(min, max, timeSeries.getValue(i).doubleValue()));
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

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof MinMaxNormalization))
			return false;

		MinMaxNormalization other = (MinMaxNormalization) o;

		if (other.globalMinMax != globalMinMax)
			return false;

		if (!Double.isNaN(other.globalMin) && !Double.isNaN(globalMin)) {
			if (other.globalMin != globalMin)
				return false;
		} else if (!Double.isNaN(other.globalMin) && Double.isNaN(globalMin))
			return false;
		else if (Double.isNaN(other.globalMin) && !Double.isNaN(globalMin))
			return false;

		if (!Double.isNaN(other.globalMax) && !Double.isNaN(globalMax)) {
			if (other.globalMax != globalMax)
				return false;
		} else if (!Double.isNaN(other.globalMax) && Double.isNaN(globalMax))
			return false;
		else if (Double.isNaN(other.globalMax) && !Double.isNaN(globalMax))
			return false;

		return true;
	}

}