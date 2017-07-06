package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesStatistics;

/**
 * <p>
 * Title: OutlierRemover
 * </p>
 * 
 * <p>
 * Description: Removes values that higher/lower than a given multiple of the
 * standard deviation in a global sense of the value domain.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */

public class OutlierRemover implements ITimeSeriesUnivariatePreprocessor {

	// standard deviation ratio
	double stdDevRatio;

	@SuppressWarnings("unused")
	private OutlierRemover() {
		this(2.96);
	}

	public OutlierRemover(double stdDevRatio) {
		this.stdDevRatio = stdDevRatio;
	}

	@Override
	public void process(List<ITimeSeriesUnivariate> data) {
		for (ITimeSeriesUnivariate timeSeries : data)
			process(timeSeries);
	}

	private void process(ITimeSeriesUnivariate timeSeries) {

		if (timeSeries == null)
			return;

		TimeSeriesStatistics statistics = new TimeSeriesStatistics(timeSeries);

		double means = statistics.getMean();
		double std = statistics.getStandardDeviation();
		std *= stdDevRatio;

		for (int i = 0; i < timeSeries.size(); i++) {
			if (Math.abs(timeSeries.getValue(i) - means) > std) {
				timeSeries.removeTimeValue(i--);
			}
		}
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_CLEANING;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesUnivariate>> getAlternativeParameterizations(int count) {
		List<IDataProcessor<ITimeSeriesUnivariate>> alternatives = new ArrayList<>();

		List<Double> alternativeDoubles = ParameterSupportTools.getAlternativeDoubles(stdDevRatio, count);

		for (Double std : alternativeDoubles) {
			if (std > 0)
				alternatives.add(new OutlierRemover(std));
			if (alternatives.size() == count)
				return alternatives;
		}

		return alternatives;
	}
}
