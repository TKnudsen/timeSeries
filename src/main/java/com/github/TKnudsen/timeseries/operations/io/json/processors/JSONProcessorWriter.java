package com.github.TKnudsen.timeseries.operations.io.json.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.ITimeSeriesUnivariatePreprocessor;

/**
 * <p>
 * Title: JSONProcessorWriter
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
public class JSONProcessorWriter {

	public static String create(ITimeSeriesUnivariatePreprocessor maxNormalization) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

		String writeValueAsString;
		try {
			writeValueAsString = mapper.writeValueAsString(maxNormalization);
			return writeValueAsString;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
