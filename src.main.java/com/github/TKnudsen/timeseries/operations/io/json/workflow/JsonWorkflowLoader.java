package com.github.TKnudsen.timeseries.operations.io.json.workflow;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.TKnudsen.timeseries.operations.io.json.ObjectMapperFactory;
import com.github.TKnudsen.timeseries.operations.workflow.multivariate.ITimeSeriesMultivariateDataMiningWorkflow;
import com.github.TKnudsen.timeseries.operations.workflow.univariate.ITimeSeriesUnivariateDataMiningWorkflow;
import com.github.TKnudsen.timeseries.operations.workflow.univariate.TimeSeriesUnivariateDataMiningWorkflow;

public class JsonWorkflowLoader {

	public static ITimeSeriesUnivariateDataMiningWorkflow loadWorkflowFromString(String jsonString) {
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

	public static ITimeSeriesUnivariateDataMiningWorkflow loadWorkflowFromFile(String file) {
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

	public static List<ITimeSeriesUnivariateDataMiningWorkflow> loadWorkflowListFromFile(String file) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesWorkflowObjectMapper();

		List<ITimeSeriesUnivariateDataMiningWorkflow> readValue;
		try {
			readValue = mapper.readValue(new File(file), new TypeReference<List<TimeSeriesUnivariateDataMiningWorkflow>>() {
			});
			return readValue;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ITimeSeriesMultivariateDataMiningWorkflow loadMultivariateWorkflowFromString(String jsonString) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesWorkflowObjectMapper();

		ITimeSeriesMultivariateDataMiningWorkflow readValue;
		try {
			readValue = mapper.readValue(jsonString, ITimeSeriesMultivariateDataMiningWorkflow.class);
			return readValue;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ITimeSeriesMultivariateDataMiningWorkflow loadMultivariateWorkflowFromFile(String file) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesWorkflowObjectMapper();

		ITimeSeriesMultivariateDataMiningWorkflow readValue;
		try {
			readValue = mapper.readValue(new File(file), ITimeSeriesMultivariateDataMiningWorkflow.class);
			return readValue;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
