package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.dimensionBased;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.weighting.Integer.IIntegerWeightingKernel;
import com.github.TKnudsen.ComplexDataObject.model.weighting.Integer.LinearIndexWeightingKernel;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;

/**
 * <p>
 * Title: MovingAverage
 * </p>
 * 
 * <p>
 * Description: Moving average preprocessing routine for multivariate time
 * series. A kernel function handles the range parameter.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.06
 */
public class MovingAverage extends DimensionBasedTimeSeriesMultivariateProcessor {

	private IIntegerWeightingKernel kernel;
	private boolean considerFutureValues = false;

	/**
	 * for serialization/reflection purposes.
	 */
	@SuppressWarnings("unused")
	private MovingAverage() {
		this(3, true);
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
	protected void initializeUnivariateTimeSeriesProcessor() {
		this.setTimeSeriesProcessor(
				new com.github.TKnudsen.timeseries.operations.preprocessing.univariate.MovingAverage(kernel,
						considerFutureValues));
	}

	@Override
	public List<IDataProcessor<ITimeSeriesMultivariate>> getAlternativeParameterizations(int count) {
		List<Integer> integers = ParameterSupportTools.getAlternativeIntegers(kernel.getInterval(), count);

		List<IDataProcessor<ITimeSeriesMultivariate>> processors = new ArrayList<>();
		for (Integer i : integers)
			if (i > 1)
				processors.add(new MovingAverage(i, considerFutureValues));

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
