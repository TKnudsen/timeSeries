package com.github.TKnudsen.timeseries.data.uncertainty;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.ValueUncertaintyCharacteristics;
import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.IValueUncertainty;
import com.github.TKnudsen.ComplexDataObject.data.uncertainty.range.IValueUncertaintyRange;
import com.github.TKnudsen.ComplexDataObject.model.tools.MathFunctions;
import com.github.TKnudsen.timeseries.data.ITimeSeries;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;

/**
 * 
 * timeSeries <br>
 * <br>
 * 
 * Prepares the results of value uncertainty calculations for the visualization
 * of value uncertainties. <br>
 * <br>
 * 
 * In general, data structures related to value uncertainty are generalized to
 * to time series.
 *
 * Copyright: (c) 2016-2018 Juergen Bernard,
 * https://github.com/TKnudsen/timeSeries<br>
 * <br>
 * 
 */
public class ValueUncertaintyConversionTools {

	// UNIVARIATE OPERATIONS
	/**
	 * Converts an uncertainty time series into a general time series. The default
	 * uncertainty value is used (getAmount()).
	 * 
	 * @param uncertainties
	 * @return
	 */
	public static ITimeSeries<Double> computeAmounts(ITimeSeries<? extends IValueUncertainty> uncertainties) {
		List<Long> timeStamps = uncertainties.getTimestamps();
		List<Double> values = new ArrayList<>();

		for (Long timeStamp : timeStamps)
			values.add(uncertainties.getValue(timeStamp, false).getAmount());

		return new TimeSeriesUnivariate(timeStamps, values);
	}

	/**
	 * Converts an uncertainty time series into a general time series. The upper
	 * bounds of the value uncertainty ranges are used).
	 * 
	 * @param uncertainties
	 * @return
	 */
	public static ITimeSeries<Double> computeUpperBound(ITimeSeries<? extends IValueUncertaintyRange> uncertainties) {
		List<Long> timeStamps = uncertainties.getTimestamps();
		List<Double> values = new ArrayList<>();

		for (Long timeStamp : timeStamps)
			values.add(uncertainties.getValue(timeStamp, false).getUpperBound());

		return new TimeSeriesUnivariate(timeStamps, values);
	}

	/**
	 * Converts an uncertainty time series into a general time series. The lower
	 * bounds of the value uncertainty ranges are used).
	 * 
	 * @param uncertainties
	 * @return
	 */
	public static ITimeSeries<Double> computeLowerBound(ITimeSeries<? extends IValueUncertaintyRange> uncertainties) {
		List<Long> timeStamps = uncertainties.getTimestamps();
		List<Double> values = new ArrayList<>();

		for (Long timeStamp : timeStamps)
			values.add(uncertainties.getValue(timeStamp, false).getLowerBound());

		return new TimeSeriesUnivariate(timeStamps, values);
	}

	// MULTIVARIATE OPERATIONS - UNCERTAINTY AMOUNT

	/**
	 * Converts a multivariate uncertainty time series into an 1D general time
	 * series. The default uncertainty values are used (getAmount()).
	 * 
	 * @param uncertainties
	 * @return
	 */
	public static ITimeSeries<Double> computeAggregationWithAmounts(
			ITimeSeries<List<IValueUncertainty>> uncertainties) {
		List<Long> timeStamps = uncertainties.getTimestamps();
		List<Double> values = new ArrayList<>();

		for (Long timeStamp : timeStamps) {
			List<? extends IValueUncertainty> vus = uncertainties.getValue(timeStamp, false);
			List<Double> v = new ArrayList<>();
			for (IValueUncertainty vu : vus)
				v.add(vu.getAmount());
			values.add(MathFunctions.getMean(v));
		}

		return new TimeSeriesUnivariate(timeStamps, values);

	}

	/**
	 * transforms value uncertainty information for every time stamp and dimension
	 * into a MultivariateTimeSeries with Double values.
	 * 
	 * @param uncertainties
	 * @return
	 */
	public static ITimeSeriesMultivariate computeAmountsForEachDimension(
			ITimeSeries<List<IValueUncertainty>> uncertainties) {

		return ValueUncertaintyConversionToolsSupport.computeCharacteristicsForEachDimension(uncertainties,
				ValueUncertaintyCharacteristics.AMOUNT);
	}

	/**
	 * Converts a multivariate uncertainty time series into an double
	 * representation. Either each dimension is treated individually or an aggregate
	 * of all dimensions is calculated.
	 * 
	 * @param uncertainties
	 * @return
	 */
	public static List<ITimeSeries<Double>> computeAmountsTimeSeriesList(
			ITimeSeries<List<IValueUncertainty>> uncertainties, boolean aggregateDimensions) {

		List<ITimeSeries<Double>> uncertaintyTimeSeriesList = new ArrayList<>();
		if (aggregateDimensions)
			uncertaintyTimeSeriesList.add(computeAggregationWithAmounts(uncertainties));
		else
			uncertaintyTimeSeriesList.addAll(computeAmountsForEachDimension(uncertainties).getTimeSeriesList());

		return uncertaintyTimeSeriesList;
	}

	// MULTIVARIATE OPERATIONS - UNCERTAINTY RANGE

	/**
	 * transforms value uncertainty information for every time stamp and dimension
	 * into a MultivariateTimeSeries with Double values.
	 * 
	 * @param uncertainties
	 * @return
	 */
	public static ITimeSeriesMultivariate computeLowerBoundForEachDimension(
			ITimeSeries<List<IValueUncertainty>> uncertainties) {

		return ValueUncertaintyConversionToolsSupport.computeCharacteristicsForEachDimension(uncertainties,
				ValueUncertaintyCharacteristics.LOWERBOUND);
	}

	/**
	 * transforms value uncertainty information for every time stamp and dimension
	 * into a MultivariateTimeSeries with Double values.
	 * 
	 * @param uncertainties
	 * @return
	 */
	public static ITimeSeriesMultivariate computeUpperBoundForEachDimension(
			ITimeSeries<List<IValueUncertainty>> uncertainties) {

		return ValueUncertaintyConversionToolsSupport.computeCharacteristicsForEachDimension(uncertainties,
				ValueUncertaintyCharacteristics.UPPERBOUND);
	}

	/**
	 * returns three time series (lower bound, amount, upper bound) for each
	 * dimension. The outer list contains the dimensions (multivariate time series),
	 * i.e., the list of the input time series.
	 * 
	 * @return
	 */
	public static List<ITimeSeriesMultivariate> computeRangeUncertaintyTimeSeries(
			ITimeSeries<List<IValueUncertainty>> uncertainties) {

		List<ITimeSeriesMultivariate> output = new ArrayList<>();

		ITimeSeriesMultivariate lowerBound = computeLowerBoundForEachDimension(uncertainties);
		lowerBound.setName(ValueUncertaintyCharacteristics.LOWERBOUND.name());
		output.add(lowerBound);

		ITimeSeriesMultivariate amount = computeAmountsForEachDimension(uncertainties);
		amount.setName(ValueUncertaintyCharacteristics.AMOUNT.name());
		output.add(amount);

		ITimeSeriesMultivariate upperBound = computeUpperBoundForEachDimension(uncertainties);
		upperBound.setName(ValueUncertaintyCharacteristics.UPPERBOUND.name());
		output.add(upperBound);

		return output;
	}
}
