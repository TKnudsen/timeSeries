package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.uncertainty.processing;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.IUncertaintyQuantitative;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;

public class ValueAggregationModificationMeasure
		extends TimeSeriesProcessingUncertaintyMeasure<ITimeSeriesMultivariate, IUncertaintyQuantitative<Integer>> {

	public ValueAggregationModificationMeasure() {
		super();
	}

	@Override
	public String getName() {
		return "ValueAggregationModificationMeasure";
	}

	@Override
	public String getDescription() {
		return "Calculates the sampling rate uncertainty of a given MVTS over time";
	}

	@Override
	public void calculateUncertainty(ITimeSeriesMultivariate originalData, ITimeSeriesMultivariate processedData) {
		// TODO Auto-generated method stub
		throw new NullPointerException(getName() + ": calculateUncertaintyMeasure() not implemented yet.");
	}

}
