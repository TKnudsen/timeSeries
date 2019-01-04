package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.dimensionBased;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.weighting.Long.LinearLongWeightingKernel;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;

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
public class MovingAverageTimeBased extends DimensionBasedTimeSeriesMultivariateProcessor {

	final LinearLongWeightingKernel kernel;

	private boolean considerFutureValues = false;

	/**
	 * for serialization/reflection purposes.
	 */
	@SuppressWarnings("unused")
	private MovingAverageTimeBased() {
		this(10L, true);
	}

	public MovingAverageTimeBased(long kernelInterval, boolean considerFutureValues) {
		this(new LinearLongWeightingKernel(kernelInterval), considerFutureValues);
	}

	public MovingAverageTimeBased(LinearLongWeightingKernel kernel, boolean considerFutureValues) {
		if (kernel == null)
			throw new NullPointerException("MovingAverageTimeBased: kernel was null");

		this.kernel = kernel;
		this.considerFutureValues = considerFutureValues;
	}

	@Override
	protected void initializeUnivariateTimeSeriesProcessor() {
		this.setTimeSeriesProcessor(
				new com.github.TKnudsen.timeseries.operations.preprocessing.univariate.MovingAverageTimeBased(kernel,
						considerFutureValues));
	}

	@Override
	public List<IDataProcessor<ITimeSeriesMultivariate>> getAlternativeParameterizations(int count) {
		List<Long> longs = ParameterSupportTools.getAlternativeLongs(kernel.getInterval(), count);

		List<IDataProcessor<ITimeSeriesMultivariate>> processors = new ArrayList<>();
		for (Long i : longs)
			if (i > 1)
				processors.add(new MovingAverageTimeBased(i, considerFutureValues));

		return processors;
	}

	public long getKernelInterval() {
		return kernel.getInterval();
	}

	public void setKernelInterval(long kernelInterval) {
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
