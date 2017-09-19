package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.MovingAverage;

/**
 * <p>
 * Title: OutlierRemoverMovingAverageBased
 * </p>
 * 
 * <p>
 * Description: Cleanses outliers of a time series using a moving average model as reference for local outlier detection.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public class OutlierRemoverMovingAverageBased extends DimensionBasedTimeSeriesMultivariateProcessor {

	// standard deviation ratio
	double stdDeviationRatio;

	// the value that is assigned to an outlier
	double outlierValue;

	// moving average options
	private int elements;
	private boolean considerFutureValues;

	private MovingAverage movingAverage;

	public OutlierRemoverMovingAverageBased() {
		this(2.96, 3, true, Double.NaN);
	}

	public OutlierRemoverMovingAverageBased(double stdDeviationRatio, int elements) {
		this(stdDeviationRatio, elements, true, Double.NaN);
	}

	public OutlierRemoverMovingAverageBased(double stdDeviationRatio, int elements, boolean considerFutureValues) {
		this(stdDeviationRatio, elements, considerFutureValues, Double.NaN);
	}

	public OutlierRemoverMovingAverageBased(double stdDeviationRatio, int elements, boolean considerFutureValues, double outlierValue) {
		this.stdDeviationRatio = stdDeviationRatio;
		this.elements = elements;
		this.considerFutureValues = considerFutureValues;
		this.outlierValue = outlierValue;
	}

	public OutlierRemoverMovingAverageBased(double stdDeviationRatio, MovingAverage movingAverage) {
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
					alternatives.add(new OutlierRemoverMovingAverageBased(std, ele, considerFutureValues));
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
}
