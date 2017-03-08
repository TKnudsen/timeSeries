package com.github.TKnudsen.timeseries.operations.io.json;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

/**
 * <p>
 * Title: JSONWriter
 * </p>
 * 
 * <p>
 * Description: stores time series objects as JSON
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.0
 */
public class JSONWriter {

	public static String writeToString(ITimeSeriesUnivariate ts) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

		String writeValueAsString;
		try {
			writeValueAsString = mapper.writeValueAsString(ts);
			return writeValueAsString;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void writeToFile(ITimeSeriesUnivariate ts, String file) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

		try {
			mapper.writeValue(new File(file), ts);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}
}
