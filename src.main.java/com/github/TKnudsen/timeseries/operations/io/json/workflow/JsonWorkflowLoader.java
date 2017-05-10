package com.github.TKnudsen.timeseries.operations.io.json.workflow;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.TKnudsen.timeseries.operations.workflow.univariate.ITimeSeriesUnivariateDataMiningWorkflow;
import com.github.TKnudsen.timeseries.operations.workflow.univariate.TimeSeriesUnivariateDataMiningWorkflow;

public class JsonWorkflowLoader {

	public static ITimeSeriesUnivariateDataMiningWorkflow loadWorkflowFromString(String jsonString) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

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
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

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
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

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

}
