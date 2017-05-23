package com.github.TKnudsen.timeseries.operations.distance.timeSeriesUnivariate;

import com.github.TKnudsen.ComplexDataObject.model.distanceMeasure.IIDObjectDistanceMeasure;
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
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public interface ITimeSeriesDistanceMeasure<TS extends ITimeSeries<?>> extends IIDObjectDistanceMeasure<TS> {

}
