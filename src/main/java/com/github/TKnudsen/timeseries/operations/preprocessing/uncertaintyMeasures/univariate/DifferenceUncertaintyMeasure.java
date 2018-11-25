package com.github.TKnudsen.timeseries.operations.preprocessing.uncertaintyMeasures.univariate;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.IValueUncertainty;
import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.ValueUncertainty;
import com.github.TKnudsen.timeseries.data.uncertainty.ITimeSeriesValueUncertaintyCalculationResult;
import com.github.TKnudsen.timeseries.data.uncertainty.univariate.TimeSeriesUnivariateValueUncertainty;
import com.github.TKnudsen.timeseries.data.uncertainty.univariate.UncertaintyTimeSeries;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

/**
 * A class that computes a time series value uncertainty as the difference
 * between an original and a processed time series.
 * 
 * Can become negative.
 * 
 * Produces value uncertainty results in an absolute scale.
 *
 */
public class DifferenceUncertaintyMeasure extends TimeSeriesUnivariateUncertaintyMeasure {

	@Override
	public ITimeSeriesValueUncertaintyCalculationResult<IValueUncertainty> compute(ITimeSeriesUnivariate originalTimeSeries,
			ITimeSeriesUnivariate processedTimeSeries) {

		uncertaintiesOverTime = new TreeMap<>();
		List<Long> timeStamps = new ArrayList<>();
		List<IValueUncertainty> valueUncertainties = new ArrayList<>();

		for (Long timeStamp : originalTimeSeries.getTimestamps()) {
			try {
				double originalV = originalTimeSeries.getValue(timeStamp, false);

				// access may fail
				double processedV = processedTimeSeries.getValue(timeStamp, false);

				ValueUncertainty valueUncertainty = new ValueUncertainty(Math.abs(originalV - processedV));

				uncertaintiesOverTime.put(timeStamp, valueUncertainty);

				timeStamps.add(timeStamp);
				valueUncertainties.add(valueUncertainty);
			} catch (Exception e) {
				// time stamp does not exist in processed time series
			}
		}

		timeSeriesValueUncertainty = new TimeSeriesUnivariateValueUncertainty(
				new UncertaintyTimeSeries<IValueUncertainty>(timeStamps, valueUncertainties), false);

		return timeSeriesValueUncertainty;
	}

}
