package com.github.TKnudsen.timeseries.operations.io.json.processors;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;

/**
 * <p>
 * Title: JSONProcessorLoader
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
public class JSONProcessorLoader {

	public static TimeSeriesProcessor<ITimeSeriesUnivariate> loadKonfigs(String jsonString) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

		TimeSeriesProcessor<ITimeSeriesUnivariate> readValue;
		try {
			readValue = mapper.readValue(jsonString, TimeSeriesProcessor.class);
			return readValue;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
