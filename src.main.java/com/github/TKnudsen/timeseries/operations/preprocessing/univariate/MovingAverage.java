package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.preprocessing.DataProcessingCategory;
import com.github.TKnudsen.ComplexDataObject.model.weighting.Integer.IIntegerWeightingKernel;
import com.github.TKnudsen.ComplexDataObject.model.weighting.Integer.LinearIndexWeightingKernel;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.ITimeSeriesPreprocessorUnivariate;

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
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public class MovingAverage implements ITimeSeriesPreprocessorUnivariate {

	private IIntegerWeightingKernel kernel;
	private boolean considerFutureValues = false;

	public MovingAverage(int kernelInterval, boolean considerFutureValues) {
		this.kernel = new LinearIndexWeightingKernel(kernelInterval);
		this.considerFutureValues = considerFutureValues;
	}

	public MovingAverage(IIntegerWeightingKernel kernel, boolean considerFutureValues) {
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
				for (int k = Math.max(0, i - kernel.getInterval().intValue()); k < Math.min(i + kernel.getInterval().intValue() + 1, timeSeries.size()); k++)
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
		return getKernelInterval();
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
