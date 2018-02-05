package com.github.TKnudsen.timeseries.operations.distance;

import com.github.TKnudsen.ComplexDataObject.model.distanceMeasure.IDistanceMeasure;
import com.github.TKnudsen.timeseries.data.ITimeSeries;

/**
 * <p>
 * Title: ITimeSeriesDistanceMeasure
 * </p>
 * 
 * <p>
 * Description: Interface for all time series-based distance measures.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public interface ITimeSeriesDistanceMeasure<TS extends ITimeSeries<?>> extends IDistanceMeasure<TS> {

}
