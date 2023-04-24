package com.github.TKnudsen.timeseries.operations.io.json.timeSeries;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.multivariate.TimeSeriesMultivariateLabeledWithEventsIntervalsAndDurations;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.io.json.ObjectMapperFactory;

/**
 * <p>
 * Title: JSONTimeSeriesLoader
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
 * @version 1.03
 */
public class JSONTimeSeriesLoader {

	public static ITimeSeriesUnivariate loadFromString(String json) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesObjectMapper();

		ITimeSeriesUnivariate readValue;
		try {
			readValue = mapper.readValue(json, ITimeSeriesUnivariate.class);
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
			readValue = mapper.readValue(new File(file), ITimeSeriesUnivariate.class);
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

	/**
	 * needs testing whether an interface can be defined as a type reference
	 * 
	 * @param file file
	 * @return time series
	 */
	public static List<ITimeSeriesUnivariate> loadTSListFromFile(String file) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesObjectMapper();

		List<ITimeSeriesUnivariate> readValue;
		try {
			readValue = mapper.readValue(new File(file), new TypeReference<List<ITimeSeriesUnivariate>>() {
			});
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

	public static TimeSeriesMultivariateLabeledWithEventsIntervalsAndDurations loadTSMVLabeledFromFile(String file) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesObjectMapper();

		TimeSeriesMultivariateLabeledWithEventsIntervalsAndDurations readValue;
		try {
			readValue = mapper.readValue(new File(file),
					TimeSeriesMultivariateLabeledWithEventsIntervalsAndDurations.class);
			return readValue;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static TimeSeriesMultivariateLabeledWithEventsIntervalsAndDurations loadTSMVLabeledFromString(String json) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesObjectMapper();

		TimeSeriesMultivariateLabeledWithEventsIntervalsAndDurations readValue;
		try {
			readValue = mapper.readValue(json, TimeSeriesMultivariateLabeledWithEventsIntervalsAndDurations.class);
			return readValue;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
