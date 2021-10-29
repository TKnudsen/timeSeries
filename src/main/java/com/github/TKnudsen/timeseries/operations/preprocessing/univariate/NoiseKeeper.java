package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.ComplexDataObject.model.weighting.Integer.IIntegerWeightingKernel;
import com.github.TKnudsen.ComplexDataObject.model.weighting.Integer.LinearIndexWeightingKernel;
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
 * removes the signal of a time series and preserves the noise. the signal is
 * calculated with a moving average routine.
 *
 * 
 * @author Juergen Bernard
 * 
 * @version 1.01
 */
public class NoiseKeeper extends TimeSeriesProcessor<ITimeSeriesUnivariate> {

	private final MovingAverage movingAverage;

	/**
	 * for serialization purposes
	 */
	@SuppressWarnings("unused")
	private NoiseKeeper() {
		this(3, false);
	}

	public NoiseKeeper(int kernelInterval, boolean considerFutureValues) {
		this(new LinearIndexWeightingKernel(kernelInterval), considerFutureValues);
	}

	public NoiseKeeper(IIntegerWeightingKernel kernel, boolean considerFutureValues) {
		Objects.requireNonNull(kernel);

		this.movingAverage = new MovingAverage(kernel, considerFutureValues);
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

			ITimeSeriesUnivariate cloned = TimeSeriesTools.cloneTimeSeries(timeSeries);
			TimeSeriesTools.calculateMovingAverageTimeSensitive(cloned, getKernelInterval(), isConsiderFutureValues());

			List<Double> retValues = new ArrayList<>();

			for (int i = 0; i < timeSeries.size(); i++)
				retValues.add(timeSeries.getValue(i) - cloned.getValue(i));

			for (int i = 0; i < timeSeries.size(); i++)
				timeSeries.replaceValue(i, retValues.get(i));
		}
	}

	public int getKernelInterval() {
		return movingAverage.getKernelInterval();
	}

	public void setKernelInterval(int kernelInterval) {
		this.movingAverage.setKernelInterval(kernelInterval);
	}

	public boolean isConsiderFutureValues() {
		return this.movingAverage.isConsiderFutureValues();
	}

	public void setConsiderFutureValues(boolean considerFutureValues) {
		movingAverage.setConsiderFutureValues(considerFutureValues);
	}

	@Override
	public List<IDataProcessor<ITimeSeriesUnivariate>> getAlternativeParameterizations(int count) {
		List<Integer> ints = ParameterSupportTools.getAlternativeIntegers(getKernelInterval(), count);

		List<IDataProcessor<ITimeSeriesUnivariate>> processors = new ArrayList<>();
		for (Integer i : ints)
			processors.add(new NoiseKeeper(i, isConsiderFutureValues()));

		return processors;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof NoiseKeeper))
			return false;

		NoiseKeeper other = (NoiseKeeper) o;

		return other.getKernelInterval() == getKernelInterval()
				&& other.isConsiderFutureValues() == isConsiderFutureValues();
	}
}
