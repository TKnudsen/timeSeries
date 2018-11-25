package com.github.TKnudsen.timeseries.operations.preprocessing.uncertaintyMeasures;

import com.github.TKnudsen.timeseries.data.ITimeSeries;
import com.github.TKnudsen.timeseries.data.uncertainty.ITimeSeriesValueUncertainty;

public interface ITimeSeriesValueUncertaintyMeasure<TS extends ITimeSeries<?>, VU> {

	public ITimeSeriesValueUncertainty<VU> compute(TS originalData, TS processedData);
}
