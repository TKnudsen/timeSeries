package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesStatistics;

/**
 * <p>
 * Title: OutlierTreatment
 * </p>
 * 
 * *
 * <p>
 * Description: Replaces the value domain for values higher/lower than a given
 * multiple of the standard deviation. Replaces with the minimum maximum value
 * that is still allowed. The temporal domain is untouched.
 * 
 * Disclaimer: uses a global std and not local. Implementation is not really
 * sophisticated.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.06
 */

public class OutlierTreatment extends TimeSeriesProcessor<ITimeSeriesUnivariate> {

	// standard deviation ratio
	double stdDeviationRatio;

	public OutlierTreatment() {
		this(2.96);
	}

	public OutlierTreatment(double stdDeviationRatio) {
		this.stdDeviationRatio = stdDeviationRatio;
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
		std *= stdDeviationRatio;

		for (int i = 0; i < timeSeries.size(); i++) {
			if (Math.abs(timeSeries.getValue(i) - means) > std) {
				if ((timeSeries.getValue(i) - means) > std)
					timeSeries.replaceValue(i, means + std);
				else
					timeSeries.replaceValue(i, means - std);
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

		List<Double> alternativeDoubles = ParameterSupportTools.getAlternativeDoubles(stdDeviationRatio, count);

		for (Double std : alternativeDoubles) {
			if (std > 0)
				alternatives.add(new OutlierTreatment(std));
			if (alternatives.size() == count)
				return alternatives;
		}

		return alternatives;
	}
}
