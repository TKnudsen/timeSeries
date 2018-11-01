package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.uncertainty.processing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.distribution.NumericalDistributionUncertainty;
import com.github.TKnudsen.ComplexDataObject.model.transformations.normalization.LinearNormalizationFunction;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * Computes the relative change of every timestamp and dimension. Relative
 * change is assessed by the bounds of the value domaines of the individual
 * (original) time series. For every timestamp the relative changes in every
 * dimension are collected and represented as a
 * {@link NumericalDistributionUncertainty}.
 * 
 * <p>
 * Copyright: Copyright (c) 2017-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.05
 */
public class RelativeValueUncertaintyMeasure extends TimeSeriesProcessingUncertaintyMeasure<ITimeSeriesMultivariate> {

	public RelativeValueUncertaintyMeasure() {
		super();
	}

	@Override
	public String getName() {
		return "RelativeValueUncertantyMeasure";
	}

	@Override
	public String getDescription() {
		return "Calculates the relative change of the value domains of a given MVTS over time";
	}

	@Override
	public void calculateUncertainty(ITimeSeriesMultivariate originalTimeSeries,
			ITimeSeriesMultivariate processedTimeSeries) {

		uncertaintiesOverTime = new TreeMap<>();

		// characterize value domain of every dimension
		Map<String, LinearNormalizationFunction> valueDeltaNormalizationFunctionsPerDimension = new LinkedHashMap<>();

		for (String dimension : originalTimeSeries.getAttributeNames()) {
			double min = TimeSeriesTools.getMinValue(originalTimeSeries.getTimeSeries(dimension));
			double max = TimeSeriesTools.getMaxValue(originalTimeSeries.getTimeSeries(dimension));

			valueDeltaNormalizationFunctionsPerDimension.put(dimension,
					new LinearNormalizationFunction(0.0, max - min, true));
		}

		for (Long timeStamp : originalTimeSeries.getTimestamps()) {

			List<Double> relatives = new ArrayList<>();

			try {
				// dummy access
				processedTimeSeries.getValue(timeStamp, false);

				for (String dimension : originalTimeSeries.getAttributeNames()) {
					double originalV = originalTimeSeries.getTimeSeries(dimension).getValue(timeStamp, false);
					double processedV = processedTimeSeries.getTimeSeries(dimension).getValue(timeStamp, false);

					relatives.add(valueDeltaNormalizationFunctionsPerDimension.get(dimension)
							.apply(Math.abs(originalV - processedV)).doubleValue());
				}

				uncertaintiesOverTime.put(timeStamp, new NumericalDistributionUncertainty(relatives));
			} catch (Exception e) {
				// time stamp does not exist in processed time series
			}
		}
	}

}
