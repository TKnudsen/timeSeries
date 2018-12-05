package com.github.TKnudsen.timeseries.operations.preprocessing.uncertaintyMeasures.univariate;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.IValueUncertainty;
import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.ValueUncertainty;
import com.github.TKnudsen.ComplexDataObject.model.transformations.normalization.LinearNormalizationFunction;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.uncertainty.ITimeSeriesValueUncertaintyCalculationResult;
import com.github.TKnudsen.timeseries.data.uncertainty.univariate.TimeSeriesUnivariateValueUncertaintyCalculationResult;
import com.github.TKnudsen.timeseries.data.uncertainty.univariate.UncertaintyTimeSeries;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
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
 * @version 1.06
 */
public class RelativeValueUncertaintyMeasure extends TimeSeriesUnivariateUncertaintyMeasure {

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
	public ITimeSeriesValueUncertaintyCalculationResult<IValueUncertainty> compute(
			ITimeSeriesUnivariate originalTimeSeries, ITimeSeriesUnivariate processedTimeSeries) {

		// uncertaintiesOverTime = new TreeMap<>();
		List<Long> timeStamps = new ArrayList<>();
		List<IValueUncertainty> valueUncertainties = new ArrayList<>();

		// characterize the value domain
		double min = TimeSeriesTools.getMinValue(originalTimeSeries);
		double max = TimeSeriesTools.getMaxValue(originalTimeSeries);

		LinearNormalizationFunction valueDeltaNormalizationFunction = new LinearNormalizationFunction(0.0, max - min,
				true);

		for (Long timeStamp : originalTimeSeries.getTimestamps()) {

			ValueUncertainty valueUncertainty = null;
			double originalV = originalTimeSeries.getValue(timeStamp, false);
			double processedV = Double.NaN;

			try {
				// access may fail
				processedV = processedTimeSeries.getValue(timeStamp, false);
			} catch (Exception e) {
				// time stamp does not exist in processed time series
				processedV = TimeSeriesTools.getInterpolatedValue(processedTimeSeries, timeStamp);
				
				int indexLastValue = processedTimeSeries.findByDate(timeStamp, false);
				originalV = processedTimeSeries.getValue(indexLastValue);
			}

			if (Double.isNaN(processedV))
				continue;

			valueUncertainty = new ValueUncertainty(
					valueDeltaNormalizationFunction.apply(Math.abs(originalV - processedV)).doubleValue());

			timeStamps.add(timeStamp);
			valueUncertainties.add(valueUncertainty);
		}

		timeSeriesValueUncertainty = new TimeSeriesUnivariateValueUncertaintyCalculationResult(
				new UncertaintyTimeSeries<IValueUncertainty>(timeStamps, valueUncertainties), true);

		return timeSeriesValueUncertainty;
	}

}
