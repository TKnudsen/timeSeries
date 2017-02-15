package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.preprocessing.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.preprocessing.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.preprocessing.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;

/**
 * <p>
 * Title: Sampling
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class Sampling implements ITimeSeriesMultivariatePreprocessor {

	private int elements;

	public Sampling(int elements) {
		this.elements = elements;
	}

	@Override
	public void process(List<ITimeSeriesMultivariate> data) {
		if (data == null)
			return;

		if (data.size() == 0)
			return;

		for (int i = 0; i < data.size(); i++) {
			if (data.get(i) == null)
				continue;
			if (data.get(i).isEmpty())
				continue;
			process(data.get(i));
		}
	}

	private void process(ITimeSeriesMultivariate data) {
		for (int i = data.size() - 1; i > 0; i--) {
			if (i % elements == 0) {
			} else
				data.removeTimeValue(i);
		}
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_REDUCTION;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesMultivariate>> getAlternativeParameterizations(int count) {
		List<Integer> integers = ParameterSupportTools.getAlternativeIntegers(elements, count);

		List<IDataProcessor<ITimeSeriesMultivariate>> processors = new ArrayList<>();
		for (Integer i : integers)
			processors.add(new Sampling(i));

		return processors;
	}

}