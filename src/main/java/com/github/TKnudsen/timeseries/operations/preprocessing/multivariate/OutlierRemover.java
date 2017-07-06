package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;

/**
 * <p>
 * Title: OutlierRemover
 * </p>
 * 
 * <p>
 * Description: Removes values that higher/lower than a given multiple of the
 * standard deviation. The value domains of every individual
 * IUnivariateTimeSeries are used to calculate the std.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class OutlierRemover extends DimensionBasedTimeSeriesMultivariateProcessor {

	double std;

	@SuppressWarnings("unused")
	private OutlierRemover() {
		this(2.96);
	}

	public OutlierRemover(double std) {
		this.std = std;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesMultivariate>> getAlternativeParameterizations(int count) {
		List<IDataProcessor<ITimeSeriesMultivariate>> alternatives = new ArrayList<>();

		List<Double> alternativeDoubles = ParameterSupportTools.getAlternativeDoubles(std, count);

		for (Double std : alternativeDoubles) {
			if (std > 0)
				alternatives.add(new OutlierRemover(std));
			if (alternatives.size() == count)
				return alternatives;
		}

		return alternatives;
	}

	@Override
	protected void initializeUnivariateTimeSeriesProcessor() {
		setUnivariateTimeSeriesProcessor(new com.github.TKnudsen.timeseries.operations.preprocessing.univariate.OutlierRemover(std));
	}
}
