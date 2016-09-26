package com.github.TKnudsen.timeseries.data.univariate;

import java.util.Collections;
import java.util.List;

import com.github.TKnudsen.timeseries.util.RandomTools;

/**
 * <p>
 * Title: TimeSeriesUnivariate
 * </p>
 * 
 * <p>
 * Description: Models an univariate phenomenon observed over time.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.0
 */

public class TimeSeriesUnivariate implements ITimeSeriesUnivariate {

	protected final long id;
	private String name;
	private String description;

	protected final List<Long> timestamps;
	protected final List<Double> values;
	protected Double missingValueIndicator;

	public TimeSeriesUnivariate(List<Long> timestamps, List<Double> values) {
		this.id = RandomTools.getRandomLong();
		this.timestamps = timestamps;
		this.values = values;

		initialize();
	}

	public TimeSeriesUnivariate(long id, List<Long> timestamps, List<Double> values) {
		this.id = id;
		this.timestamps = timestamps;
		this.values = values;

		initialize();
	}

	public TimeSeriesUnivariate(long id, List<Long> timestamps, List<Double> values, Double missingValueIndicator) {
		this.id = id;
		this.timestamps = timestamps;
		this.values = values;
		this.missingValueIndicator = missingValueIndicator;

		initialize();
	}

	private void initialize() {
		if (timestamps == null)
			throw new IllegalArgumentException("TimeSeriesUnivariate: time stamps null");

		if (values == null)
			throw new IllegalArgumentException("TimeSeriesUnivariate: values null");

		if (timestamps.size() != values.size())
			throw new IllegalArgumentException("TimeSeriesUnivariate: input data inconsistent");
	}

	@Override
	public int size() {
		return timestamps.size();
	}

	@Override
	public boolean isEmpty() {
		return timestamps.isEmpty() && values.isEmpty();
	}

	@Override
	public Double getMissingValueIndicator() {
		return missingValueIndicator;
	}

	public void setMissingValueIndicator(Double missingValueIndicator) {
		this.missingValueIndicator = missingValueIndicator;
	}

	@Override
	public long getTimestamp(int index) {
		if (index < 0 || index >= timestamps.size())
			throw new IndexOutOfBoundsException("TimeSeriesUnivariate: index out of bounds");

		return timestamps.get(index);
	}

	@Override
	public Double getValue(int index) {
		if (index < 0 || index >= timestamps.size())
			throw new IndexOutOfBoundsException("TimeSeriesUnivariate: index out of bounds");

		return values.get(index);
	}

	@Override
	public Double getValue(long timestamp, boolean allowInterpolation) {
		int index = findByDate(timestamp);
		if (index > 0)
			return values.get(index);

		throw new UnsupportedOperationException("TimeSeriesUnivariate: get vlaues with temporal interpolation is still to be implemented.");
	}

	@Override
	public long getFirstTimestamp() {
		if (timestamps.size() == 0)
			throw new NullPointerException("TimeSeriesUnivariate: access to a time stamp that doesn't exist");

		return timestamps.get(0);
	}

	@Override
	public long getLastTimestamp() {
		if (timestamps.size() == 0)
			throw new NullPointerException("TimeSeriesUnivariate: access to a time stamp that doesn't exist");

		return timestamps.get(timestamps.size() - 1);
	}

	@Override
	public List<Long> getTimestamps() {
		return Collections.unmodifiableList(timestamps);
	}

	@Override
	public List<Double> getValues() {
		return Collections.unmodifiableList(values);
	}

	@Override
	public int findByDate(long timeStamp) {
		int ret = -1;
		try {
			for (int i = 0; i < timestamps.size(); i++)
				if (timestamps.get(i).longValue() == timeStamp)
					return i;
		} catch (Exception e) {

		}
		return ret;
	}

	@Override
	public boolean containsTimestamp(long timestamp) {
		if (findByDate(timestamp) == -1)
			return false;
		return true;
	}

	@Override
	public void insert(long timestamp, Double value) {
		if (isEmpty() || timestamp > getLastTimestamp()) {
			timestamps.add(timestamp);
			values.add(value);
		} else if (timestamp < getFirstTimestamp()) {
			timestamps.add(0, timestamp);
			values.add(0, value);
		} else {
			int index = 0;
			while (timestamps.get(index) < timestamp)
				index++;

			if (timestamps.get(index).longValue() == timestamp)
				replaceValue(index, value);
			else {
				timestamps.add(index, timestamp);
				values.add(index, value);
			}
		}
	}

	@Override
	public void removeTimeValue(long timestamp) {
		int index = 0;
		while (timestamps.get(index) < timestamp)
			index++;

		if (timestamps.get(index).longValue() == timestamp) {
			timestamps.remove(index);
			values.remove(index);
		}
	}

	@Override
	public void removeTimeValue(int index) {
		timestamps.remove(index);
		values.remove(index);
	}

	@Override
	public void replaceValue(int index, Double value) {
		if (index < 0 || index >= timestamps.size())
			throw new IndexOutOfBoundsException("TimeSeriesUnivariate: index out of bounds");

		values.set(index, value);
	}

	@Override
	public void replaceValue(long timestamp, Double value) {
		int index = findByDate(timestamp);

		if (index > 0)
			values.set(index, value);
	}

	@Override
	public long getID() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		int hash = 23;

		if (values == null)
			hash = 23 * hash;
		else
			for (Double value : values) {
				long l = Double.doubleToLongBits(value);
				hash = 31 * hash + (int) (l ^ (l >>> 32));
			}

		if (timestamps == null)
			hash = 23 * hash;
		else
			for (long l : timestamps)
				hash = 23 * hash + (int) l;

		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (!(obj instanceof TimeSeriesUnivariate))
			return false;

		TimeSeriesUnivariate otherTimeSeries = (TimeSeriesUnivariate) obj;

		if (size() != otherTimeSeries.size())
			return false;

		for (int i = 0; i < size(); i++)
			if (getTimestamp(i) != otherTimeSeries.getTimestamp(i) || getValue(i) != otherTimeSeries.getValue(i))
				return false;

		return true;
	}
}
