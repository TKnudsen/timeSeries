package com.github.TKnudsen.timeseries.operations.segmentation;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.preprocessing.DataProcessingCategory;
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
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public interface ITimeSeriesSegmenter<T extends ITimeSeries<Number>> {

	public List<T> process(List<T> data);

	public DataProcessingCategory getSegmenterClass();
}
