package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.uncertainty.processing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.IValueUncertainty;
import com.github.TKnudsen.ComplexDataObject.data.uncertainty.distribution.NumericalDistributionUncertainty;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.uncertaintyMeasures.TimeSeriesProcessingUncertaintyMeasure;

/**
 * Computes the relative change of every timestamp and dimension. Relative
 * change is assessed by the bounds of the value domains of the individual
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

		// create composition of univariate value uncertainties
		Map<String, SortedMap<Long, IValueUncertainty<Double>>> valueUncertaintyMeasuresPerDimension = new LinkedHashMap<>();

		for (String dimension : originalTimeSeries.getAttributeNames()) {
			com.github.TKnudsen.timeseries.operations.preprocessing.uncertaintyMeasures.univariate.RelativeValueUncertaintyMeasure measure = new com.github.TKnudsen.timeseries.operations.preprocessing.uncertaintyMeasures.univariate.RelativeValueUncertaintyMeasure();
			measure.calculateUncertainty(originalTimeSeries.getTimeSeries(dimension),
					processedTimeSeries.getTimeSeries(dimension));

			SortedMap<Long, IValueUncertainty<Double>> uncertaintiesOverTime = measure.getUncertaintiesOverTime();
			valueUncertaintyMeasuresPerDimension.put(dimension, uncertaintiesOverTime);
		}

		// iterate over timestamps and aggregate the relative uncertainty values of
		// every domain
		for (Long timeStamp : originalTimeSeries.getTimestamps()) {
			List<Double> relatives = new ArrayList<>();

			try {
				// dummy access
				processedTimeSeries.getValue(timeStamp, false);

				for (String dimension : originalTimeSeries.getAttributeNames()) {
					SortedMap<Long, IValueUncertainty<Double>> uncertainties = valueUncertaintyMeasuresPerDimension
							.get(dimension);

					relatives.add(uncertainties.get(timeStamp).getUncertainty());
				}

				uncertaintiesOverTime.put(timeStamp, new NumericalDistributionUncertainty(relatives));
			} catch (Exception e) {
				// time stamp does not exist in processed time series
			}
		}
	}

}
