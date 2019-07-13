package com.github.TKnudsen.timeseries.operations.preprocessing.uncertaintyMeasures.univariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.IValueUncertainty;
import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.ValueUncertainty;
import com.github.TKnudsen.timeseries.data.uncertainty.ITimeSeriesValueUncertaintyCalculationResult;
import com.github.TKnudsen.timeseries.data.uncertainty.univariate.TimeSeriesUnivariateValueUncertaintyCalculationResult;
import com.github.TKnudsen.timeseries.data.uncertainty.univariate.UncertaintyTimeSeries;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * A class that computes a time series value uncertainty as the difference
 * between an original and a processed time series.
 * 
 * Can handle time stamps that are removed.
 * 
 * Produces value uncertainty results in an absolute scale.
 *
 */
public class DifferenceUncertaintyMeasure extends TimeSeriesUnivariateUncertaintyMeasure {

	@Override
	public ITimeSeriesValueUncertaintyCalculationResult<IValueUncertainty> compute(
			ITimeSeriesUnivariate originalTimeSeries, ITimeSeriesUnivariate processedTimeSeries) {

		List<Long> timeStamps = new ArrayList<>();
		List<IValueUncertainty> valueUncertainties = new ArrayList<>();

		for (Long timeStamp : originalTimeSeries.getTimestamps()) {

			ValueUncertainty valueUncertainty = null;
			double originalV = originalTimeSeries.getValue(timeStamp, false);
			double deltaV = Double.MAX_VALUE;

			if (!Double.isNaN(originalV))
				try {
					// access may fail
					double processedV = processedTimeSeries.getValue(timeStamp, false);
					deltaV = Math.abs(originalV - processedV);

				} catch (Exception e) {
					// time stamp does not exist in processed time series
					// min max to limit time interval to the bounts of the output
					long l = Math.max(processedTimeSeries.getFirstTimestamp(),
							Math.min(processedTimeSeries.getLastTimestamp(), timeStamp));
					double processedV = TimeSeriesTools.getInterpolatedValue(processedTimeSeries, l);

					if (!Double.isNaN(processedV)) {
						deltaV = Math.abs(originalV - processedV);
					}
				}

			valueUncertainty = new ValueUncertainty(deltaV);

			timeStamps.add(timeStamp);
			valueUncertainties.add(valueUncertainty);
		}

		timeSeriesValueUncertainty = new TimeSeriesUnivariateValueUncertaintyCalculationResult(
				new UncertaintyTimeSeries<IValueUncertainty>(timeStamps, valueUncertainties), false);

		return timeSeriesValueUncertainty;
	}

}
