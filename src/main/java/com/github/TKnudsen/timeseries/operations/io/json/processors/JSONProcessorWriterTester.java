package com.github.TKnudsen.timeseries.operations.io.json.processors;

import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.ITimeSeriesUnivariatePreprocessor;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.normalization.MinMaxNormalization;

/**
 * <p>
 * Title: JSONProcessorWriterTester
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
public class JSONProcessorWriterTester {

	public static void main(String[] args) {

		String create = JSONProcessorWriter.create(new MinMaxNormalization(true));
		System.out.println(create);

		ITimeSeriesUnivariatePreprocessor loadKonfigs = JSONProcessorLoader.loadKonfigs(create);
		System.out.println(loadKonfigs);
	}

}
