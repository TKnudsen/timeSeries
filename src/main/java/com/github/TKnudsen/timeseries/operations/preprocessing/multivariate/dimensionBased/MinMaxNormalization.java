package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.dimensionBased;

import java.util.Arrays;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;

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
 * Copyright: Copyright (c) 2017-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.04
 */
public class MinMaxNormalization extends DimensionBasedTimeSeriesMultivariateProcessor {

	private boolean globalMinMax;

	/**
	 * for serialization/reflection purposes
	 */
	@SuppressWarnings("unused")
	private MinMaxNormalization() {
		this(false);
	}

	public MinMaxNormalization(boolean globalMinMax) {
		this.globalMinMax = globalMinMax;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesMultivariate>> getAlternativeParameterizations(int count) {
		return Arrays.asList(new MinMaxNormalization(!globalMinMax));
	}

	@Override
	protected void initializeUnivariateTimeSeriesProcessor() {
		setTimeSeriesProcessor(
				new com.github.TKnudsen.timeseries.operations.preprocessing.univariate.normalization.MinMaxNormalization(
						globalMinMax));
	}

	public boolean isGlobalMinMax() {
		return globalMinMax;
	}

}
