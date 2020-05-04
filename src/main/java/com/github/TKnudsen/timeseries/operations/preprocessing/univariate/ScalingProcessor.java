package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;

/**
 * <p>
 * ScalingProcessor
 * </p>
 * 
 * <p>
 * Scales the value domain of time series using a given factor.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2010-2020
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class ScalingProcessor extends TimeSeriesProcessor<ITimeSeriesUnivariate> {

	private double scalingFactor;

	public ScalingProcessor(double scalingFactor) {
		this.scalingFactor = scalingFactor;
	}

	@Override
	public void process(List<ITimeSeriesUnivariate> data) {

		for (ITimeSeriesUnivariate timeSeries : data) {
			for (int i = 0; i < timeSeries.size(); i++)
				timeSeries.replaceValue(i, timeSeries.getValue(i) * scalingFactor);
		}
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_NORMALIZATION;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesUnivariate>> getAlternativeParameterizations(int count) {
		List<IDataProcessor<ITimeSeriesUnivariate>> alternatives = new ArrayList<>();

		List<Double> alternativeDoubles = ParameterSupportTools.getAlternativeDoubles(scalingFactor, count);

		for (Double value : alternativeDoubles) {
			if (value > 0)
				alternatives.add(new ScalingProcessor(value));
			if (alternatives.size() == count)
				return alternatives;
		}

		return alternatives;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof ScalingProcessor))
			return false;

		ScalingProcessor other = (ScalingProcessor) o;

		return other.scalingFactor == scalingFactor;
	}

	public double getScalingFactor() {
		return scalingFactor;
	}

	public void setScalingFactor(double scalingFactor) {
		this.scalingFactor = scalingFactor;
	}

}