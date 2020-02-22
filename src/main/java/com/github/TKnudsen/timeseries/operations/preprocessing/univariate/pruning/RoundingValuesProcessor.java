package com.github.TKnudsen.timeseries.operations.preprocessing.univariate.pruning;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.ComplexDataObject.model.tools.MathFunctions;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;

/**
 * Rounds the value domain of a time series by the pre-defined number of
 * digits/decimals.
 * 
 * The rounding operation is MathFunctions.round(value, decimals).
 * 
 * Useful, e.g., in database-IO scenarios where many digits cause unnecessary
 * traffic.
 *
 */
public class RoundingValuesProcessor extends TimeSeriesProcessor<ITimeSeriesUnivariate> {

	private int decimals;

	@SuppressWarnings("unused")
	private RoundingValuesProcessor() {
		this.decimals = 3;
	}

	public RoundingValuesProcessor(int decimals) {
		this.decimals = decimals;
	}

	@Override
	public void process(List<ITimeSeriesUnivariate> data) {
		for (ITimeSeriesUnivariate timeSeries : data)
			if (timeSeries != null)
				for (int i = 0; i < timeSeries.size(); i++)
					timeSeries.replaceValue(i, MathFunctions.round(timeSeries.getValue(i), decimals));
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_REDUCTION;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesUnivariate>> getAlternativeParameterizations(int count) {
		List<IDataProcessor<ITimeSeriesUnivariate>> processors = new ArrayList<>();

		List<Integer> alternativeInts = ParameterSupportTools.getAlternativeIntegers(decimals, count);

		for (Integer integer : alternativeInts)
			if (integer > 0)
				processors.add(new RoundingValuesProcessor(integer));

		return processors;
	}

}
