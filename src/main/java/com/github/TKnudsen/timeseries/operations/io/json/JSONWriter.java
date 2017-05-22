package com.github.TKnudsen.timeseries.operations.io.json;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.TKnudsen.timeseries.data.ITimeSeries;
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
 * @version 1.02
 */
public class JSONWriter {

	// public static String writeToString(ITimeSeriesUnivariate ts) {
	// ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesObjectMapper();
	//
	// String writeValueAsString;
	// try {
	// writeValueAsString = mapper.writeValueAsString(ts);
	// return writeValueAsString;
	// } catch (JsonProcessingException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// public static void writeToFile(ITimeSeriesUnivariate ts, String fileName)
	// {
	// ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesObjectMapper();
	// mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
	// mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	//
	// // create dirs when necessary
	// File file = new File(fileName);
	// if (file.getParentFile() != null)
	// file.getParentFile().mkdirs();
	//
	// try {
	// mapper.writeValue(file, ts);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// return;
	// }
	//
	// public static String writeToString(ITimeSeriesMultivariate timeSeriesMV)
	// {
	// ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesObjectMapper();
	//
	// String writeValueAsString;
	// try {
	// writeValueAsString = mapper.writeValueAsString(timeSeriesMV);
	// return writeValueAsString;
	// } catch (JsonProcessingException e) {
	// e.printStackTrace();
	// }
	// return null;
	//
	// }
	//
	// public static void writeToFile(ITimeSeriesMultivariate timeSeriesMV,
	// String fileMV) {
	// ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesObjectMapper();
	// mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
	// mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	//
	// // create dirs when necessary
	// File file = new File(fileMV);
	// if (file.getParentFile() != null)
	// file.getParentFile().mkdirs();
	//
	// try {
	// mapper.writeValue(file, timeSeriesMV);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// return;
	//
	// }

	public static String writeToString(ITimeSeries ts) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesObjectMapper();

		String writeValueAsString;
		try {
			writeValueAsString = mapper.writeValueAsString(ts);
			return writeValueAsString;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void writeToFile(ITimeSeries ts, String fileName) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesObjectMapper();
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

		// create dirs when necessary
		File file = new File(fileName);
		if (file.getParentFile() != null)
			file.getParentFile().mkdirs();

		try {
			mapper.writeValue(file, ts);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}

	public static void writeTSListToFile(List<ITimeSeriesUnivariate> tsList, String fileName) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesObjectMapper();

		// create dirs when necessary
		File file = new File(fileName);
		if (file.getParentFile() != null)
			file.getParentFile().mkdirs();

		try {
			mapper.writeValue(file, tsList);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}
}
