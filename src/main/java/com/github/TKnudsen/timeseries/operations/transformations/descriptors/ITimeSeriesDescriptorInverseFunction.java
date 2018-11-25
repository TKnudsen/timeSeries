package com.github.TKnudsen.timeseries.operations.transformations.descriptors;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeatureVector;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

/**
 * <p>
 * Title: ITimeSeriesDescriptorInverseFunction
 * </p>
 * 
 * <p>
 * Description: inverts the function f(TS)->FV as good as possible. Useful to
 * assess the accuracy of descriptors visually.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public interface ITimeSeriesDescriptorInverseFunction {

	public ITimeSeriesUnivariate invertFunction(NumericalFeatureVector featureVector);
}