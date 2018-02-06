package com.github.TKnudsen.timeseries.operations.io.json.workflow;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.io.json.ObjectMapperFactory;
import com.github.TKnudsen.timeseries.operations.workflow.ITimeSeriesDataMiningWorkflow;

/**
 * <p>
 * Title: JsonWorkflowWriter
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
public class JsonWorkflowWriter {

	public static String writeWorkflowToString(ITimeSeriesDataMiningWorkflow<ITimeSeriesUnivariate> wf) {
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

	public static void writeWorkflowToFile(ITimeSeriesDataMiningWorkflow<ITimeSeriesUnivariate> wf, String file) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesWorkflowObjectMapper();
		try {
			mapper.writeValue(new File(file), wf);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}

	public static String writeMultivariateWorkflowToString(ITimeSeriesDataMiningWorkflow<ITimeSeriesMultivariate> wf) {
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

	public static void writeMultivariateWorkflowToFile(ITimeSeriesDataMiningWorkflow<ITimeSeriesMultivariate> wf,
			String file) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesWorkflowObjectMapper();
		try {
			mapper.writeValue(new File(file), wf);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}

	public static void writeWorkflowListToFile(List<ITimeSeriesDataMiningWorkflow<ITimeSeriesUnivariate>> wfs,
			String file) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesWorkflowObjectMapper();

		try {
			mapper.writeValue(new File(file), wfs);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}

}
