package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.dimensionBased;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.primitives.TimeQuantization;

/**
 * <p>
 * Title: LinearInterpolation
 * </p>
 * 
 * <p>
 * Description: Linear interpolation preprocessing routine for multivariate time
 * series. If a local observed quantization in the time series exceeds the range
 * of the given time interval, new timestamps are added (by means on
 * interpolation).
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.04
 */
public class LinearInterpolation extends DimensionBasedTimeSeriesMultivariateProcessor {

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
	protected void initializeUnivariateTimeSeriesProcessor() {
		this.setTimeSeriesProcessor(
				new com.github.TKnudsen.timeseries.operations.preprocessing.univariate.LinearInterpolation(
						timeDuration));
	}

	@Override
	public List<IDataProcessor<ITimeSeriesMultivariate>> getAlternativeParameterizations(int count) {
		List<IDataProcessor<ITimeSeriesMultivariate>> processors = new ArrayList<>();

		long typeFactor = timeDuration.getTypeFactor();

		List<Long> alternativeLongs = ParameterSupportTools.getAlternativeLongs(typeFactor, count);

		for (Long l : alternativeLongs)
			if (l > 0)
				processors.add(new LinearInterpolation(timeDuration.getType(), l));

		return processors;
	}

	public TimeDuration getTimeDuration() {
		return timeDuration;
	}

}
