package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.uncertainty.processing;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.IUncertaintyQuantitative;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.multivariate.TimeSeriesMultivariate;

public class ValueAggregationModificationMeasure extends TimeSeriesProcessingUncertaintyMeasure<TimeSeriesMultivariate, IUncertaintyQuantitative<Integer>> {

	public ValueAggregationModificationMeasure(ITimeSeriesMultivariate originalTimeSeries,
			ITimeSeriesMultivariate processedTimeSeries) {
		super(originalTimeSeries, processedTimeSeries);
		// TODO Auto-generated constructor stub
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
	public void calculateUncertaintyMeasure() {
		// TODO Auto-generated method stub
		
	}

}
