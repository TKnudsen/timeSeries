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

import com.github.TKnudsen.ComplexDataObject.model.tools.MathFunctions;

/**
 * <p>
 * Title: TimeSeriesUnivariate
 * </p>
 * 
 * <p>
 * Description: Models an univariate time series, i.e., data structure that
 * stores univariate phenomena observed over time, expressed with numerical
 * values.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.04
 */

public class TimeSeriesUnivariate implements ITimeSeriesUnivariate {

	protected final long id;
	private String name;
	private String description;

	protected final List<Long> timeStamps;
	protected final List<Double> values;
	protected Double missingValueIndicator;

	protected SortedMap<String, Object> attributes = new TreeMap<String, Object>();

	protected int hashCode;

	/**
	 * used for JSON, reflection, serialization & stuff
	 */
	@SuppressWarnings("unused")
	private TimeSeriesUnivariate() {
		this.id = MathFunctions.randomLong();
		this.timeStamps = new ArrayList<>();
		this.values = new ArrayList<>();
		this.missingValueIndicator = Double.NaN;

		initialize();
	}

	public TimeSeriesUnivariate(List<Long> timeStamps, List<Double> values) {
		this.id = MathFunctions.randomLong();
		this.timeStamps = timeStamps;
		this.values = values;
		this.missingValueIndicator = Double.NaN;

		initialize();
	}

	public TimeSeriesUnivariate(List<Long> timeStamps, List<Double> values, Double missingValueIndicator) {
		this.id = MathFunctions.randomLong();
		this.timeStamps = timeStamps;
		this.values = values;
		this.missingValueIndicator = missingValueIndicator;

		initialize();
	}

	public TimeSeriesUnivariate(long id, List<Long> timeStamps, List<Double> values) {
		this.id = id;
		this.timeStamps = timeStamps;
		this.values = values;
		this.missingValueIndicator = Double.NaN;

		initialize();
	}

	public TimeSeriesUnivariate(long id, List<Long> timeStamps, List<Double> values, Double missingValueIndicator) {
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
				throw new IllegalArgumentException(
						"TimeSeriesUnivariate: temporal information needs to be sorted and unique");

		resetHash();
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
	public Double getMissingValueIndicator() {
		return missingValueIndicator;
	}

	public void setMissingValueIndicator(Double missingValueIndicator) {
		this.missingValueIndicator = missingValueIndicator;
	}

	@Override
	public long getTimestamp(int index) {
		if (index < 0 || index >= timeStamps.size())
			throw new IndexOutOfBoundsException("TimeSeriesUnivariate: index out of bounds");

		return timeStamps.get(index);
	}

	@Override
	public Double getValue(int index) {
		if (index < 0 || index >= timeStamps.size())
			throw new IndexOutOfBoundsException("TimeSeriesUnivariate: index out of bounds");

		return values.get(index);
	}

	@Override
	public Double getValue(long timeStamp, boolean allowInterpolation)
			throws IndexOutOfBoundsException, IllegalArgumentException {
		if (allowInterpolation) {
			int index = findByDate(timeStamp, false);
			if (getTimestamp(index) == timeStamp)
				return getValue(index);
			else {
				long lBefore = getTimestamp(index);
				double vBefore = getValue(index);

				if (timeStamps.size() - 1 < index + 1)
					throw new IndexOutOfBoundsException(
							"TimeSeriesUnivariate.getValue: given time stamp outside bouds");
				long lAfter = getTimestamp(index + 1);
				double vAfter = getValue(index + 1);

				long deltaBefore = timeStamp - lBefore;
				double value = vBefore + ((vAfter - vBefore) * ((double) deltaBefore / (lAfter - lBefore)));
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
	public List<Double> getValues() {
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

	/**
	 * retrieves the index for a given time stamp. In case that no exact match is
	 * needed and not existing the index left (earlier) is returned.
	 * 
	 * @param indexStart
	 * @param indexEnd
	 * @param timeStamp
	 * @param requireExactMatch
	 * @return
	 * @throws IllegalArgumentException
	 */
	private int interpolationSearch(int indexStart, int indexEnd, long timeStamp, boolean requireExactMatch)
			throws IllegalArgumentException {
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
	public boolean containsTimestamp(long timeStamp) {
		try {
			int index = findByDate(timeStamp, true);
			if (index >= 0)
				return true;
		} catch (IllegalArgumentException e) {
		}

		return false;
	}

	@Override
	public void insert(long timeStamp, Double value) {
		if (isEmpty() || timeStamp > getLastTimestamp()) {
			timeStamps.add(timeStamp);
			values.add(value);
		} else if (timeStamp < getFirstTimestamp()) {
			timeStamps.add(0, timeStamp);
			values.add(0, value);
		} else {
			// int index = 0;
			// while (timeStamps.get(index) < timeStamp)
			// index++;
			//
			// if (timeStamps.get(index).longValue() == timeStamp)
			// replaceValue(index, value);
			// else {
			// timeStamps.add(index, timeStamp);
			// values.add(index, value);
			// }

			int indexLeftIfNotExisting = findByDate(timeStamp, false);
			if (timeStamps.get(indexLeftIfNotExisting).longValue() == timeStamp)
				replaceValue(indexLeftIfNotExisting, value);
			else {
				timeStamps.add(indexLeftIfNotExisting + 1, timeStamp);
				values.add(indexLeftIfNotExisting + 1, value);
			}
		}

		resetHash();
	}

	@Override
	public void removeTimeValue(long timeStamp) {
		int index = 0;
		while (timeStamps.get(index) < timeStamp)
			index++;

		if (timeStamps.get(index).longValue() == timeStamp) {
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
	public void replaceTimeValue(int index, long timeStamp) throws IllegalArgumentException {
		if (index < 0 || index >= timeStamps.size())
			throw new IndexOutOfBoundsException("TimeSeriesUnivariate: index out of bounds");

		timeStamps.set(index, timeStamp);

		resetHash();
	}

	@Override
	public void replaceValue(int index, Double value) throws IllegalArgumentException {
		if (index < 0 || index >= timeStamps.size())
			throw new IndexOutOfBoundsException("TimeSeriesUnivariate: index out of bounds");

		values.set(index, value);

		resetHash();
	}

	@Override
	public void replaceValue(long timeStamp, Double value) throws IllegalArgumentException {
		int index = findByDate(timeStamp, true);

		if (index < 0 || index >= timeStamps.size())
			throw new IndexOutOfBoundsException("TimeSeriesUnivariate: timeStamp out of bounds");

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
			stringBuffer
					.append(new Date(timeStamps.get(i)).toString() + ", " + String.format("%f", values.get(i)) + "\n");

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

	protected void resetHash() {
		hashCode = -1;
	}

	@Override
	public int hashCode() {
		if (hashCode != -1)
			return hashCode;

		hashCode = 23;

		if (values == null)
			hashCode = 23 * hashCode;
		else
			for (Double value : values) {
				long l = Double.doubleToLongBits(value);
				hashCode = 31 * hashCode + (int) (l ^ (l >>> 32));
			}

		if (timeStamps == null)
			hashCode = 23 * hashCode;
		else
			for (long l : timeStamps)
				hashCode = 23 * hashCode + (int) l;

		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (!(obj instanceof TimeSeriesUnivariate))
			return false;

		if (hashCode != obj.hashCode())
			return false;

		// TODO the following lines may be removed since they are already
		// covered with hashCode
		TimeSeriesUnivariate otherTimeSeries = (TimeSeriesUnivariate) obj;

		if (size() != otherTimeSeries.size())
			return false;

		for (int i = 0; i < size(); i++)
			if (getTimestamp(i) != otherTimeSeries.getTimestamp(i) || getValue(i) != otherTimeSeries.getValue(i))
				return false;

		return true;
	}
}
