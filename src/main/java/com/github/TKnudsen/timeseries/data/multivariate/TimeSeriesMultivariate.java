package com.github.TKnudsen.timeseries.data.multivariate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.tools.MathFunctions;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

/**
 * <p>
 * Title: TimeSeriesMultivariate
 * </p>
 * 
 * <p>
 * Description: Models a multivariate time series with Double values. Expects
 * the individual timeSeries to have identical time stamps.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public class TimeSeriesMultivariate implements ITimeSeriesMultivariate {

	protected long id;
	private String name;
	private String description;

	private List<ITimeSeriesUnivariate> timeSeriesUnivariateList;
	List<String> attributeNames;
	private List<Double> missingValueIndicators;

	private int dimensionality;

	protected TimeSeriesMultivariate() {
		this.id = MathFunctions.randomLong();
		this.timeSeriesUnivariateList = new ArrayList<>();

	}

	public TimeSeriesMultivariate(List<ITimeSeriesUnivariate> timeSeriesUnivariateList) {
		this.id = MathFunctions.randomLong();
		this.timeSeriesUnivariateList = timeSeriesUnivariateList;

		initialize();
	}

	public TimeSeriesMultivariate(long id, List<ITimeSeriesUnivariate> timeSeriesUnivariateList) {
		this.id = id;
		this.timeSeriesUnivariateList = timeSeriesUnivariateList;

		initialize();
	}

	public TimeSeriesMultivariate(List<ITimeSeriesUnivariate> timeSeriesUnivariateList, List<String> timeSeriesNames) {
		this.id = MathFunctions.randomLong();
		this.timeSeriesUnivariateList = timeSeriesUnivariateList;

		initialize();

		if (timeSeriesNames != null)
			if (timeSeriesUnivariateList.size() != timeSeriesNames.size())
				throw new IllegalArgumentException("TimeSeriesMultivariate: content and attributes have different sizes");

		for (int i = 0; i < timeSeriesUnivariateList.size(); i++)
			if (timeSeriesNames != null && timeSeriesNames.get(i) != null)
				timeSeriesUnivariateList.get(i).setName(timeSeriesNames.get(i));
			else
				timeSeriesUnivariateList.get(i).setName("");
	}

	public TimeSeriesMultivariate(long id, List<ITimeSeriesUnivariate> timeSeriesUnivariateList, List<String> timeSeriesNames) {
		this.id = id;
		this.timeSeriesUnivariateList = timeSeriesUnivariateList;

		initialize();

		if (timeSeriesNames != null)
			if (timeSeriesUnivariateList.size() != timeSeriesNames.size())
				throw new IllegalArgumentException("TimeSeriesMultivariate: content and attributes have different sizes");

		for (int i = 0; i < timeSeriesUnivariateList.size(); i++)
			timeSeriesUnivariateList.get(i).setName(timeSeriesNames.get(i));
	}

	private void initialize() {
		if (timeSeriesUnivariateList == null)
			throw new IllegalArgumentException("TimeSeriesMultivariate: time series null");

		if (timeSeriesUnivariateList.size() == 0)
			throw new IllegalArgumentException("TimeSeriesMultivariate: time series empty");

		long l = getFirstTimeseriesUnivariate().getFirstTimestamp();
		for (int i = 1; i < timeSeriesUnivariateList.size(); i++)
			if (timeSeriesUnivariateList.get(i).getFirstTimestamp() != l)
				throw new IllegalArgumentException("TimeSeriesMultivariate: time series out of sync");

		l = getFirstTimeseriesUnivariate().getLastTimestamp();
		for (int i = 1; i < timeSeriesUnivariateList.size(); i++)
			if (timeSeriesUnivariateList.get(i).getLastTimestamp() != l)
				throw new IllegalArgumentException("TimeSeriesMultivariate: time series out of sync");

		for (int i = 1; i < timeSeriesUnivariateList.size(); i++)
			if (timeSeriesUnivariateList.get(i).size() != size())
				throw new IllegalArgumentException("TimeSeriesMultivariate: time series have different sizes");

		// dimensionality = getFirstTimeseriesUnivariate().size();
		dimensionality = timeSeriesUnivariateList.size();

		missingValueIndicators = new ArrayList<>();
		for (int i = 0; i < timeSeriesUnivariateList.size(); i++)
			missingValueIndicators.add(timeSeriesUnivariateList.get(i).getMissingValueIndicator());

		// init names
		getAttributeNames();
	}

	private ITimeSeriesUnivariate getFirstTimeseriesUnivariate() {
		if (timeSeriesUnivariateList.size() != 0)
			return timeSeriesUnivariateList.get(0);
		throw new IllegalArgumentException("TimeSeriesMultivariate: time series empty");
	}

	@Override
	public int size() {
		return getFirstTimeseriesUnivariate().size();
	}

	@Override
	public boolean isEmpty() {
		return timeSeriesUnivariateList.isEmpty();
	}

	@Override
	public List<Double> getMissingValueIndicator() {
		return Collections.unmodifiableList(missingValueIndicators);
	}

	@Override
	public long getTimestamp(int index) {
		return getFirstTimeseriesUnivariate().getTimestamp(index);
	}

	@Override
	public List<Double> getValue(int index) {
		List<Double> values = new ArrayList<>();
		for (int i = 0; i < timeSeriesUnivariateList.size(); i++)
			values.add(timeSeriesUnivariateList.get(i).getValue(index));
		return Collections.unmodifiableList(values);
	}

	@Override
	public List<Double> getValue(long timestamp, boolean allowInterpolation) throws IndexOutOfBoundsException, IllegalArgumentException {
		List<Double> values = new ArrayList<>();
		for (int i = 0; i < timeSeriesUnivariateList.size(); i++) {
			values.add(timeSeriesUnivariateList.get(i).getValue(timestamp, allowInterpolation));
		}

		return Collections.unmodifiableList(values);
	}

	@Override
	public long getFirstTimestamp() {
		return getFirstTimeseriesUnivariate().getFirstTimestamp();
	}

	@Override
	public long getLastTimestamp() {
		return getFirstTimeseriesUnivariate().getLastTimestamp();
	}

	@Override
	public List<Long> getTimestamps() {
		return getFirstTimeseriesUnivariate().getTimestamps();
	}

	@Override
	public List<List<Double>> getValues() {
		List<List<Double>> values = new ArrayList<>();
		for (int i = 0; i < timeSeriesUnivariateList.size(); i++)
			values.add(timeSeriesUnivariateList.get(i).getValues());
		return Collections.unmodifiableList(values);
	}

	@Override
	public int findByDate(long timestamp, boolean requireExactMatch) throws IllegalArgumentException {
		return getFirstTimeseriesUnivariate().findByDate(timestamp, requireExactMatch);
	}

	@Override
	public boolean containsTimestamp(long timestamp) {
		return getFirstTimeseriesUnivariate().containsTimestamp(timestamp);
	}

	@Override
	public void insert(long timstamp, List<Double> values) {
		if (values == null || values.size() != timeSeriesUnivariateList.size())
			throw new IllegalArgumentException("TimeSeriesMultivariate: insert values are invalid");

		for (int i = 0; i < timeSeriesUnivariateList.size(); i++)
			timeSeriesUnivariateList.get(i).insert(timstamp, values.get(i));
	}

	@Override
	public void removeTimeValue(long timestamp) {
		for (int i = 0; i < timeSeriesUnivariateList.size(); i++)
			timeSeriesUnivariateList.get(i).removeTimeValue(timestamp);
	}

	@Override
	public void removeTimeValue(int index) {
		for (int i = 0; i < timeSeriesUnivariateList.size(); i++)
			timeSeriesUnivariateList.get(i).removeTimeValue(index);
	}

	@Override
	public void replaceValue(int index, List<Double> values) {
		if (values == null || values.size() != timeSeriesUnivariateList.size())
			throw new IllegalArgumentException("TimeSeriesMultivariate: insert values are invalid");

		for (int i = 0; i < timeSeriesUnivariateList.size(); i++)
			timeSeriesUnivariateList.get(i).replaceValue(index, values.get(i));
	}

	@Override
	public void replaceValue(long timestamp, List<Double> values) {
		if (values == null || values.size() != timeSeriesUnivariateList.size())
			throw new IllegalArgumentException("TimeSeriesMultivariate: insert values are invalid");
		for (int i = 0; i < timeSeriesUnivariateList.size(); i++)
			timeSeriesUnivariateList.get(i).replaceValue(timestamp, values.get(i));
	}

	@Override
	public long getID() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int getDimensionality() {
		return dimensionality;
	}

	@Override
	public List<ITimeSeriesUnivariate> getTimeSeriesList() {
		return timeSeriesUnivariateList;
	}

	@Override
	public ITimeSeriesUnivariate getTimeSeries(String attributeName) {
		for (ITimeSeriesUnivariate tsu : this.timeSeriesUnivariateList)
			if (tsu.getName().equals(attributeName))
				return tsu;

		return null;
	}

	@Override
	public ITimeSeriesUnivariate getTimeSeries(int attributeIndex) {
		if (attributeIndex < 0 || attributeIndex >= timeSeriesUnivariateList.size())
			throw new IndexOutOfBoundsException("TimeSeriesMultivariate: dimension out of bounds");

		return timeSeriesUnivariateList.get(attributeIndex);
	}

	@Override
	public List<String> getAttributeNames() {
		if (attributeNames != null)
			return attributeNames;

		attributeNames = new ArrayList<>();
		for (int i = 0; i < timeSeriesUnivariateList.size(); i++)
			attributeNames.add(timeSeriesUnivariateList.get(i).getName());
		attributeNames = Collections.unmodifiableList(attributeNames);
		return attributeNames;
	}

	@Override
	public String getAttributeName(int attributeIndex) {
		if (attributeIndex < 0 || attributeIndex >= timeSeriesUnivariateList.size())
			throw new IndexOutOfBoundsException("TimeSeriesMultivariate: required index (dimension) not meaningful");

		return timeSeriesUnivariateList.get(attributeIndex).getName();
	}

	@Override
	public List<String> getAttributeDescriptions() {
		List<String> attributeDescriptions = new ArrayList<>();
		for (int i = 0; i < timeSeriesUnivariateList.size(); i++)
			attributeDescriptions.add(timeSeriesUnivariateList.get(i).getDescription());
		return Collections.unmodifiableList(attributeDescriptions);
	}

	@Override
	public String getAttributeDescription(int attributeIndex) {
		if (attributeIndex < 0 || attributeIndex >= timeSeriesUnivariateList.size())
			throw new IndexOutOfBoundsException("TimeSeriesMultivariate: required index (dimension) not meaningful");

		return timeSeriesUnivariateList.get(attributeIndex).getDescription();
	}

	@Override
	public Double getValue(int index, String attribute) {
		ITimeSeriesUnivariate ts = getTimeSeries(attribute);

		if (ts != null)
			if (index < 0 || index >= ts.size())
				throw new IndexOutOfBoundsException("TimeSeriesMultivariate: required index (dimension) not meaningful");

		return ts.getValue(index);
	}

	@Override
	public int hashCode() {
		int hash = 23;

		if (getTimeSeriesList() == null)
			hash = 23 * hash;
		else
			for (ITimeSeriesUnivariate timeSeries : getTimeSeriesList())
				hash = 23 * hash + timeSeries.hashCode();

		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (!(obj instanceof TimeSeriesMultivariate))
			return false;

		TimeSeriesMultivariate otherTimeSeries = (TimeSeriesMultivariate) obj;

		if (size() != otherTimeSeries.size())
			return false;

		if (getDimensionality() != otherTimeSeries.getDimensionality())
			return false;

		for (int i = 0; i < getDimensionality(); i++)
			if (!getTimeSeries(i).equals(otherTimeSeries.getTimestamp(i)))
				return false;

		return true;
	}
}
