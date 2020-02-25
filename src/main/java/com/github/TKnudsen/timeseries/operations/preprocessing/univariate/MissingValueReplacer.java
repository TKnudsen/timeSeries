package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * Replaces a specific (missing) value with a pre-determined default value
 * <p>
 * Copyright: Copyright (c) 2015-2020
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class MissingValueReplacer extends TimeSeriesProcessor<ITimeSeriesUnivariate> {

	private Double missingValueIndicator;
	private double defaultValue;

	@SuppressWarnings("unused")
	private MissingValueReplacer() {
		this.missingValueIndicator = Double.NaN;
	}

	public MissingValueReplacer(Double missingValueIndicator, double defaultValue) {
		this.missingValueIndicator = missingValueIndicator;
		this.defaultValue = defaultValue;
	}

	@Override
	public void process(List<ITimeSeriesUnivariate> data) {
		if (data.isEmpty())
			throw new IllegalStateException("List<TimeSeries> is empty");

		for (int i = 0; i < data.size(); i++) {
			if (data.get(i) == null)
				throw new IllegalStateException("TimeSeries is null");
			if (data.get(i).isEmpty())
				throw new IllegalStateException("TimeSeries is empty");
			process(data.get(i));
		}
	}

	private void process(ITimeSeriesUnivariate data) {
		for (int i = 0; i < data.size(); i++) {
			if (TimeSeriesTools.compareDoubleObjects(missingValueIndicator, data.getValue(i))) {
				data.replaceValue(i, defaultValue);
				i--;
			}
		}
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_CLEANING;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesUnivariate>> getAlternativeParameterizations(int count) {
		return null;
	}
}