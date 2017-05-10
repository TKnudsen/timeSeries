package com.github.TKnudsen.timeseries.operations.io.json.workflow;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.TKnudsen.timeseries.operations.workflow.univariate.ITimeSeriesUnivariateDataMiningWorkflow;

public class JsonWorkflowWriter {

	public static String writeWorkflowToString(ITimeSeriesUnivariateDataMiningWorkflow wf) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

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
		ObjectMapper mapper = new ObjectMapper();
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

		try {
			mapper.writeValue(new File(file), wf);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}

	public static void writeWorkflowListToFile(List<ITimeSeriesUnivariateDataMiningWorkflow> wfs, String file) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

		try {
			mapper.writeValue(new File(file), wfs);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}

}
