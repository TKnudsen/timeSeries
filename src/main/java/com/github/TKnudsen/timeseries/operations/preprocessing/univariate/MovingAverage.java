package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.ComplexDataObject.model.weighting.Integer.IIntegerWeightingKernel;
import com.github.TKnudsen.ComplexDataObject.model.weighting.Integer.LinearIndexWeightingKernel;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;

/**
 * <p>
 * Title: MovingAverage
 * </p>
 * 
 * <p>
 * Description: Moving average preprocessing routine. A kernel function handles
 * the range parameter.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.05
 */
public class MovingAverage extends TimeSeriesProcessor<ITimeSeriesUnivariate> {

	private IIntegerWeightingKernel kernel;
	private boolean considerFutureValues = false;

	/**
	 * for serialization purposes
	 */
	@SuppressWarnings("unused")
	private MovingAverage() {
		this(3, false);
	}

	public MovingAverage(int kernelInterval, boolean considerFutureValues) {
		this(new LinearIndexWeightingKernel(kernelInterval), considerFutureValues);
	}

	public MovingAverage(IIntegerWeightingKernel kernel, boolean considerFutureValues) {
		if (kernel == null)
			throw new NullPointerException("MovingAverage: kernel was null");

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
			List<Double> retValues = new ArrayList<>();

			for (int i = 0; i < timeSeries.size(); i++) {
				kernel.setReference(i);
				double values = 0;

				double weights = 0;
				for (int k = Math.max(0, i - kernel.getInterval().intValue()); k < Math
						.min(i + kernel.getInterval().intValue() + 1, timeSeries.size()); k++)
					if (!considerFutureValues && k > i)
						break;
					else if (!Double.isNaN(timeSeries.getValue(k))) {
						double w = kernel.getWeight(k);
						values += timeSeries.getValue(k) * w;
						weights += w;
					}

				if (weights <= 0)
					retValues.add(Double.NaN);
				else {
					values /= weights;
					retValues.add(values);
				}
			}

			for (int i = 0; i < timeSeries.size(); i++)
				timeSeries.replaceValue(i, retValues.get(i));
		}
	}

	public int getKernelInterval() {
		return kernel.getInterval();
	}

	public void setKernelInterval(int kernelInterval) {
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
		List<Integer> integers = ParameterSupportTools.getAlternativeIntegers(kernel.getInterval(), count);

		List<IDataProcessor<ITimeSeriesUnivariate>> processors = new ArrayList<>();
		for (Integer i : integers)
			processors.add(new MovingAverage(i, considerFutureValues));

		return processors;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof MovingAverage))
			return false;

		MovingAverage other = (MovingAverage) o;

		return other.kernel.getInterval().equals(kernel.getInterval())
				&& other.considerFutureValues == considerFutureValues;
	}

}
