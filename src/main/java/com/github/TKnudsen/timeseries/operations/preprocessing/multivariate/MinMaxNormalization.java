package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.Arrays;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.NumericalUncertainty;
import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.IProcessingUncertaintyMeasure;
import com.github.TKnudsen.ComplexDataObject.model.processors.IUncertainDataProcessor;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.uncertainty.processing.RelativeValueDomainModificationMeasure;

/**
 * <p>
 * Title: MinMaxNormalization
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class MinMaxNormalization extends DimensionBasedTimeSeriesMultivariateProcessor implements IUncertainDataProcessor<ITimeSeriesMultivariate, NumericalUncertainty> {

	private boolean globalMinMax;

	public MinMaxNormalization(boolean globalMinMax) {
		this.globalMinMax = globalMinMax;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesMultivariate>> getAlternativeParameterizations(int count) {
		return Arrays.asList(new MinMaxNormalization(!globalMinMax));
	}

	@Override
	protected void initializeUnivariateTimeSeriesProcessor() {
		setUnivariateTimeSeriesProcessor(new com.github.TKnudsen.timeseries.operations.preprocessing.univariate.normalization.MinMaxNormalization(globalMinMax));
	}

	public boolean isGlobalMinMax() {
		return globalMinMax;
	}

	@Override
	public IProcessingUncertaintyMeasure<ITimeSeriesMultivariate, NumericalUncertainty> getUncertaintyMeasure(
			ITimeSeriesMultivariate originalTS, ITimeSeriesMultivariate processedTS) {
		return new RelativeValueDomainModificationMeasure(originalTS, processedTS);
	}
}
