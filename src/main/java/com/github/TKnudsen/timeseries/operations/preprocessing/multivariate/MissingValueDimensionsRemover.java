package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * 
 * TimeSeries
 *
 * Copyright: (c) 2015-2018 Juergen Bernard,
 * https://github.com/TKnudsen/TimeSeries<br>
 * 
 * MissingValueDimensionsRemover is a pre-processing routine that removes
 * dimensions with more % missing values as defined by a threshold
 *
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class MissingValueDimensionsRemover extends TimeSeriesProcessor<ITimeSeriesMultivariate> {

	private Double missingValueIndicator;

	/**
	 * rate of maximum missing values before a dimension is removed from
	 * multivariate time series.
	 */
	private double missingValueRate;

	@SuppressWarnings("unused")
	private MissingValueDimensionsRemover() {
		this(Double.NaN, 0.75);
	}

	public MissingValueDimensionsRemover(Double missingValueIndicator, double missingValueRate) {
		this.missingValueIndicator = missingValueIndicator;
		this.missingValueRate = missingValueRate;
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

		for (int d = data.getDimensionality() - 1; d >= 0; d--) {
			int count = 0;
			for (Double value : data.getTimeSeries(d).getValues()) {
				if (value != null && TimeSeriesTools.compareDoubleObjects(value, missingValueIndicator)) {
					count++;
				}
			}

			double rate = count / (double) data.getTimeSeries(d).size();
			if (rate >= missingValueRate) {
				data.removeTimeSeries(d);
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

		List<Double> alternativeDoubles = ParameterSupportTools.getAlternativeDoubles(missingValueRate, count);

		for (Double v : alternativeDoubles) {
			if (v >= 0 && v <= 1)
				alternatives.add(new MissingValueDimensionsRemover(missingValueIndicator, v));
			if (alternatives.size() == count)
				return alternatives;
		}

		return alternatives;
	}

	public double getMissingValueRate() {
		return missingValueRate;
	}

	public void setMissingValueRate(double missingValueRate) {
		this.missingValueRate = missingValueRate;
	}

	public double getMissingValueIndicator() {
		return missingValueIndicator;
	}

	public void setMissingValueIndicator(double missingValueIndicator) {
		this.missingValueIndicator = missingValueIndicator;
	}
}