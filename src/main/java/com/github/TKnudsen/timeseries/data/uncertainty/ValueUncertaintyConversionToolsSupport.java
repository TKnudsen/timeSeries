package com.github.TKnudsen.timeseries.data.uncertainty;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.ValueUncertaintyCharacteristics;
import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.IValueUncertainty;
import com.github.TKnudsen.ComplexDataObject.data.uncertainty.distribution.IValueUncertaintyDistribution;
import com.github.TKnudsen.ComplexDataObject.data.uncertainty.range.IValueUncertaintyRange;
import com.github.TKnudsen.ComplexDataObject.model.tools.DataConversion;
import com.github.TKnudsen.timeseries.data.ITimeSeries;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.multivariate.TimeSeriesMultivariateFactory;

/**
 * 
 * timeSeries <br>
 * <br>
 * 
 * Little helpers for the tools class
 * {@link ValueUncertaintyConversionTools}<br>
 * <br>
 *
 * Copyright: (c) 2016-2018 Juergen Bernard,
 * https://github.com/TKnudsen/timeSeries<br>
 * <br>
 * 
 */
class ValueUncertaintyConversionToolsSupport {

	/**
	 * transforms value uncertainty information for every time stamp and dimension
	 * into a MultivariateTimeSeries with Double values.
	 * 
	 * @param uncertainties
	 * @return
	 */
	static ITimeSeriesMultivariate computeCharacteristicsForEachDimension(
			ITimeSeries<List<IValueUncertainty>> uncertainties,
			ValueUncertaintyCharacteristics valueUncertaintyCharacteristics) {

		List<Entry<Long, Double[]>> pairs = new ArrayList<>();

		List<Long> timeStamps = uncertainties.getTimestamps();
		for (Long timeStamp : timeStamps) {
			List<? extends IValueUncertainty> vus = uncertainties.getValue(timeStamp, false);

			List<Double> v = new ArrayList<>();
			for (IValueUncertainty vu : vus) {
				if (acceptsCharacteristics(vu, valueUncertaintyCharacteristics))
					v.add(getCharacteristics(vu, valueUncertaintyCharacteristics));
				else
					throw new IllegalArgumentException(
							"ValueUncertaintyConversionToolsSupport.computeCharacteristicsForEachDimension: uncertainty data do not contain "
									+ valueUncertaintyCharacteristics + " information");
			}
			Double[] array = DataConversion.listToArray(v, Double.class);
			pairs.add(new AbstractMap.SimpleEntry<Long, Double[]>(timeStamp, array));
		}

		ITimeSeriesMultivariate mvts = TimeSeriesMultivariateFactory.createTimeSeriesMultivatiate(pairs, Double.NaN);

		mvts.setName(valueUncertaintyCharacteristics.name());

		return mvts;
	}

	private static boolean acceptsCharacteristics(IValueUncertainty valueUncertaintyType,
			ValueUncertaintyCharacteristics valueUncertaintyCharacteristics) {

		Objects.requireNonNull(valueUncertaintyType);

		switch (valueUncertaintyCharacteristics) {
		case AMOUNT:
			return true;
		case LOWERBOUND:
			if (valueUncertaintyType instanceof IValueUncertaintyRange)
				return true;
			else
				return false;
		case UPPERBOUND:
			if (valueUncertaintyType instanceof IValueUncertaintyRange)
				return true;
			else
				return false;
		case LOWERQUARTILE:
			if (valueUncertaintyType instanceof IValueUncertaintyDistribution)
				return true;
			else
				return false;
		case UPPERQUARTILE:
			if (valueUncertaintyType instanceof IValueUncertaintyDistribution)
				return true;
			else
				return false;
		case MEDIAN:
			if (valueUncertaintyType instanceof IValueUncertaintyDistribution)
				return true;
			else
				return false;
		case MEAN:
			if (valueUncertaintyType instanceof IValueUncertaintyDistribution)
				return true;
			else
				return false;
		default:
			throw new IllegalArgumentException();
		}
	}

	private static Double getCharacteristics(IValueUncertainty valueUncertaintyType,
			ValueUncertaintyCharacteristics characteristics) {

		if (valueUncertaintyType == null)
			return null;

		switch (characteristics) {
		case AMOUNT:
			return valueUncertaintyType.getAmount();
		case LOWERBOUND:
			if (valueUncertaintyType instanceof IValueUncertaintyRange)
				return ((IValueUncertaintyRange) valueUncertaintyType).getLowerBound();
		case UPPERBOUND:
			if (valueUncertaintyType instanceof IValueUncertaintyRange)
				return ((IValueUncertaintyRange) valueUncertaintyType).getUpperBound();
		case LOWERQUARTILE:
			if (valueUncertaintyType instanceof IValueUncertaintyDistribution)
				return ((IValueUncertaintyDistribution) valueUncertaintyType).getLowerQuartile();
		case UPPERQUARTILE:
			if (valueUncertaintyType instanceof IValueUncertaintyDistribution)
				return ((IValueUncertaintyDistribution) valueUncertaintyType).getUpperQartile();
		case MEDIAN:
			if (valueUncertaintyType instanceof IValueUncertaintyDistribution)
				return ((IValueUncertaintyDistribution) valueUncertaintyType).getMedian();
		case MEAN:
			if (valueUncertaintyType instanceof IValueUncertaintyDistribution)
				return ((IValueUncertaintyDistribution) valueUncertaintyType).getMean();
		}
		new IllegalArgumentException();
		return null;
	}
}
