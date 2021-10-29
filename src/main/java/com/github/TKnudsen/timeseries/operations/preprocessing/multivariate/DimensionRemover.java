package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;

/**
 * 
 * TimeSeries
 *
 * Copyright: (c) 2015-2018 Juergen Bernard,
 * https://github.com/TKnudsen/TimeSeries<br>
 * 
 * DimensionRemover is a pre-processing routine that removes the dimension with
 * a given name
 *
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class DimensionRemover extends TimeSeriesProcessor<ITimeSeriesMultivariate> {

	private final String dimensionName;

	@SuppressWarnings("unused")
	private DimensionRemover() {
		this(null);
	}

	public DimensionRemover(String dimensionName) {
		this.dimensionName = dimensionName;
	}

	@Override
	public void process(List<ITimeSeriesMultivariate> data) {

		if (data.isEmpty())
			throw new IllegalStateException("List<TimeSeries> is empty");

		for (int i = 0; i < data.size(); i++) {
			if (data.get(i) == null)
				throw new IllegalStateException("TimeSeries is null");
			if (data.get(i).isEmpty())
				throw new IllegalStateException("TimeSeries is empty");
			process(data.get(i));
		}
	}

	private void process(ITimeSeriesMultivariate data) {
		data.removeTimeSeries(getDimensionName());
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_REDUCTION;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesMultivariate>> getAlternativeParameterizations(int count) {
		List<IDataProcessor<ITimeSeriesMultivariate>> alternatives = new ArrayList<>();

		return alternatives;
	}

	public String getDimensionName() {
		return dimensionName;
	}

}