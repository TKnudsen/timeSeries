package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesStatistics;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * <p>
 * Title: OutlierTreatmentMovingAverageBased
 * </p>
 * 
 * <p>
 * Description: Replaces values that are farer away from the calculated moving
 * average than a given standard deviation ratio. Replaces with NAN. The
 * temporal domain is untouched.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */

public class OutlierTreatmentMovingAverageBased implements ITimeSeriesUnivariatePreprocessor {

	// standard deviation ratio
	double stdDevRatio;

	// moving average options
	private Integer elements;
	private Boolean considerFutureValues;
	private MovingAverage movingAverage;

	@SuppressWarnings("unused")
	private OutlierTreatmentMovingAverageBased() {
		this.elements = 3;
		considerFutureValues = true;
	}

	public OutlierTreatmentMovingAverageBased(double stdDevRatio, int elements) {
		this.stdDevRatio = stdDevRatio;
		this.elements = new Integer(elements);

		initialize();
	}

	public OutlierTreatmentMovingAverageBased(double stdDevRatio, int elements, boolean considerFutureValues) {
		this.stdDevRatio = stdDevRatio;
		this.elements = new Integer(elements);
		this.considerFutureValues = new Boolean(considerFutureValues);

		initialize();
	}

	public OutlierTreatmentMovingAverageBased(double stdDevRatio, MovingAverage movingAverage) {
		this.stdDevRatio = stdDevRatio;
		this.movingAverage = movingAverage;

		initialize();
	}

	private void initialize() {
		if (movingAverage != null)
			return;

		if (elements != null)
			if (considerFutureValues != null && !considerFutureValues)
				movingAverage = new MovingAverage(elements.intValue(), considerFutureValues);
			else
				movingAverage = new MovingAverage(elements, true);

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

		std *= stdDevRatio;

		for (int i = 0; i < timeSeries.size(); i++) {
			if (Math.abs(timeSeries.getValue(i) - clone.getValue(i)) > std) {
				timeSeries.removeTimeValue(i);
				clone.removeTimeValue(i--);
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

		List<Double> alternativeDoubles = ParameterSupportTools.getAlternativeDoubles(stdDevRatio, sqrt + 1);
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
