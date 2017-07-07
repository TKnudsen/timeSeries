package com.github.TKnudsen.timeseries.operations.transformations.descriptors;

import com.github.TKnudsen.ComplexDataObject.model.transformations.descriptors.numericalFeatures.INumericFeatureVectorDescriptor;
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
 * @version 1.04
 */
public interface ITimeSeriesDescriptor<TS extends ITimeSeries<?>> extends INumericFeatureVectorDescriptor<TS> {

}
