package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.NumericalUncertainty;
import com.github.TKnudsen.ComplexDataObject.model.processors.IProcessingUncertaintyMeasure;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.dimensionBased.ITimeSeriesMultivariatePreprocessor;
import com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.uncertainty.processing.RelativeValueDomainModificationMeasure;

/**
 * <p>
 * Title: TimeSeriesMultivariatetimeSeriesProcessor
 * </p>
 * 
 * <p>
 * Description: Basic class for all MultivariateTimeSeries Processors.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public abstract class TimeSeriesMultivariatetimeSeriesProcessor implements ITimeSeriesMultivariatePreprocessor {

	@Override
	public IProcessingUncertaintyMeasure<ITimeSeriesMultivariate, NumericalUncertainty> getUncertaintyMeasure(
			ITimeSeriesMultivariate originalTS, ITimeSeriesMultivariate processedTS) {
		return new RelativeValueDomainModificationMeasure(originalTS, processedTS);
	}
}
