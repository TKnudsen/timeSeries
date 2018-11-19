package com.github.TKnudsen.timeseries.data;

import com.github.TKnudsen.ComplexDataObject.data.interfaces.IKeyValueProvider;
import com.github.TKnudsen.ComplexDataObject.model.tools.MathFunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.github.TKnudsen.timeseries.data.primitives.TimeValuePairTools;

/**
 * <p>
 * Title: TimeSeriesWithTimeValuePairs
 * </p>
 * 
 * <p>
 * Description: Abstract time series class for data structured w.r.t. time
 * (time-value pairs).
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public abstract class TimeSeriesWithTimeValuePairs<T> implements ITimeSeries<T>, IKeyValueProvider<Object> {

	protected final long id;
	protected String name;
	protected String description;

	protected final List<Long> timeStamps;
	protected final List<T> values;
	protected T missingValueIndicator;

	protected SortedMap<String, Object> attributes = new TreeMap<String, Object>();

	protected int hashCode;

	/**
	 * used for JSON, reflection, serialization & stuff
	 */
	@SuppressWarnings("unused")
	private TimeSeriesWithTimeValuePairs() {
		this(MathFunctions.randomLong(), new ArrayList<Long>(), new ArrayList<T>(), null);
	}

	public TimeSeriesWithTimeValuePairs(List<Long> timeStamps, List<T> values) {
		this(MathFunctions.randomLong(), timeStamps, values, null);
	}

	public TimeSeriesWithTimeValuePairs(List<ITimeValuePair<T>> values) {
		this(MathFunctions.randomLong(), TimeValuePairTools.getTimeStamps(values), TimeValuePairTools.getValues(values), null);
	}

	public TimeSeriesWithTimeValuePairs(List<Long> timeStamps, List<T> values, T missingValueIndicator) {
		this(MathFunctions.randomLong(), timeStamps, values, missingValueIndicator);
	}

	public TimeSeriesWithTimeValuePairs(long id, List<Long> timeStamps, List<T> values) {
		this(id, timeStamps, values, null);
	}

	public TimeSeriesWithTimeValuePairs(long id, List<Long> timeStamps, List<T> values, T missingValueIndicator) {
		this.id = id;
		this.timeStamps = timeStamps;
		this.values = values;
		this.missingValueIndicator = missingValueIndicator;

		initialize();
	}

	private void initialize() {
		if (timeStamps == null)
			throw new IllegalArgumentException("TimeSeriesUnivariate: time stamps null");

		if (values == null)
			throw new IllegalArgumentException("TimeSeriesUnivariate: values null");

		if (timeStamps.size() != values.size())
			throw new IllegalArgumentException("TimeSeriesUnivariate: input data inconsistent");

		for (int i = 0; i < timeStamps.size() - 1; i++)
			if (timeStamps.get(i) >= timeStamps.get(i + 1))
				throw new IllegalArgumentException("TimeSeriesUnivariate: temporal information needs to be sorted and unique");

		resetHash();
	}

	protected void resetHash() {
		hashCode = -1;
	}

	@Override
	public int size() {
		return timeStamps.size();
	}

	@Override
	public boolean isEmpty() {
		return timeStamps.isEmpty() && values.isEmpty();
	}

	@Override
	public T getMissingValueIndicator() {
		return missingValueIndicator;
	}

	public void setMissingValueIndicator(T missingValueIndicator) {
		this.missingValueIndicator = missingValueIndicator;
	}

	@Override
	public long getTimestamp(int index) {
		if (index < 0 || index >= timeStamps.size())
			throw new IndexOutOfBoundsException("TimeSeriesUnivariate: index out of bounds");

		return timeStamps.get(index);
	}

	@Override
	public T getValue(int index) {
		if (index < 0 || index >= timeStamps.size())
			throw new IndexOutOfBoundsException("TimeSeriesUnivariate: index out of bounds");

		return values.get(index);
	}

	@Override
	public long getFirstTimestamp() {
		if (timeStamps.size() == 0)
			throw new NullPointerException("TimeSeriesUnivariate: access to a time stamp that doesn't exist");

		return timeStamps.get(0);
	}

	@Override
	public long getLastTimestamp() {
		if (timeStamps.size() == 0)
			throw new NullPointerException("TimeSeriesUnivariate: access to a time stamp that doesn't exist");

		return timeStamps.get(timeStamps.size() - 1);
	}

	@Override
	public List<Long> getTimestamps() {
		return Collections.unmodifiableList(timeStamps);
	}

	@Override
	public List<T> getValues() {
		return Collections.unmodifiableList(values);
	}

	@Override
	public int findByDate(long timeStamp, boolean requireExactMatch) throws IllegalArgumentException {
		if (getFirstTimestamp() > timeStamp)
			throw new IllegalArgumentException("Time stamp outside time interval");

		if (getLastTimestamp() < timeStamp)
			throw new IllegalArgumentException("Time stamp outside time interval");

		return interpolationSearch(0, timeStamps.size() - 1, timeStamp, requireExactMatch);
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
	public void insert(long timestamp, T value) {
		if (isEmpty() || timestamp > getLastTimestamp()) {
			timeStamps.add(timestamp);
			values.add(value);
		} else if (timestamp < getFirstTimestamp()) {
			timeStamps.add(0, timestamp);
			values.add(0, value);
		} else {
			int index = 0;
			while (timeStamps.get(index) < timestamp)
				index++;

			if (timeStamps.get(index).longValue() == timestamp)
				replaceValue(index, value);
			else {
				timeStamps.add(index, timestamp);
				values.add(index, value);
			}
		}

		resetHash();
	}

	@Override
	public void removeTimeValue(long timestamp) {
		int index = 0;
		while (timeStamps.get(index) < timestamp)
			index++;

		if (timeStamps.get(index).longValue() == timestamp) {
			timeStamps.remove(index);
			values.remove(index);
		}

		resetHash();
	}

	@Override
	public void removeTimeValue(int index) {
		timeStamps.remove(index);
		values.remove(index);

		resetHash();
	}
	
	@Override
	public void replaceTimeValue(int index, long timestamp) throws IllegalArgumentException {
		
		if (index < 0 || index >= timeStamps.size())
			throw new IndexOutOfBoundsException("TimeSeriesUnivariate: index out of bounds");
		
		timeStamps.set(index, timestamp);
		
		resetHash();
	}

	@Override
	public void replaceValue(int index, T value) throws IllegalArgumentException {
		if (index < 0 || index >= timeStamps.size())
			throw new IndexOutOfBoundsException("TimeSeriesUnivariate: index out of bounds");

		values.set(index, value);

		resetHash();
	}

	@Override
	public void replaceValue(long timestamp, T value) throws IllegalArgumentException {
		int index = findByDate(timestamp, true);

		if (index < 0 || index >= timeStamps.size())
			throw new IndexOutOfBoundsException("TimeSeriesUnivariate: timestamp out of bounds");

		values.set(index, value);

		resetHash();
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
			stringBuffer.append(new Date(timeStamps.get(i)).toString() + ", " + String.format("%f", values.get(i)) + "\n");

		return stringBuffer.toString();
	}

	@Override
	public void add(String attribute, Object value) {
		attributes.put(attribute, value);
	}

	@Override
	public Object getAttribute(String attribute) {
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
		return ret;
	}

	@Override
	public Object removeAttribute(String attribute) {
		if (attributes.get(attribute) != null)
			return attributes.remove(attribute);
		return null;

		// no resetHash since attributes are not part of the value-building hash
		// components
	}
}
