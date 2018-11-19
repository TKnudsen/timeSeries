package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesStatistics;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * <p>
 * Title: OutlierTreatmentMovingAverageBased
 * </p>
 * 
 * <p>
 * Description: Replaces values that are farer away from the calculated moving
 * average than a given standard deviation ratio. Replaces with the minimum
 * maximum value that is still allowed. The temporal domain is untouched.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.05
 */

public class OutlierTreatmentMovingAverageBased extends TimeSeriesProcessor<ITimeSeriesUnivariate> {

	// standard deviation ratio
	double stdDeviationRatio;

	// moving average options
	private int elements;
	private boolean considerFutureValues;

	private MovingAverage movingAverage;

	public OutlierTreatmentMovingAverageBased() {
		this(2.96, 3, true);
	}

	public OutlierTreatmentMovingAverageBased(double stdDeviationRatio, int elements) {
		this(stdDeviationRatio, elements, true);
	}

	public OutlierTreatmentMovingAverageBased(double stdDeviationRatio, int elements, boolean considerFutureValues) {
		this.stdDeviationRatio = stdDeviationRatio;
		this.elements = elements;
		this.considerFutureValues = considerFutureValues;

		initializeMovingAverage();
	}

	public OutlierTreatmentMovingAverageBased(double stdDeviationRatio, MovingAverage movingAverage) {
		this.stdDeviationRatio = stdDeviationRatio;
		this.movingAverage = movingAverage;
	}

	private void initializeMovingAverage() {
		if (elements <= 0)
			throw new IllegalArgumentException("OutlierTreatmentMovingAverage: Kernel width has to be larger than 0.");

		movingAverage = new MovingAverage(elements, considerFutureValues);

		if (movingAverage == null)
			throw new IllegalArgumentException();
	}

	@Override
	public void process(List<ITimeSeriesUnivariate> data) {
		for (ITimeSeriesUnivariate timeSeries : data)
			process(timeSeries);
	}

	private void process(ITimeSeriesUnivariate timeSeries) {

		if (timeSeries == null)
			return;

		ITimeSeriesUnivariate clone = TimeSeriesTools.cloneTimeSeries(timeSeries);
		List<ITimeSeriesUnivariate> clones = new ArrayList<ITimeSeriesUnivariate>(Arrays.asList(clone));
		movingAverage.process(clones);

		TimeSeriesStatistics statistics = new TimeSeriesStatistics(timeSeries);

		double std = statistics.getStandardDeviation();
		if (std == 0) // min == max
			return;

		std *= stdDeviationRatio;

		for (int i = 0; i < timeSeries.size(); i++) {
			if (Math.abs(timeSeries.getValue(i) - clone.getValue(i)) > std) {
				if ((timeSeries.getValue(i) - clone.getValue(i)) > std)
					timeSeries.replaceValue(i, clone.getValue(i) + std);
				else
					timeSeries.replaceValue(i, clone.getValue(i) - std);
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

		int sqrt = (int) Math.sqrt(count);

		List<Double> alternativeDoubles = ParameterSupportTools.getAlternativeDoubles(stdDeviationRatio, sqrt + 1);
		List<Integer> alternativeElements = ParameterSupportTools.getAlternativeIntegers(elements, sqrt);

		for (Double std : alternativeDoubles)
			for (Integer ele : alternativeElements) {
				if (std > 0 && ele > 0)
					alternatives.add(new OutlierTreatmentMovingAverageBased(std, ele));
				if (alternatives.size() == count)
					return alternatives;
			}

		return alternatives;
	}
}
