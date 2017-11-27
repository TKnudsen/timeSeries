package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.primitives.TimeQuantization;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

/**
 * <p>
 * Title: LinearInterpolation
 * </p>
 * 
 * <p>
 * Description: interpolates a time series with a given quantization. If a local
 * observed quantization in the time series exceeds the range of the given time
 * interval, new timestamps are added (by means on interpolation).
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public class LinearInterpolation implements ITimeSeriesUnivariatePreprocessor {

	/**
	 * threshold duration when a local interpolation process will be triggered.
	 * Also: quantization of interpolated subsequences.
	 */
	private TimeDuration timeDuration;

	@SuppressWarnings("unused")
	private LinearInterpolation() {
		this(TimeQuantization.SECONDS, 3L);
	}

	public LinearInterpolation(TimeQuantization timeQuantization, long count) {
		this(new TimeDuration(timeQuantization, count));
	}

	public LinearInterpolation(TimeDuration timeDuration) {
		this.timeDuration = timeDuration;
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_CLEANING;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesUnivariate>> getAlternativeParameterizations(int count) {
		List<IDataProcessor<ITimeSeriesUnivariate>> processors = new ArrayList<>();

		long typeFactor = timeDuration.getTypeFactor();

		List<Long> alternativeLongs = ParameterSupportTools.getAlternativeLongs(typeFactor, count);

		for (Long l : alternativeLongs)
			if (l > 0)
				processors.add(new LinearInterpolation(timeDuration.getType(), l));

		return processors;
	}

	@Override
	public void process(List<ITimeSeriesUnivariate> data) {
		if (data == null)
			return;

		for (ITimeSeriesUnivariate timeSeries : data)
			process(timeSeries);
	}

	private void process(ITimeSeriesUnivariate timeSeries) {
		if (timeSeries == null || timeSeries.size() == 0)
			return;

		long previous = timeSeries.getFirstTimestamp();
		long current = previous;
		long quantization = timeDuration.getDuration();

		for (int i = 1; i < timeSeries.size(); i++) {
			current = timeSeries.getTimestamp(i);
			long delta = Math.abs(current - previous);
			if (delta > quantization) {
				double t0 = timeSeries.getTimestamp(i - 1);
				double v0 = timeSeries.getValue(i - 1);
				double t1 = timeSeries.getTimestamp(i);
				double v1 = timeSeries.getValue(i);
				long nextInsert = timeSeries.getTimestamp(i - 1) + quantization;
				while (delta > quantization) {
					double interpolatedValue = v0 + (Math.abs(nextInsert - t0) / Math.abs(t0 - t1) * (v1 - v0));
					timeSeries.insert(nextInsert, interpolatedValue);
					delta -= quantization;
					nextInsert += quantization;
				}
			}

			previous = timeSeries.getTimestamp(i);
		}
	}

	public TimeDuration getTimeDuration() {
		return timeDuration;
	}

	public void setTimeDuration(TimeDuration timeDuration) {
		this.timeDuration = timeDuration;
	}
}
