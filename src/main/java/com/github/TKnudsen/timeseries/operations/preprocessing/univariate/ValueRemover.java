package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.ComplexDataObject.model.tools.MathFunctions;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;

/**
 * <p>
 * Removes all time-value pairs from a univariate time series whose value
 * matches a configured indicator value.
 * </p>
 *
 * <p>
 * The comparison uses {@link MathFunctions#compareDoubles}, which treats
 * {@code NaN == NaN} as {@code true}. A {@code null} value is always removed
 * regardless of the indicator.
 * </p>
 *
 * <p>
 * Subclasses may extend this class to provide named convenience constructors
 * for common indicator values. See {@link MissingValueRemover} for an example.
 * </p>
 *
 * @version 2.0 revised in February 2026
 * @since 2015
 */
public class ValueRemover extends TimeSeriesProcessor<ITimeSeriesUnivariate> {

	private final double valueIndicator;

	/**
	 * Creates a remover for the given indicator value.
	 *
	 * @param valueIndicator the value whose matching time-value pairs will be
	 *                       removed; use {@code Double.NaN} to remove NaN entries
	 */
	public ValueRemover(double valueIndicator) {
		this.valueIndicator = valueIndicator;
	}

	/**
	 * Returns the indicator value used to identify entries for removal.
	 *
	 * @return the value indicator
	 */
	public double getValueIndicator() {
		return valueIndicator;
	}

	/**
	 * Processes all time series in the list, removing entries whose value matches
	 * the configured indicator. Null entries in the list are not permitted. Empty
	 * lists and empty series are silently skipped.
	 *
	 * @param data the list of time series to process; must not be null
	 * @throws NullPointerException if {@code data} is null or contains null
	 *                              entries
	 */
	@Override
	public void process(List<ITimeSeriesUnivariate> data) {
		Objects.requireNonNull(data, "data must not be null");
		for (ITimeSeriesUnivariate ts : data) {
			Objects.requireNonNull(ts, "TimeSeries in list must not be null");
			if (!ts.isEmpty())
				processSeries(ts);
		}
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_CLEANING;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesUnivariate>> getAlternativeParameterizations(int count) {
		return Collections.emptyList();
	}

	// -------------------------------------------------------------------------
	// Internal
	// -------------------------------------------------------------------------

	private void processSeries(ITimeSeriesUnivariate ts) {
		synchronized (ts) {
			for (int i = 0; i < ts.size(); i++) {
				Double value = ts.getValue(i);
				if (value == null
						|| MathFunctions.compareDoubles(valueIndicator, value.doubleValue())) {
					ts.removeTimeValue(i);
					i--;
				}
			}
		}
	}
}