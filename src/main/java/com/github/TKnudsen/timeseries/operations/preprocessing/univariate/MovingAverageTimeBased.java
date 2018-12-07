package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.ComplexDataObject.model.weighting.Long.LinearLongWeightingKernel;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * 
 * timeSeries
 *
 * Copyright: (c) 2016-2018 Juergen Bernard,
 * https://github.com/TKnudsen/timeSeries<br>
 * <br>
 * 
 * Moving average with a temporal kernel function. Presumes that the time series
 * is ordered.
 * </p>
 * 
 * @author Juergen Bernard
 * 
 * @version 1.02
 */
public class MovingAverageTimeBased extends TimeSeriesProcessor<ITimeSeriesUnivariate> {

	final LinearLongWeightingKernel kernel;

	private boolean considerFutureValues = false;

	/**
	 * for serialization purposes
	 */
	@SuppressWarnings("unused")
	private MovingAverageTimeBased() {
		this(3, false);
	}

	public MovingAverageTimeBased(long kernelInterval, boolean considerFutureValues) {
		this(new LinearLongWeightingKernel(kernelInterval), considerFutureValues);
	}

	public MovingAverageTimeBased(LinearLongWeightingKernel kernel, boolean considerFutureValues) {
		Objects.requireNonNull(kernel);

		this.kernel = kernel;
		this.considerFutureValues = considerFutureValues;
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_CLEANING;
	}

	@Override
	public void process(List<ITimeSeriesUnivariate> data) {
		if (data == null)
			return;

		for (ITimeSeriesUnivariate timeSeries : data) {
			TimeSeriesTools.calculateMovingAverageTimeSensitive(timeSeries, getKernelInterval(), considerFutureValues);
		}
	}

	public long getKernelInterval() {
		return kernel.getInterval();
	}

	public void setKernelInterval(long kernelInterval) {
		this.kernel.setInterval(kernelInterval);
	}

	public boolean isConsiderFutureValues() {
		return considerFutureValues;
	}

	public void setConsiderFutureValues(boolean considerFutureValues) {
		this.considerFutureValues = considerFutureValues;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesUnivariate>> getAlternativeParameterizations(int count) {
		List<Long> longs = ParameterSupportTools.getAlternativeLongs(kernel.getInterval(), count);

		List<IDataProcessor<ITimeSeriesUnivariate>> processors = new ArrayList<>();
		for (Long i : longs)
			if (i > 0)
				processors.add(new MovingAverageTimeBased(i, considerFutureValues));

		return processors;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof MovingAverageTimeBased))
			return false;

		MovingAverageTimeBased other = (MovingAverageTimeBased) o;

		return other.kernel.getInterval().equals(kernel.getInterval())
				&& other.considerFutureValues == considerFutureValues;
	}
}
