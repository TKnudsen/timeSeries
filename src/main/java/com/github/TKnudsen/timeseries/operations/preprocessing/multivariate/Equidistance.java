package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;
import com.github.TKnudsen.timeseries.operations.tools.TimeQuantizationTools;
import com.github.TKnudsen.timeseries.operations.tools.TimeQuantizationTools.TimeStampQuantizationTuple;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesMultivariateTools;

public class Equidistance extends TimeSeriesProcessor<ITimeSeriesMultivariate> {

	private boolean allowExtrapolatedTemporalBorders;

	/**
	 * defines whether or not the greatest common divisor is used for guessing
	 * between differnet quantitizations of equal applicability.
	 */
	private boolean greatestCommonDivisor;

	/**
	 * defines whether or not a quantization from external is used. otherwise the
	 * routine guesses most meaningful quantizations on demand.
	 */
	private boolean useExternalQuantization;

	private long quantization;
	private long quantizationAppliedLast = -1;

	private boolean startAtFirstTimeStamp;

	public Equidistance(boolean allowExtrapolatedTemporalBorders, boolean greatestCommonDivisor, long quantization,
			boolean startAtFirstTimeStamp) {
		this(allowExtrapolatedTemporalBorders, greatestCommonDivisor, true, quantization, startAtFirstTimeStamp);
	}

	public Equidistance(boolean allowExtrapolatedTemporalBorders, boolean greatestCommonDivisor,
			boolean useExternalQuantization, long quantization, boolean startAtFirstTimeStamp) {
		this.allowExtrapolatedTemporalBorders = allowExtrapolatedTemporalBorders;
		this.greatestCommonDivisor = greatestCommonDivisor;
		this.startAtFirstTimeStamp = startAtFirstTimeStamp;
		this.useExternalQuantization = useExternalQuantization;
		this.quantization = quantization;
	}

	@Override
	public void process(List<ITimeSeriesMultivariate> timeSeriesList) {
		if (timeSeriesList == null)
			return;
		if (timeSeriesList.size() == 0)
			return;
		for (int i = 0; i < timeSeriesList.size(); i++) {
			if (timeSeriesList.get(i) == null)
				continue;
			if (timeSeriesList.get(i).isEmpty())
				continue;
			process(timeSeriesList.get(i));
		}
	}

	/**
	 * 
	 * @param timeSeries
	 */
	private void process(ITimeSeriesMultivariate timeSeries) {

		List<Long> timeStamps = timeSeries.getTimestamps();
		List<Long> quantizationList = TimeQuantizationTools.getQuantizationList(timeStamps);

		long startTimeStampToUse = timeSeries.getFirstTimestamp();

		long quantization = this.quantization;

		List<Long> quantizationGuesses = null;
		if (!useExternalQuantization) {
			quantizationGuesses = TimeQuantizationTools.guessQuantization(quantizationList, greatestCommonDivisor);
			quantization = quantizationGuesses.get(0);
		} else {
			quantizationGuesses = Arrays.asList(quantization);
		}

		if (!startAtFirstTimeStamp) {
			TimeStampQuantizationTuple<Long, Long> timeStampQuantizationTuple = TimeQuantizationTools
					.guessStartTimeStamp(quantizationGuesses, timeStamps);
			startTimeStampToUse = timeStampQuantizationTuple.timeStamp;
			quantization = timeStampQuantizationTuple.quantization;
		}

		System.out.println("quantization: " + quantization);
		System.out.println("start timestamp: " + startTimeStampToUse);

		applyEquidistance(timeSeries, startTimeStampToUse, quantization);
		this.quantizationAppliedLast = quantization;
	}

	/**
	 * 
	 * @param timeSeries
	 * @param startTimeStamp
	 * @param quantization
	 */
	private void applyEquidistance(ITimeSeriesMultivariate timeSeries, Long startTimeStamp, Long quantization) {

		long timeStamp = startTimeStamp;
		int i = 1;

		while (timeStamp < timeSeries.getLastTimestamp()) {

			timeStamp = startTimeStamp + i * quantization;
			long prevTimeStamp = timeStamp - quantization;

			insertEntry(timeSeries, timeStamp);
			removeEntriesBetween(timeSeries, prevTimeStamp, timeStamp);

			i++;
		}

		timeStamp = startTimeStamp;
		i = 1;

		while (timeStamp > timeSeries.getFirstTimestamp()) {

			timeStamp = startTimeStamp - i * quantization;
			long nextTimeStamp = timeStamp + quantization;

			insertEntry(timeSeries, timeStamp);
			removeEntriesBetween(timeSeries, timeStamp, nextTimeStamp);

			i++;
		}
	}

	/**
	 * 
	 * @param timeSeries
	 * @param timeStamp
	 */
	private void insertEntry(ITimeSeriesMultivariate timeSeries, Long timeStamp) {

		List<Long> timeStamps = timeSeries.getTimestamps();

		if (!timeStamps.contains(timeStamp)) {

			if (allowExtrapolatedTemporalBorders) {
				if (timeStamp > timeSeries.getLastTimestamp()) {
					insertNewBorderEntry(timeSeries, timeStamp, timeSeries.getLastTimestamp());
				} else if (timeStamp < timeSeries.getFirstTimestamp()) {
					insertNewBorderEntry(timeSeries, timeStamp, timeSeries.getFirstTimestamp());
				} else {
					insertInterpolatedEntry(timeSeries, timeStamp);
				}
			} else {
				if (timeStamp < timeSeries.getFirstTimestamp())
					return;
				else if (timeStamp > timeSeries.getLastTimestamp())
					return;
				insertInterpolatedEntry(timeSeries, timeStamp);
			}
		}
	}

	private void insertNewBorderEntry(ITimeSeriesMultivariate timeSeries, Long timeStamp, Long timeStampWithValues) {

		List<ITimeSeriesUnivariate> timeSeriesUnivariateList = timeSeries.getTimeSeriesList();

		List<Double> values = new ArrayList<Double>();

		for (ITimeSeriesUnivariate timeSeriesUnivariate : timeSeriesUnivariateList) {
			values.add(timeSeriesUnivariate.getValue(timeStampWithValues, false));
		}

		if (values.size() == timeSeriesUnivariateList.size()) {
			timeSeries.insert(timeStamp, values);
		}
	}

	private void insertInterpolatedEntry(ITimeSeriesMultivariate timeSeries, Long timeStamp) {
		List<Double> values = null;

		// extrapolation with constant value of bounds.
		if (timeStamp < timeSeries.getFirstTimestamp())
			values = timeSeries.getValue(timeSeries.getFirstTimestamp(), false);
		else if (timeStamp > timeSeries.getLastTimestamp())
			values = timeSeries.getValue(timeSeries.getLastTimestamp(), false);
		else
			values = TimeSeriesMultivariateTools.getInterpolatedValue(timeSeries, timeStamp);
		timeSeries.insert(timeStamp, values);

		// List<ITimeSeriesUnivariate> timeSeriesUnivariateList =
		// timeSeries.getTimeSeriesList();
		//
		// List<Long> nearestTimeStampNeighbors = TimeSeriesTools
		// .getNearestTimeStampNeighbors(timeSeriesUnivariateList.get(0), timeStamp);
		//
		// List<Double> interpolatedValues = new ArrayList<Double>();
		//
		// if (nearestTimeStampNeighbors.size() == 2) {
		//
		// Long neighborPrev = nearestTimeStampNeighbors.get(0);
		// Long neighborNext =
		// nearestTimeStampNeighbors.get(nearestTimeStampNeighbors.size() - 1);
		//
		// for (ITimeSeriesUnivariate timeSeriesUnivariate : timeSeriesUnivariateList) {
		// double value = TimeSeriesTools.getInterpolatedValue(timeSeriesUnivariate,
		// neighborPrev, neighborNext,
		// timeStamp);
		// interpolatedValues.add(value);
		// }
		// }
		//
		// if (interpolatedValues.size() == timeSeriesUnivariateList.size()) {
		// timeSeries.insert(timeStamp, interpolatedValues);
		// }
	}

	/**
	 * removes time value pairs within an interval defined by two time stamps. The
	 * time stamps remain. Requires a SORTED time series.
	 * 
	 * @param timeSeries
	 * @param timeStamp1
	 * @param timeStamp2
	 */
	private void removeEntriesBetween(ITimeSeriesMultivariate timeSeries, Long timeStamp1, Long timeStamp2) {
		int index = timeSeries.findByDate(timeStamp1, false);
		for (int i = index + 1; i < timeSeries.size(); i++)
			if (timeSeries.getTimestamp(i) < timeStamp2)
				timeSeries.removeTimeValue(i--);
			else
				break;
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_CLEANING;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesMultivariate>> getAlternativeParameterizations(int count) {
		List<IDataProcessor<ITimeSeriesMultivariate>> processors = new ArrayList<>();

		long q = quantization;
		if (!useExternalQuantization)
			q = quantizationAppliedLast;
		if (q == -1L)
			return processors;

		List<Long> quantizations = ParameterSupportTools.getAlternativeLongs(q, count);

		for (Long l : quantizations)
			processors.add(new Equidistance(false, false, true, l, true));
		// processors.add(new Equidistance(false, false, true, 1000L + i * 1000L,
		// true));

		return processors;
	}

	public boolean isExtrapolatedTemporalBordersAllowed() {
		return allowExtrapolatedTemporalBorders;
	}

	public boolean isGreatestCommonDivisor() {
		return greatestCommonDivisor;
	}

	public boolean isCustomQuantizationUsed() {
		return useExternalQuantization;
	}

	public long getQuantization() {
		return quantization;
	}

	public void setQuantization(long quantization) {
		this.quantization = quantization;

		this.useExternalQuantization = true;
	}

	public boolean startAtFirstTimeStamp() {
		return startAtFirstTimeStamp;
	}
}
