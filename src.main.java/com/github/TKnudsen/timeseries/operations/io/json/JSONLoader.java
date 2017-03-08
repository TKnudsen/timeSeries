package com.github.TKnudsen.timeseries.operations.io.json;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;

/**
 * <p>
 * Title: JSONLoader
 * </p>
 * 
 * <p>
 * Description: loads time series objects stored as JSON
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.0
 */
public class JSONLoader {

	public static ITimeSeriesUnivariate loadConfigsFromString(String json) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

		ITimeSeriesUnivariate readValue;
		try {
			readValue = mapper.readValue(json, TimeSeriesUnivariate.class);
			return readValue;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ITimeSeriesUnivariate loadConfigsFromFile(String file) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

		ITimeSeriesUnivariate readValue;
		try {
			readValue = mapper.readValue(new File(file), TimeSeriesUnivariate.class);
			return readValue;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
