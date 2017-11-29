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
 * @version 1.03
 */
public class ObjectMapperFactory {

	private static ObjectMapper timeSeriesObjectMapper;
	private static ObjectMapper timeSeriesWorkflowObjectMapper;

	/**
	 * retrieves an ObjectMapper for the JSON-IO of timeseries datao
	 * 
	 * @return
	 */
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

	/**
	 * retrieves an ObjectMapper for the JSON-IO of timeseries data mining
	 * workflows
	 * 
	 * @return
	 */
	public static ObjectMapper getTimeSeriesWorkflowObjectMapper() {
		if (timeSeriesWorkflowObjectMapper == null)
			initTimeSeriesWorkflowObjectMapper();

		return timeSeriesWorkflowObjectMapper;
	}

	private static void initTimeSeriesWorkflowObjectMapper() {
		timeSeriesWorkflowObjectMapper = new ObjectMapper();
		timeSeriesWorkflowObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		timeSeriesWorkflowObjectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
	}
}
