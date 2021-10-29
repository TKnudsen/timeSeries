package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.weighting.Integer.IIntegerWeightingKernel;
import com.github.TKnudsen.ComplexDataObject.model.weighting.Integer.LinearIndexWeightingKernel;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.dimensionBased.DimensionBasedTimeSeriesMultivariateProcessor;

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
 * @version 1.03
 */
public class NoiseKeeper extends DimensionBasedTimeSeriesMultivariateProcessor {

	final IIntegerWeightingKernel kernel;

	private boolean considerFutureValues = false;

	/**
	 * for serialization/reflection purposes.
	 */
	@SuppressWarnings("unused")
	private NoiseKeeper() {
		this(3, true);
	}

	public NoiseKeeper(int kernelInterval, boolean considerFutureValues) {
		this(new LinearIndexWeightingKernel(kernelInterval), considerFutureValues);
	}

	public NoiseKeeper(IIntegerWeightingKernel kernel, boolean considerFutureValues) {
		if (kernel == null)
			throw new NullPointerException("MovingAverageTimeBased: kernel was null");

		this.kernel = kernel;
		this.considerFutureValues = considerFutureValues;
	}

	@Override
	protected void initializeUnivariateTimeSeriesProcessor() {
		this.setTimeSeriesProcessor(new com.github.TKnudsen.timeseries.operations.preprocessing.univariate.NoiseKeeper(
				kernel, considerFutureValues));
	}

	@Override
	public List<IDataProcessor<ITimeSeriesMultivariate>> getAlternativeParameterizations(int count) {
		List<Integer> ints = ParameterSupportTools.getAlternativeIntegers(kernel.getInterval(), count);

		List<IDataProcessor<ITimeSeriesMultivariate>> processors = new ArrayList<>();
		for (Integer i : ints)
			processors.add(new NoiseKeeper(i, considerFutureValues));

		return processors;
	}

	public int getKernelInterval() {
		return kernel.getInterval();
	}

	public void setKernelInterval(int kernelInterval) {
		this.kernel.setInterval(kernelInterval);

		initializeUnivariateTimeSeriesProcessor();
	}

	public boolean isConsiderFutureValues() {
		return considerFutureValues;
	}

	public void setConsiderFutureValues(boolean considerFutureValues) {
		this.considerFutureValues = considerFutureValues;

		initializeUnivariateTimeSeriesProcessor();
	}

}
