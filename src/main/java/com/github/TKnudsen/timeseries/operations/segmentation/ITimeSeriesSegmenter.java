package com.github.TKnudsen.timeseries.operations.segmentation;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.ITimeSeries;

/**
 * <p>
 * Interface for time series segmentation tasks.
 * </p>
 *
 * @version 1.02
 * @since 2016
 */
public interface ITimeSeriesSegmenter<O, T extends ITimeSeries<O>> {

	public List<T> process(List<T> data);
	
	public List<T> process(T data);

	public DataProcessingCategory getPreprocessingCategory();
}
