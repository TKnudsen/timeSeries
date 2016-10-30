package com.github.TKnudsen.timeseries.operations.preprocessing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.preprocessing.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

public class MovingAverage implements IPreprocessingRoutineUnivariate {

	private int kernel = 3;
	private boolean considerFutureValues = false;

	public MovingAverage(int kernel, boolean considerFutureValues) {
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
				List<Double> tmpValues = new LinkedList<Double>();

				for (int k = Math.max(0, i - kernel); k < Math.min(i + kernel, timeSeries.size()); k++)
					if (!considerFutureValues && k > i)
						break;
					else if (!Double.isNaN(timeSeries.getValue(k)))
						tmpValues.add(timeSeries.getValue(k));

				if (tmpValues.size() == 0)
					retValues.add(Double.NaN);
				else {
					Double d = 0.0;
					for (Double tmp : tmpValues)
						d += tmp;
					retValues.add(d / (double) tmpValues.size());
				}
			}

			for (int i = 0; i < timeSeries.size(); i++)
				timeSeries.replaceValue(i, retValues.get(i));
		}
	}

	public int getKernel() {
		return kernel;
	}

	public void setKernel(int kernel) {
		this.kernel = kernel;
	}

	public boolean isConsiderFutureValues() {
		return considerFutureValues;
	}

	public void setConsiderFutureValues(boolean considerFutureValues) {
		this.considerFutureValues = considerFutureValues;
	}

}
