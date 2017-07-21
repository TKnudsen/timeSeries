package com.github.TKnudsen.timeseries.operations.transformations.featureExtraction.multivariate;

/**
 * <p>
 * Title: ValueVarianceFeatureExtractor
 * </p>
 * 
 * <p>
 * Description: Extracts a feature for every multivariate time series based on
 * the variance information of individual dimensions.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class ValueVarianceFeatureExtractor extends DimensionBasedTimeSeriesMultivariateFeatureExtractor {

	@Override
	protected void initializeUnivariateTimeSeriesFeatureExtractor() {
		setUnivariateTimeSeriesFeatureExtractor(new com.github.TKnudsen.timeseries.operations.transformations.featureExtraction.ValueVarianceFeatureExtractor());
	}

	@Override
	public String getName() {
		return "Variance";
	}

}
