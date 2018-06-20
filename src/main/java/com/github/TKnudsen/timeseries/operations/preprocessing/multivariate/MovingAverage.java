package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.NumericalUncertainty;
import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.IProcessingUncertaintyMeasure;
import com.github.TKnudsen.ComplexDataObject.model.processors.IUncertainDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.weighting.Integer.IIntegerWeightingKernel;
import com.github.TKnudsen.ComplexDataObject.model.weighting.Integer.LinearIndexWeightingKernel;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.uncertainty.processing.RelativeValueDomainModificationMeasure;

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
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class MovingAverage extends DimensionBasedTimeSeriesMultivariateProcessor implements IUncertainDataProcessor<ITimeSeriesMultivariate, NumericalUncertainty> {

	private IIntegerWeightingKernel kernel;
	private boolean considerFutureValues = false;

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
		this.setUnivariateTimeSeriesProcessor(new com.github.TKnudsen.timeseries.operations.preprocessing.univariate.MovingAverage(kernel, considerFutureValues));
	}

	@Override
	public List<IDataProcessor<ITimeSeriesMultivariate>> getAlternativeParameterizations(int count) {
		List<Integer> integers = ParameterSupportTools.getAlternativeIntegers(kernel.getInterval(), count);

		List<IDataProcessor<ITimeSeriesMultivariate>> processors = new ArrayList<>();
		for (Integer i : integers)
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

	@Override
	public IProcessingUncertaintyMeasure<ITimeSeriesMultivariate, NumericalUncertainty> getUncertaintyMeasure(
			ITimeSeriesMultivariate originalTS, ITimeSeriesMultivariate processedTS) {
		return new RelativeValueDomainModificationMeasure(originalTS, processedTS);
	}

}
