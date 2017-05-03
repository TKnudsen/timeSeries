package com.github.TKnudsen.timeseries.operations.io.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>
 * Title: ObjectMapperFactory
 * </p>
 * 
 * <p>
 * Description: creates (singleton ObjectMapper)
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public class ObjectMapperFactory {

	private static ObjectMapper timeSeriesObjectMapper;

	public static ObjectMapper getTimeSeriesObjectMapper() {
		if (timeSeriesObjectMapper == null)
			initTimeSeriesObjectMapper();

		return timeSeriesObjectMapper;
	}

	private static void initTimeSeriesObjectMapper() {
		timeSeriesObjectMapper = new ObjectMapper();
		timeSeriesObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		timeSeriesObjectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		
		timeSeriesObjectMapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		timeSeriesObjectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
	}
}
