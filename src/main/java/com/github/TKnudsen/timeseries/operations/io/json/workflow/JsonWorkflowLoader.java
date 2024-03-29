package com.github.TKnudsen.timeseries.operations.io.json.workflow;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.io.json.ObjectMapperFactory;
import com.github.TKnudsen.timeseries.operations.workflow.ITimeSeriesDataMiningWorkflow;
import com.github.TKnudsen.timeseries.operations.workflow.multivariate.TimeSeriesMultivariateDataMiningWorkflow;
import com.github.TKnudsen.timeseries.operations.workflow.univariate.TimeSeriesUnivariateDataMiningWorkflow;

/**
 * <p>
 * Title: JsonWorkflowLoader
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public class JsonWorkflowLoader {

	public static ITimeSeriesDataMiningWorkflow<ITimeSeriesUnivariate> loadWorkflowFromString(String jsonString) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesWorkflowObjectMapper();

		TimeSeriesUnivariateDataMiningWorkflow readValue;
		try {
			readValue = mapper.readValue(jsonString, TimeSeriesUnivariateDataMiningWorkflow.class);
			return readValue;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ITimeSeriesDataMiningWorkflow<ITimeSeriesUnivariate> loadWorkflowFromFile(String file) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesWorkflowObjectMapper();

		TimeSeriesUnivariateDataMiningWorkflow readValue;
		try {
			readValue = mapper.readValue(new File(file), TimeSeriesUnivariateDataMiningWorkflow.class);
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
	 * @return time series workflow
	 */
	public static List<ITimeSeriesDataMiningWorkflow<ITimeSeriesUnivariate>> loadWorkflowListFromFile(String file) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesWorkflowObjectMapper();

		List<ITimeSeriesDataMiningWorkflow<ITimeSeriesUnivariate>> readValue;
		try {
			readValue = mapper.readValue(new File(file),
					new TypeReference<List<ITimeSeriesDataMiningWorkflow<ITimeSeriesUnivariate>>>() {
					});
			return readValue;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ITimeSeriesDataMiningWorkflow<ITimeSeriesMultivariate> loadMultivariateWorkflowFromString(
			String jsonString) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesWorkflowObjectMapper();

		TimeSeriesMultivariateDataMiningWorkflow readValue;
		try {
			readValue = mapper.readValue(jsonString, TimeSeriesMultivariateDataMiningWorkflow.class);
			return readValue;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ITimeSeriesDataMiningWorkflow<ITimeSeriesMultivariate> loadMultivariateWorkflowFromFile(String file) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesWorkflowObjectMapper();

		TimeSeriesMultivariateDataMiningWorkflow readValue;
		try {
			readValue = mapper.readValue(new File(file), TimeSeriesMultivariateDataMiningWorkflow.class);
			return readValue;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
