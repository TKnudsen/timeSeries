package com.github.TKnudsen.timeseries.operations.io.json.workflow;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.TKnudsen.ComplexDataObject.model.transformations.descriptors.numericalFeatures.INumericFeatureVectorDescriptor;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.io.json.ObjectMapperFactory;
import com.github.TKnudsen.timeseries.operations.workflow.ITimeSeriesDataMiningWorkflow;
import com.github.TKnudsen.timeseries.operations.workflow.multivariate.TimeSeriesMultivariateDataMiningWorkflow;
import com.github.TKnudsen.timeseries.operations.workflow.univariate.TimeSeriesUnivariateDataMiningWorkflow;

public class JsonWorkflowLoader {

	public static ITimeSeriesDataMiningWorkflow<ITimeSeriesUnivariate, INumericFeatureVectorDescriptor<ITimeSeriesUnivariate>> loadWorkflowFromString(
			String jsonString) {
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

	public static ITimeSeriesDataMiningWorkflow<ITimeSeriesUnivariate, INumericFeatureVectorDescriptor<ITimeSeriesUnivariate>> loadWorkflowFromFile(
			String file) {
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

	public static List<ITimeSeriesDataMiningWorkflow<ITimeSeriesUnivariate, INumericFeatureVectorDescriptor<ITimeSeriesUnivariate>>> loadWorkflowListFromFile(
			String file) {
		ObjectMapper mapper = ObjectMapperFactory.getTimeSeriesWorkflowObjectMapper();

		List<ITimeSeriesDataMiningWorkflow<ITimeSeriesUnivariate, INumericFeatureVectorDescriptor<ITimeSeriesUnivariate>>> readValue;
		try {
			readValue = mapper.readValue(new File(file),
					new TypeReference<List<TimeSeriesUnivariateDataMiningWorkflow>>() {
					});
			return readValue;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ITimeSeriesDataMiningWorkflow<ITimeSeriesMultivariate, INumericFeatureVectorDescriptor<ITimeSeriesMultivariate>> loadMultivariateWorkflowFromString(
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

	public static ITimeSeriesDataMiningWorkflow<ITimeSeriesMultivariate, INumericFeatureVectorDescriptor<ITimeSeriesMultivariate>> loadMultivariateWorkflowFromFile(
			String file) {
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
