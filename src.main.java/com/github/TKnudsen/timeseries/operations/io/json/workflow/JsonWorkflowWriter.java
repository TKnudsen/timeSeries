package com.github.TKnudsen.timeseries.operations.io.json.workflow;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.TKnudsen.timeseries.operations.io.json.ObjectMapperFactory;
import com.github.TKnudsen.timeseries.operations.workflow.multivariate.ITimeSeriesMultivariateDataMiningWorkflow;
import com.github.TKnudsen.timeseries.operations.workflow.univariate.ITimeSeriesUnivariateDataMiningWorkflow;

public class JsonWorkflowWriter {

	public static String writeWorkflowToString(ITimeSeriesUnivariateDataMiningWorkflow wf) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesWorkflowObjectMapper();

		String writeValueAsString;
		try {
			writeValueAsString = mapper.writeValueAsString(wf);
			return writeValueAsString;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void writeWorkflowToFile(ITimeSeriesUnivariateDataMiningWorkflow wf, String file) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesWorkflowObjectMapper();
		try {
			mapper.writeValue(new File(file), wf);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}
	
	public static String writeMultivariateWorkflowToString(ITimeSeriesMultivariateDataMiningWorkflow wf) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesWorkflowObjectMapper();

		String writeValueAsString;
		try {
			writeValueAsString = mapper.writeValueAsString(wf);
			return writeValueAsString;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void writeMultivariateWorkflowToFile(ITimeSeriesMultivariateDataMiningWorkflow wf, String file) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesWorkflowObjectMapper();
		try {
			mapper.writeValue(new File(file), wf);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}

	public static void writeWorkflowListToFile(List<ITimeSeriesUnivariateDataMiningWorkflow> wfs, String file) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesWorkflowObjectMapper();

		try {
			mapper.writeValue(new File(file), wfs);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}

}
