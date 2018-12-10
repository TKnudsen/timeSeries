package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;
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
 * Copyright: Copyright (c) 2015-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public class MissingValueRemover extends TimeSeriesProcessor<ITimeSeriesMultivariate> {

	private double missingValueIndicator;

	@SuppressWarnings("unused")
	private MissingValueRemover() {
		this.missingValueIndicator = Double.NaN;
	}

	public MissingValueRemover(double missingValueIndicator) {
		this.missingValueIndicator = missingValueIndicator;
	}

	@Override
	public void process(List<ITimeSeriesMultivariate> data) {
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

	private void process(ITimeSeriesMultivariate data) {
		for (int i = 0; i < data.size(); i++) {
			for (Double d : data.getValue(i))
				if (TimeSeriesTools.compareDoubles(missingValueIndicator, d)) {
					data.removeTimeValue(i);
					i--;

					break;
				}
		}
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_CLEANING;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesMultivariate>> getAlternativeParameterizations(int count) {
		List<IDataProcessor<ITimeSeriesMultivariate>> alternatives = new ArrayList<>();

		return alternatives;
	}

	public double getMissingValueIndicator() {
		return missingValueIndicator;
	}

	public void setMissingValueIndicator(double missingValueIndicator) {
		this.missingValueIndicator = missingValueIndicator;
	}
}