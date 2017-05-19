package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * <p>
 * Title: MissingValueRemover
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public class MissingValueRemover implements ITimeSeriesUnivariatePreprocessor {

	private double missingValueIndicator;

	@SuppressWarnings("unused")
	private MissingValueRemover() {
		this.missingValueIndicator = Double.NaN;
	}

	public MissingValueRemover(double missingValueIndicator) {
		this.missingValueIndicator = missingValueIndicator;
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
			if (TimeSeriesTools.compareDoubles(missingValueIndicator, data.getValue(i))) {
				data.removeTimeValue(i);
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