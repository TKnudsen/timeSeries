package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.Date;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.preprocessing.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.ITimeSeriesPreprocessorUnivariate;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * <p>
 * Title: TemporalQuantization
 * </p>
 * 
 * <p>
 * Description: Provides temporal quantization for a given time series.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class TemporalQuantization implements ITimeSeriesPreprocessorUnivariate {

	private TimeDuration quantization;
	private TimeDuration maximumAllowedGap;

	public TemporalQuantization(TimeDuration quantization) {
		if (quantization == null || quantization.getDuration() == 0)
			throw new IllegalArgumentException("TemporalQuantization: illegal quantization definition.");

		this.quantization = quantization;
		this.maximumAllowedGap = null;
	}

	public TemporalQuantization(TimeDuration quantization, TimeDuration maximumAllowedGap) {
		if (quantization == null || quantization.getDuration() == 0)
			throw new IllegalArgumentException("TemporalQuantization: illegal quantization definition.");

		this.quantization = quantization;
		this.maximumAllowedGap = maximumAllowedGap;
	}

	protected void process(ITimeSeriesUnivariate timeSeries) {
		Date date = new Date();
		System.out.println(date + ", " + date.getTime() + ": TemporalQuantization.process()");

		if (timeSeries == null)
			return;

		if (maximumAllowedGap != null)
			checkForGaps(timeSeries, maximumAllowedGap);

		// remove missing values that do not fit into the targeted quantization
		Long targetTimeStamp = timeSeries.getFirstTimestamp();
		for (int i = 0; i < timeSeries.size(); i++) {
			Long currentTimestamp = timeSeries.getTimestamp(i);
			if (currentTimestamp.equals(targetTimeStamp))
				continue;
			else if (currentTimestamp < targetTimeStamp && Double.isNaN(timeSeries.getValue(i))) {
				timeSeries.removeTimeValue(i);
				i--;
			}
		}

		targetTimeStamp = timeSeries.getFirstTimestamp();
		Long currentTimestamp;

		for (int i = 0; i < timeSeries.size(); i++) {
			currentTimestamp = timeSeries.getTimestamp(i);

			if (currentTimestamp.equals(targetTimeStamp)) {
				targetTimeStamp += quantization.getDuration();
				continue;
			} else if (currentTimestamp < targetTimeStamp) {
				// check whether the next time stamp is also < targetTimeStamp
				if (i < timeSeries.size() - 1) {
					Long nextTimeStamp = timeSeries.getTimestamp(i + 1);
					if (nextTimeStamp < targetTimeStamp) {
						timeSeries.removeTimeValue(i);
						i--;
						continue;
					} else {
						// interpolate between last time stamp before...
						// and next time stamp after the target
						double value = TimeSeriesTools.getInterpolatedValue(timeSeries, currentTimestamp, nextTimeStamp, targetTimeStamp);
						timeSeries.removeTimeValue(i);
						timeSeries.insert(targetTimeStamp, value);
						targetTimeStamp += quantization.getDuration();
						continue;
					}
				} else {
					// end of time series. beyond last target within series
					timeSeries.removeTimeValue(i);
					i--;
					continue;
				}
			} else {
				// temporal gap > quantization. feed in new time stamps..
				double value = TimeSeriesTools.getInterpolatedValue(timeSeries, timeSeries.getTimestamp(i - 1), currentTimestamp, targetTimeStamp);
				timeSeries.insert(targetTimeStamp, value);
				targetTimeStamp += quantization.getDuration();
				continue;
			}
		}
	}

	private void checkForGaps(ITimeSeriesUnivariate timeSeries, TimeDuration maximumAllowedGap) {
		if (maximumAllowedGap == null || maximumAllowedGap.getDuration() == 0)
			return;

		Long referenceDuration = maximumAllowedGap.getDuration();

		List<Long> timestamps = timeSeries.getTimestamps();

		Long lastTimeStamp = null;
		for (Long timeStamp : timestamps) {
			if (lastTimeStamp != null) {
				if (lastTimeStamp > timeStamp)
					throw new IllegalArgumentException("TemporalQuantization.checkForGaps: given time series not sorted.");

				if (timeStamp - lastTimeStamp > referenceDuration)
					throw new IllegalArgumentException("TemporalQuantization.checkForGaps: given time series has a too large gap");
			}

			lastTimeStamp = timeStamp;
		}
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_CLEANING;
	}

	@Override
	public void process(List<ITimeSeriesUnivariate> data) {
		for (ITimeSeriesUnivariate timeSeries : data)
			process(timeSeries);
	}

	@Override
	public String toString() {
		return "TemporalQuantization: quantization: " + quantization.toString() + ", maximumAllowedGap" + maximumAllowedGap.toString();
	}

	public TimeDuration getQuantization() {
		return quantization;
	}

	public void setQuantization(TimeDuration quantization) {
		this.quantization = quantization;
	}

	public TimeDuration getMaximumAllowedGap() {
		return maximumAllowedGap;
	}

	public void setMaximumAllowedGap(TimeDuration maximumAllowedGap) {
		this.maximumAllowedGap = maximumAllowedGap;
	}
}