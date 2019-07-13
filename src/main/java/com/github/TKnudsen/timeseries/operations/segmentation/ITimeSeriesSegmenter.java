package com.github.TKnudsen.timeseries.operations.segmentation;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.ITimeSeries;

/**
 * <p>
 * Title: TimeSeriesSegmenter
 * </p>
 * 
 * <p>
 * Description: interface for time series Segmentation tasks
 * </p>
 * 
 * <p>
 * Copyright: (c) 2016-2017 J�rgen Bernard,
 * https://github.com/TKnudsen/timeSeries
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public interface ITimeSeriesSegmenter<O, T extends ITimeSeries<O>> {

	public List<T> process(List<T> data);
	
	public List<T> process(T data);

	public DataProcessingCategory getPreprocessingCategory();
}
