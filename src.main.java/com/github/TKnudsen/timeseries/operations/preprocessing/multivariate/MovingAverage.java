package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.preprocessing.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.preprocessing.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.preprocessing.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.ComplexDataObject.model.weighting.Integer.IIntegerWeightingKernel;
import com.github.TKnudsen.ComplexDataObject.model.weighting.Integer.LinearIndexWeightingKernel;
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
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class MovingAverage implements ITimeSeriesMultivariatePreprocessor {

	private IIntegerWeightingKernel kernel;
	private boolean considerFutureValues = false;

	com.github.TKnudsen.timeseries.operations.preprocessing.univariate.MovingAverage movingAverageUnivariate;

	public MovingAverage(int kernelInterval, boolean considerFutureValues) {
		this.kernel = new LinearIndexWeightingKernel(kernelInterval);
		this.considerFutureValues = considerFutureValues;

		this.movingAverageUnivariate = new com.github.TKnudsen.timeseries.operations.preprocessing.univariate.MovingAverage(kernel, considerFutureValues);
	}

	public MovingAverage(IIntegerWeightingKernel kernel, boolean considerFutureValues) {
		this.kernel = kernel;
		this.considerFutureValues = considerFutureValues;

		this.movingAverageUnivariate = new com.github.TKnudsen.timeseries.operations.preprocessing.univariate.MovingAverage(kernel, considerFutureValues);
	}

	@Override
	public void process(List<ITimeSeriesMultivariate> data) {
		if (data == null)
			return;

		for (ITimeSeriesMultivariate timeSeriesMultivariate : data) {
			if (timeSeriesMultivariate == null)
				continue;

			List<String> attributeNames = timeSeriesMultivariate.getAttributeNames();
			for (String attribute : attributeNames)
				movingAverageUnivariate.process(new ArrayList<>(Arrays.asList(timeSeriesMultivariate.getTimeSeries(attribute))));
		}
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_CLEANING;
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
	}

	public boolean isConsiderFutureValues() {
		return considerFutureValues;
	}

	public void setConsiderFutureValues(boolean considerFutureValues) {
		this.considerFutureValues = considerFutureValues;
	}
}
