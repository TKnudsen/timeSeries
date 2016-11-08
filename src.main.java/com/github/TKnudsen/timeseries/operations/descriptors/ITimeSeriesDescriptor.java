package com.github.TKnudsen.timeseries.operations.descriptors;

import com.github.TKnudsen.ComplexDataObject.model.descriptors.INumericFeatureVectorDescriptor;
import com.github.TKnudsen.timeseries.data.ITimeSeries;

/**
 * <p>
 * Title: ITimeSeriesDescriptor
 * </p>
 * 
 * <p>
 * Description: Basic time series descriptor interface
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public interface ITimeSeriesDescriptor<TS extends ITimeSeries<Double>> extends INumericFeatureVectorDescriptor<TS> {

}
