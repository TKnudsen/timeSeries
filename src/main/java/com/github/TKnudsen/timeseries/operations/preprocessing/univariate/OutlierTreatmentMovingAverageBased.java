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
 * average than a given standard deviation ratio. Replaces with the minimum
 * maximum allowed value (+-std), or a pre-defined outlier values (e.g.
 * Double.NaN). The temporal domain is untouched.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */

public class OutlierTreatmentMovingAverageBased implements ITimeSeriesUnivariatePreprocessor {

	// standard deviation ratio
	double stdDeviationRatio;

	// the value that is assigned to an outlier
	Double outlierValue;

	// moving average options
	private int elements;
	private boolean considerFutureValues;

	private MovingAverage movingAverage;

	public OutlierTreatmentMovingAverageBased() {
		this(2.96, 3, true, null);
	}

	public OutlierTreatmentMovingAverageBased(double stdDeviationRatio, int elements) {
		this(stdDeviationRatio, elements, true, null);
	}

	public OutlierTreatmentMovingAverageBased(double stdDeviationRatio, int elements, boolean considerFutureValues) {
		this(stdDeviationRatio, elements, considerFutureValues, null);
	}

	public OutlierTreatmentMovingAverageBased(double stdDeviationRatio, int elements, boolean considerFutureValues, Double outlierValue) {
		this.stdDeviationRatio = stdDeviationRatio;
		this.elements = elements;
		this.considerFutureValues = considerFutureValues;
		this.outlierValue = outlierValue;

		initializeMovingAverage();
	}

	public OutlierTreatmentMovingAverageBased(double stdDeviationRatio, MovingAverage movingAverage) {
		this(stdDeviationRatio, movingAverage, null);
	}

	public OutlierTreatmentMovingAverageBased(double stdDeviationRatio, MovingAverage movingAverage, Double outlierValue) {
		this.stdDeviationRatio = stdDeviationRatio;
		this.movingAverage = movingAverage;
		this.outlierValue = outlierValue;
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
				if (outlierValue == null) {
					if ((timeSeries.getValue(i) - clone.getValue(i)) > std)
						timeSeries.replaceValue(i, clone.getValue(i) + std);
					else
						timeSeries.replaceValue(i, clone.getValue(i) - std);
				} else
					timeSeries.replaceValue(i, outlierValue);
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
