package com.github.TKnudsen.timeseries.operations.segmentation;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.preprocessing.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.TimeSeries;

public interface TimeSeriesSegmenter<T extends TimeSeries<Number>> {

	public List<T> process(List<T> data);

	public DataProcessingCategory getSegmenterClass();
}
