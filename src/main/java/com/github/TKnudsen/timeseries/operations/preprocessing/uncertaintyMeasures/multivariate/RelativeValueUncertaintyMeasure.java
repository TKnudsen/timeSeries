package com.github.TKnudsen.timeseries.operations.preprocessing.uncertaintyMeasures.multivariate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.IValueUncertainty;
import com.github.TKnudsen.ComplexDataObject.data.uncertainty.distribution.ValueUncertaintyDistribution;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.uncertainty.ITimeSeriesValueUncertaintyCalculationResult;
import com.github.TKnudsen.timeseries.data.uncertainty.multivariate.TimeSeriesMultivariateValueUncertainty;
import com.github.TKnudsen.timeseries.data.uncertainty.multivariate.UncertaintyMultivariateTimeSeries;

/**
 * Computes the relative change of every timeStamp and dimension. Relative
 * change is assessed by the bounds of the value domains of the individual
 * (original) time series. For every timeStamp the relative changes in every
 * dimension are collected and represented as a
 * {@link NumericalDistributionUncertainty}.
 * 
 * <p>
 * Copyright: Copyright (c) 2017-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.07
 */
public class RelativeValueUncertaintyMeasure extends TimeSeriesMultivariateUncertaintyMeasure {

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
	public ITimeSeriesValueUncertaintyCalculationResult<List<IValueUncertainty>> compute(ITimeSeriesMultivariate originalTimeSeries,
			ITimeSeriesMultivariate processedTimeSeries) {

		uncertaintiesOverTime = new TreeMap<>();

		// create composition of univariate value uncertainties
		Map<String, ITimeSeriesValueUncertaintyCalculationResult<IValueUncertainty>> valueUncertaintyMeasuresPerDimension = new LinkedHashMap<>();

		for (String dimension : originalTimeSeries.getAttributeNames()) {
			com.github.TKnudsen.timeseries.operations.preprocessing.uncertaintyMeasures.univariate.RelativeValueUncertaintyMeasure measure = new com.github.TKnudsen.timeseries.operations.preprocessing.uncertaintyMeasures.univariate.RelativeValueUncertaintyMeasure();
			ITimeSeriesValueUncertaintyCalculationResult<IValueUncertainty> vuOneDimension = measure
					.compute(originalTimeSeries.getTimeSeries(dimension), processedTimeSeries.getTimeSeries(dimension));

			valueUncertaintyMeasuresPerDimension.put(dimension, vuOneDimension);
		}

		List<Long> timeStamps = new ArrayList<>();
		List<List<IValueUncertainty>> valueUncertaintiesAllDimensionsOverTime = new ArrayList<>();

		// iterate over timeStamps and aggregate the relative uncertainty values of
		// every domain
		for (Long timeStamp : originalTimeSeries.getTimestamps()) {
			List<Double> relatives = new ArrayList<>();
			List<IValueUncertainty> valueUncertainties = new ArrayList<>();

			// requires interpolation search from long to int (index)
			int timeSeriesIndexToSpeedupAccess = originalTimeSeries.findByDate(timeStamp, true);

			try {
//				// dummy access
//				processedTimeSeries.getValue(timeStamp, false);

				for (String dimension : originalTimeSeries.getAttributeNames()) {
					ITimeSeriesValueUncertaintyCalculationResult<IValueUncertainty> uncertainties = valueUncertaintyMeasuresPerDimension
							.get(dimension);

					// speedup
					IValueUncertainty vu = null;
					if (uncertainties.getValueUncertainties().getTimestamp(timeSeriesIndexToSpeedupAccess) == timeStamp)
						vu = uncertainties.getValueUncertainties().getValue(timeSeriesIndexToSpeedupAccess);
					else
						vu = uncertainties.getValueUncertainties().getValue(timeStamp, false);

					relatives.add(vu.getAmount());
					valueUncertainties.add(vu);
				}

				uncertaintiesOverTime.put(timeStamp, new ValueUncertaintyDistribution(relatives));

				timeStamps.add(timeStamp);
				valueUncertaintiesAllDimensionsOverTime.add(valueUncertainties);
			} catch (Exception e) {
				// time stamp does not exist in processed time series
			}
		}

		this.timeSeriesValueUncertainty = new TimeSeriesMultivariateValueUncertainty(
				new UncertaintyMultivariateTimeSeries(timeStamps, valueUncertaintiesAllDimensionsOverTime), true);

		return timeSeriesValueUncertainty;
	}

}
