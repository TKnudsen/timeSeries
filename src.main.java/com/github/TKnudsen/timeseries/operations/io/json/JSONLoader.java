package com.github.TKnudsen.timeseries.operations.io.json;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.multivariate.TimeSeriesMultivariateLabeled;
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
 * @version 1.02
 */
public class JSONLoader {

	public static ITimeSeriesUnivariate loadConfigsFromString(String json) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesObjectMapper();

		ITimeSeriesUnivariate readValue;
		try {
			readValue = mapper.readValue(json, TimeSeriesUnivariate.class);
			return readValue;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ITimeSeriesUnivariate loadFromString(String json) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesObjectMapper();

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
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesObjectMapper();

		ITimeSeriesUnivariate readValue;
		try {
			readValue = mapper.readValue(new File(file), TimeSeriesUnivariate.class);
			return readValue;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ITimeSeriesUnivariate loadFromFile(String file) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesObjectMapper();

		ITimeSeriesUnivariate readValue;
		try {
			readValue = mapper.readValue(new File(file), TimeSeriesUnivariate.class);
			return readValue;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	public static ITimeSeriesMultivariate loadTSMVFromFile(String file) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesObjectMapper();

		ITimeSeriesMultivariate readValue;
		try {
			readValue = mapper.readValue(new File(file), ITimeSeriesMultivariate.class);
			return readValue;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	public static ITimeSeriesMultivariate loadTSMVFromString(String json) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesObjectMapper();

		ITimeSeriesMultivariate readValue;
		try {
			readValue = mapper.readValue(json, ITimeSeriesMultivariate.class);
			return readValue;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static TimeSeriesMultivariateLabeled loadTSMVLabeledFromFile(String file) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesObjectMapper();

		TimeSeriesMultivariateLabeled readValue;
		try {
			readValue = mapper.readValue(new File(file), TimeSeriesMultivariateLabeled.class);
			return readValue;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	public static TimeSeriesMultivariateLabeled loadTSMVLabeledFromString(String json) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesObjectMapper();

		TimeSeriesMultivariateLabeled readValue;
		try {
			readValue = mapper.readValue(json, TimeSeriesMultivariateLabeled.class);
			return readValue;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
