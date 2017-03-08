package com.github.TKnudsen.timeseries.data.univariate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.github.TKnudsen.timeseries.operations.tools.RandomTools;

/**
 * <p>
 * Title: TimeSeriesUnivariate
 * </p>
 * 
 * <p>
 * Description: Models a TimeSeriesUnivarate, a data structure that stores
 * univariate phenomena observed over time.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */

public class TimeSeriesUnivariate implements ITimeSeriesUnivariate {

	protected final long id;
	private String name;
	private String description;

	protected final List<Long> timestamps;
	protected final List<Double> values;
	protected Double missingValueIndicator;

	protected SortedMap<String, Object> attributes = new TreeMap<String, Object>();

	/**
	 * used for JSON, reflection, serialization & stuff
	 */
	@SuppressWarnings("unused")
	private TimeSeriesUnivariate() {
		this.id = RandomTools.getRandomLong();
		this.timestamps = new ArrayList<>();
		this.values = new ArrayList<>();
		this.missingValueIndicator = Double.NaN;
	}

	public TimeSeriesUnivariate(List<Long> timestamps, List<Double> values) {
		this.id = RandomTools.getRandomLong();
		this.timestamps = timestamps;
		this.values = values;
		this.missingValueIndicator = Double.NaN;

		initialize();
	}

	public TimeSeriesUnivariate(List<Long> timestamps, List<Double> values, Double missingValueIndicator) {
		this.id = RandomTools.getRandomLong();
		this.timestamps = timestamps;
		this.values = values;
		this.missingValueIndicator = missingValueIndicator;

		initialize();
	}

	public TimeSeriesUnivariate(long id, List<Long> timestamps, List<Double> values) {
		this.id = id;
		this.timestamps = timestamps;
		this.values = values;
		this.missingValueIndicator = Double.NaN;

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

		for (int i = 0; i < timestamps.size() - 1; i++)
			if (timestamps.get(i) > timestamps.get(i + 1))
				throw new IllegalArgumentException("TimeSeriesUnivariate: temporal information needs to be sorted");
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
	public Double getValue(long timeStamp, boolean allowInterpolation) throws IndexOutOfBoundsException, IllegalArgumentException {
		if (allowInterpolation) {
			int index = findByDate(timeStamp, false);
			if (getTimestamp(index) == timeStamp)
				return getValue(index);
			else {
				long lBefore = getTimestamp(index);
				double vBefore = getValue(index);

				if (timestamps.size() - 1 < index + 1)
					throw new IndexOutOfBoundsException("TimeSeriesUnivariate.getValue: given time stamp outside bouds");
				long lAfter = getTimestamp(index + 1);
				double vAfter = getValue(index + 1);

				long deltaBefore = timeStamp - lBefore;
				double value = vBefore + ((vAfter - vBefore) * (deltaBefore / (lAfter - lBefore)));
				return value;
			}
		} else {
			try {
				int index = findByDate(timeStamp, true);
				return getValue(index);
			} catch (IllegalArgumentException e) {

			}
		}

		throw new IllegalArgumentException("TimeSeriesUnivariate.getValue(long): time stamp does not exist");
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
	public int findByDate(long timeStamp, boolean requireExactMatch) throws IllegalArgumentException {
		if (getFirstTimestamp() > timeStamp)
			throw new IllegalArgumentException("Time stamp outside time interval");

		if (getLastTimestamp() < timeStamp)
			throw new IllegalArgumentException("Time stamp outside time interval");

		return interpolationSearch(0, timestamps.size() - 1, timeStamp, requireExactMatch);
	}

	private int interpolationSearch(int indexStart, int indexEnd, long timeStamp, boolean requireExactMatch) throws IllegalArgumentException {
		if (indexStart > indexEnd)
			System.out.println("Debug: this must be fixed for the next release!");

		if (indexStart == indexEnd)
			return indexStart;

		if (indexEnd - indexStart == 1)
			if (!requireExactMatch)
				return indexStart;
			else {
				if (getTimestamp(indexStart) == timeStamp)
					return indexStart;
				else if (getTimestamp(indexEnd) == timeStamp)
					return indexEnd;
				throw new IllegalArgumentException("TimeSeriesUnivariate: given time stamp does not exist");
			}

		// interpolate appropriate index
		long l1 = getTimestamp(indexStart);
		long l2 = getTimestamp(indexEnd);

		if (l1 == timeStamp)
			return indexStart;

		if (l2 == timeStamp)
			return indexEnd;

		if (l1 > timeStamp && requireExactMatch)
			throw new IllegalArgumentException("TimeSeriesUnivariate: given time stamp does not exist");

		if (l2 < timeStamp && requireExactMatch)
			throw new IllegalArgumentException("TimeSeriesUnivariate: given time stamp does not exist");

		double deltaLStartToEnd = l2 - l1;
		double deltaLStartToTime = timeStamp - l1;
		double deltaIndex = indexEnd - indexStart;
		int newSplitIndex = indexStart + Math.max(1, (int) (deltaIndex * deltaLStartToTime / deltaLStartToEnd));

		long newLong = getTimestamp(newSplitIndex);

		if (newLong == timeStamp)
			return newSplitIndex;
		else if (newLong > timeStamp) // earlier bin
			return interpolationSearch(indexStart, newSplitIndex, timeStamp, requireExactMatch);
		else // later bin
			return interpolationSearch(newSplitIndex, indexEnd, timeStamp, requireExactMatch);
	}

	@Override
	public boolean containsTimestamp(long timestamp) {
		try {
			int index = findByDate(timestamp, true);
			if (index >= 0)
				return true;
		} catch (IllegalArgumentException e) {
		}

		return false;
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
	public void replaceValue(long timestamp, Double value) throws IllegalArgumentException {
		int index = findByDate(timestamp, true);

		if (index < 0 || index >= timestamps.size())
			throw new IndexOutOfBoundsException("TimeSeriesUnivariate: timestamp out of bounds");

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
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < this.size(); i++)
			stringBuffer.append(new Date(timestamps.get(i)).toString() + ", " + String.format("%f", values.get(i)) + "\n");

		return stringBuffer.toString();
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

	@Override
	public void add(String attribute, Object value) {
		attributes.put(attribute, value);
	}

	@Override
	public Object get(String attribute) {
		return attributes.get(attribute);
	}

	@Override
	public Class<?> getType(String attribute) {
		if (attributes.get(attribute) != null)
			return attributes.get(attribute).getClass();
		return null;
	}

	@Override
	public Set<String> keySet() {
		return attributes.keySet();
	}

	@Override
	public Map<String, Class<?>> getTypes() {
		Map<String, Class<?>> ret = new HashMap<>();
		for (String string : attributes.keySet())
			if (attributes.get(string) == null)
				ret.put(string, null);
			else
				ret.put(string, attributes.get(string).getClass());
		return null;
	}

	@Override
	public Object remove(String attribute) {
		if (attributes.get(attribute) != null)
			return attributes.remove(attribute);
		return null;
	}
}
