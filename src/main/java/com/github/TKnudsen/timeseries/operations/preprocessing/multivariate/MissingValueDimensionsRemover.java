package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.multivariate.TimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
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
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class MissingValueDimensionsRemover extends TimeSeriesProcessor<ITimeSeriesMultivariate> {

	private double missingValueIndicator;

	/**
	 * rate of maximum missing values before a dimension is removed from
	 * multivariate time series.
	 */
	private double missingValueRate;

	@SuppressWarnings("unused")
	private MissingValueDimensionsRemover() {
		this(Double.NaN, 0.75);
	}

	public MissingValueDimensionsRemover(double missingValueIndicator, double missingValueRate) {
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
			data.set(i, process(data.get(i)));
		}			
	}

	private ITimeSeriesMultivariate process(ITimeSeriesMultivariate data) {
		List<ITimeSeriesUnivariate> keep = new ArrayList<>();
		for (int d = 0; d < data.getDimensionality(); d++) {
			ITimeSeriesUnivariate timeSeries = TimeSeriesTools.cloneTimeSeries(data.getTimeSeries(d));

			int count = 0;
			for (Double value : timeSeries.getValues()) {
				if (value != null && TimeSeriesTools.compareDoubles(value, missingValueIndicator)) {
					count++;					
				}
			}
					
			double rate = count / (double) timeSeries.size();
			if (rate < missingValueRate) {				
				keep.add(timeSeries);				
			} else {
				System.out.println("Dimension removed: " + timeSeries.getName());
			}
		}		
		ITimeSeriesMultivariate tsm = new TimeSeriesMultivariate(keep);
		tsm.setName(data.getName());
		return tsm;	
		
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