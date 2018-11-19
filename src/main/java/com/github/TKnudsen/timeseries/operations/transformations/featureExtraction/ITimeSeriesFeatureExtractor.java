package com.github.TKnudsen.timeseries.operations.transformations.featureExtraction;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeature;
import com.github.TKnudsen.ComplexDataObject.model.transformations.featureExtraction.IFeatureExtractor;

import com.github.TKnudsen.timeseries.data.ITimeSeries;

/**
 * <p>
 * Title: ITimeSeriesFeatureExtractor
 * </p>
 * 
 * <p>
 * Description: Basic interface for feature extractors from time series data
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public interface ITimeSeriesFeatureExtractor<TS extends ITimeSeries<?>> extends IFeatureExtractor<TS, NumericalFeature> {

}
