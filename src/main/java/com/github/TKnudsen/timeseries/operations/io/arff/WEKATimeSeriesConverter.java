package com.github.TKnudsen.timeseries.operations.io.arff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.multivariate.TimeSeriesMultivariateFactory;
import com.github.TKnudsen.timeseries.data.multivariate.TimeSeriesMultivariateLabeled;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * <p>
 * Title: WEKATimeSeriesConverter
 * </p>
 * 
 * <p>
 * Description: Tools for the conversion of TimeSeries to Instances.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class WEKATimeSeriesConverter {

	/**
	 * converts a multivariate time series to an Instances object.
	 * 
	 * @param timeSeriesMultivariate
	 * @return
	 */
	public static Instances toInstances(ITimeSeriesMultivariate timeSeriesMultivariate) {
		Instances instances;

		ArrayList<Attribute> attributes = new ArrayList<>();

		attributes.add(new Attribute("TimeStamp"));

		Map<Integer, Attribute> attributeIdices = new HashMap<>();

		for (int i = 0; i < timeSeriesMultivariate.getDimensionality(); i++) {
			String name = timeSeriesMultivariate.getAttributeName(i);
			if (name == null)
				name = ("" + i);

			Attribute attribute = new Attribute(name);
			attributes.add(attribute);
			attributeIdices.put(i, attribute);
		}

		instances = new Instances(timeSeriesMultivariate.getName() + ", " + timeSeriesMultivariate.getDescription(),
				attributes, timeSeriesMultivariate.size());

		for (int t = 0; t < timeSeriesMultivariate.size(); t++) {
			List<Double> values = timeSeriesMultivariate.getValue(t);
			Instance instance = new DenseInstance(values.size() + 1);

			for (int i = 0; i < values.size(); i++) {
				instance.setValue(attributeIdices.get(i), values.get(i));
			}

			instance.setValue(new Attribute("TimeStamp", 0), timeSeriesMultivariate.getTimestamp(t));

			instances.add(instance);
		}

		if (timeSeriesMultivariate instanceof TimeSeriesMultivariateLabeled) {
			List<String> labelsOverTime = ((TimeSeriesMultivariateLabeled) timeSeriesMultivariate)
					.getLabelsForTimeStamps();

			SortedSet<String> labelAlphabet = new TreeSet<>(labelsOverTime);
			Attribute classAttribute = new Attribute("class", new ArrayList<>(labelAlphabet));

			instances.insertAttributeAt(classAttribute, instances.numAttributes());
			instances.setClass(classAttribute);
			instances.setClassIndex(instances.numAttributes() - 1);

			for (int i = 0; i < labelsOverTime.size(); i++) {
				instances.instance(i).setClassValue(labelsOverTime.get(i));
			}

			instances.setClass(classAttribute);
			instances.setClassIndex(instances.numAttributes() - 1);
		}

		return instances;
	}

	/**
	 * converts an Instances object to a multivariate time series, if possible.
	 * 
	 * @param instances
	 * @return
	 */
	public static ITimeSeriesMultivariate toTimeSeriesMultivariate(Instances instances) {

		List<List<Double>> values = new ArrayList<List<Double>>();
		List<Long> timestamps = new ArrayList<Long>();

		long timeStamp = 0;

		for (int i = 0; i < instances.numInstances(); i++) {
			timeStamp = (long) instances.instance(i).toDoubleArray()[0];
			timestamps.add(timeStamp);
		}

		List<Attribute> attributes = new ArrayList<>();
		for (int i = 1; i < instances.numAttributes(); i++) {
			if (instances.attribute(i).isString() || instances.attribute(i).isNominal())
				continue;

			attributes.add(instances.attribute(i));

			List<Double> instanceValues = new ArrayList<Double>();

			for (int j = 0; j < instances.numInstances(); j++)
				instanceValues.add(instances.instance(j).toDoubleArray()[i]);

			values.add(instanceValues);
		}

		// names of time series / attributes / dimensions
		List<String> attributeNames = new ArrayList<>();
		for (int i = 0; i < attributes.size(); i++)
			attributeNames.add(attributes.get(i).name());

		ITimeSeriesMultivariate timeSeries = TimeSeriesMultivariateFactory.createTimeSeriesMultivatiate(timestamps,
				values, Double.NaN, attributeNames);

		// parse name, description
		String relationName = instances.relationName();
		if (relationName != null && relationName.contains(", ")) {
			String name = relationName.substring(0, relationName.indexOf(", "));
			timeSeries.setName(name);
			String description = relationName.substring(relationName.indexOf(", ") + 2, relationName.length());
			timeSeries.setDescription(description);
		} else
			timeSeries.setName(relationName);

		// add labels if class attribute exists
		if (instances.classIndex() >= 0) {
			System.out.println(
					"WEKATimeSeriesConverter.toTimeSeriesMultivariate: class index detected. creating a labeled time series");

			return labeledTimeSeries(instances, timeSeries, instances.classIndex());
		} else {
			for (int i = 0; i < instances.numAttributes(); i++)
				if (instances.attribute(i).name().equals("class") || instances.attribute(i).name().equals("Class")
						|| instances.attribute(i).name().equals("CLASS")) {
					System.out.println(
							"WEKATimeSeriesConverter.toTimeSeriesMultivariate: class attribute detected. creating a labeled time series");

					return labeledTimeSeries(instances, timeSeries, i);
				}
		}

		return timeSeries;
	}

	private static TimeSeriesMultivariateLabeled labeledTimeSeries(Instances instances,
			ITimeSeriesMultivariate timeSeries, int classIndex) {
		List<String> labels = new ArrayList<String>();
		for (int i = 0; i < instances.numInstances(); i++) {
			Instance instance = instances.get(i);
			labels.add(instance.stringValue(classIndex));
		}

		return new TimeSeriesMultivariateLabeled(timeSeries, labels);
	}

}
