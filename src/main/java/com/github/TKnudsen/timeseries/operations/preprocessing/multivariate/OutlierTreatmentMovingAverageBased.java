package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.MovingAverage;

/**
 * <p>
 * Title: OutlierTreatmentMovingAverageBased
 * </p>
 * 
 * <p>
 * Description: Replaces values that are farer away from the calculated moving
 * average than a given standard deviation ratio. Replaces with a given value
 * (standard is NaN). The temporal domain is untouched.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public class OutlierTreatmentMovingAverageBased extends DimensionBasedTimeSeriesMultivariateProcessor {

	// standard deviation ratio
	double stdDeviationRatio;

	// the value that is assigned to an outlier
	double outlierValue;

	// moving average options
	private int elements;
	private boolean considerFutureValues;

	private MovingAverage movingAverage;

	public OutlierTreatmentMovingAverageBased() {
		this(2.96, 3, true, Double.NaN);
	}

	public OutlierTreatmentMovingAverageBased(double stdDeviationRatio, int elements) {
		this(stdDeviationRatio, elements, true, Double.NaN);
	}

	public OutlierTreatmentMovingAverageBased(double stdDeviationRatio, int elements, boolean considerFutureValues) {
		this(stdDeviationRatio, elements, considerFutureValues, Double.NaN);
	}

	public OutlierTreatmentMovingAverageBased(double stdDeviationRatio, int elements, boolean considerFutureValues, double outlierReplacementValue) {
		this.stdDeviationRatio = stdDeviationRatio;
		this.elements = elements;
		this.considerFutureValues = considerFutureValues;
		this.outlierValue = outlierReplacementValue;
	}

	public OutlierTreatmentMovingAverageBased(double stdDeviationRatio, MovingAverage movingAverage) {
		this.stdDeviationRatio = stdDeviationRatio;
		this.movingAverage = movingAverage;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesMultivariate>> getAlternativeParameterizations(int count) {
		List<IDataProcessor<ITimeSeriesMultivariate>> alternatives = new ArrayList<>();

		int sqrt = (int) Math.sqrt(count);

		List<Double> alternativeDoubles = ParameterSupportTools.getAlternativeDoubles(stdDeviationRatio, sqrt + 1);
		List<Integer> alternativeElements = ParameterSupportTools.getAlternativeIntegers(elements, sqrt);

		for (Double std : alternativeDoubles)
			for (Integer ele : alternativeElements) {
				if (std > 0 && ele > 0)
					alternatives.add(new OutlierTreatmentMovingAverageBased(std, ele, considerFutureValues));
				if (alternatives.size() == count)
					return alternatives;
			}

		return alternatives;
	}

	@Override
	protected void initializeUnivariateTimeSeriesProcessor() {
		if (movingAverage == null)
			setUnivariateTimeSeriesProcessor(new com.github.TKnudsen.timeseries.operations.preprocessing.univariate.OutlierTreatmentMovingAverageBased(stdDeviationRatio, elements, considerFutureValues, outlierValue));
		else
			setUnivariateTimeSeriesProcessor(new com.github.TKnudsen.timeseries.operations.preprocessing.univariate.OutlierTreatmentMovingAverageBased(stdDeviationRatio, movingAverage, outlierValue));
	}

	public double getStdDeviationRatio() {
		return stdDeviationRatio;
	}

	public double getOutlierValue() {
		return outlierValue;
	}

	public int getElements() {
		return elements;
	}

	public boolean isConsiderFutureValues() {
		return considerFutureValues;
	}
}
