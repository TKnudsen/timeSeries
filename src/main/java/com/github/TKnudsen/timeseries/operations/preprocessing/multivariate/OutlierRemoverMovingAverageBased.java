package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;

/**
 * <p>
 * Title: OutlierRemoverMovingAverageBased
 * </p>
 * 
 * <p>
 * Description: Cleanses outliers of a time series using a moving average model
 * as reference for local outlier detection.
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

	private double stdDevRatio;
	private int elements;
	private boolean considerFutureValues;

	public OutlierRemoverMovingAverageBased(double stdDevRatio, int elements, boolean considerFutureValues) {
		this.stdDevRatio = stdDevRatio;
		this.elements = elements;
		this.considerFutureValues = considerFutureValues;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesMultivariate>> getAlternativeParameterizations(int count) {
		List<IDataProcessor<ITimeSeriesMultivariate>> alternatives = new ArrayList<>();

		int sqrt = (int) Math.sqrt(count);

		List<Double> alternativeDoubles = ParameterSupportTools.getAlternativeDoubles(stdDevRatio, sqrt + 1);
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
		setUnivariateTimeSeriesProcessor(new com.github.TKnudsen.timeseries.operations.preprocessing.univariate.OutlierTreatmentMovingAverageBased(stdDevRatio, elements, considerFutureValues));
	}
}
