package com.github.TKnudsen.timeseries.operations.preprocessing.uncertaintyMeasures.univariate;

import java.util.TreeMap;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.ValueUncertainty;
import com.github.TKnudsen.ComplexDataObject.model.transformations.normalization.LinearNormalizationFunction;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.uncertaintyMeasures.TimeSeriesProcessingUncertaintyMeasure;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * Computes the relative change of every timestamp. Relative change is assessed
 * by the bounds of the value domain of the original time series. For every
 * timestamp the relative changes are collected and represented as a
 * {@link ValueUncertainty}.
 * 
 * <p>
 * Copyright: Copyright (c) 2017-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public class RelativeValueUncertaintyMeasure extends TimeSeriesProcessingUncertaintyMeasure<ITimeSeriesUnivariate> {

	public RelativeValueUncertaintyMeasure() {
		super();
	}

	@Override
	public String getName() {
		return "RelativeValueUncertantyMeasure";
	}

	@Override
	public String getDescription() {
		return "Calculates the relative change of a given time series";
	}

	@Override
	public void calculateUncertainty(ITimeSeriesUnivariate originalTimeSeries,
			ITimeSeriesUnivariate processedTimeSeries) {

		uncertaintiesOverTime = new TreeMap<>();

		// characterize the value domain
		double min = TimeSeriesTools.getMinValue(originalTimeSeries);
		double max = TimeSeriesTools.getMaxValue(originalTimeSeries);

		LinearNormalizationFunction valueDeltaNormalizationFunction = new LinearNormalizationFunction(0.0, max - min,
				true);

		for (Long timeStamp : originalTimeSeries.getTimestamps()) {
			try {
				double originalV = originalTimeSeries.getValue(timeStamp, false);

				// access may fail
				double processedV = processedTimeSeries.getValue(timeStamp, false);

				uncertaintiesOverTime.put(timeStamp, new ValueUncertainty(
						valueDeltaNormalizationFunction.apply(Math.abs(originalV - processedV)).doubleValue()));
			} catch (Exception e) {
				// time stamp does not exist in processed time series
			}
		}
	}

}
