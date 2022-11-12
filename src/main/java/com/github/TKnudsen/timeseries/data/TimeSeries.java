package com.github.TKnudsen.timeseries.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.tools.MathFunctions;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;

/**
 * <p>
 * timeSeries
 * </p>
 * 
 * <p>
 * Models an general time series, i.e., data structure that stores univariate
 * phenomena observed over time. The value domain is stored with a generics
 * parameter.
 * </p>
 * 
 * <p>
 * Data modeling is done with two lists, one for the temporal and one for the
 * value domain. As such, the design does not build upon a single list with
 * time-value pairs which would be an alternative.
 * </p>
 * 
 * <p>
 * Copyright: (c) 2016-2020 Juergen Bernard,
 * https://github.com/TKnudsen/timeSeries
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public abstract class TimeSeries<V> implements ITimeSeries<V> {

	private final long id;
	private String name;
	private String description;

	protected final List<Long> timeStamps;
	protected final List<V> values;
	private V missingValueIndicator;

	protected int hashCode;

	protected TimeSeries() {
		this.id = MathFunctions.randomLong();
		this.timeStamps = new ArrayList<>();
		this.values = new ArrayList<>();
		this.missingValueIndicator = null;

		initialize();
	}

	public TimeSeries(List<Long> timeStamps, List<V> values) {
		this.id = MathFunctions.randomLong();
		this.timeStamps = timeStamps;
		this.values = values;
		this.missingValueIndicator = null;

		initialize();
	}

	public TimeSeries(long id, List<Long> timeStamps, List<V> values) {
		this.id = id;
		this.timeStamps = timeStamps;
		this.values = values;
		this.missingValueIndicator = null;

		initialize();
	}

	private void initialize() {
		if (timeStamps == null)
			throw new IllegalArgumentException(getName() + " : time stamps null");

		if (values == null)
			throw new IllegalArgumentException(getName() + " : values null");

		if (timeStamps.size() != values.size())
			throw new IllegalArgumentException(getName() + " : input data inconsistent");

		for (int i = 0; i < timeStamps.size() - 1; i++)
			if (timeStamps.get(i) >= timeStamps.get(i + 1))
				throw new IllegalArgumentException(getName() + " : temporal information needs to be sorted and unique");

		resetHash();
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
			for (V value : values) {
				long l = valueToHash(value);
				hashCode = 31 * hashCode + (int) (l ^ (l >>> 32));
			}

		if (timeStamps == null)
			hashCode = 23 * hashCode;
		else
			for (long l : timeStamps)
				hashCode = 23 * hashCode + (int) l;

		return hashCode;
	}

	protected abstract long valueToHash(V value);

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (!(obj instanceof TimeSeriesUnivariate))
			return false;

		if (hashCode() != obj.hashCode())
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
	public int size() {
		return timeStamps.size();
	}

	@Override
	public boolean isEmpty() {
		return timeStamps.isEmpty() && values.isEmpty();
	}

	@Override
	public V getMissingValueIndicator() {
		return missingValueIndicator;
	}

	public void setMissingValueIndicator(V missingValueIndicator) {
		this.missingValueIndicator = missingValueIndicator;
	}

	@Override
	public long getTimestamp(int index) {
		if (index < 0 || index >= timeStamps.size())
			throw new IndexOutOfBoundsException("TimeSeries: index out of bounds");

		return timeStamps.get(index);
	}

	@Override
	public V getValue(int index) {
		if (index < 0 || index >= timeStamps.size())
			throw new IndexOutOfBoundsException("TimeSeries: index out of bounds");

		return values.get(index);
	}

	@Override
	public V getValue(long timeStamp, boolean allowInterpolation)
			throws IndexOutOfBoundsException, IllegalArgumentException {
		if (allowInterpolation) {
			int index = findByDate(timeStamp, false);
			if (getTimestamp(index) == timeStamp)
				return getValue(index);
			else {
				long lBefore = getTimestamp(index);
				V vBefore = getValue(index);

				if (timeStamps.size() - 1 < index + 1)
					throw new IndexOutOfBoundsException("TimeSeries.getValue: given time stamp outside bouds");
				long lAfter = getTimestamp(index + 1);
				V vAfter = getValue(index + 1);

				V value = interpolateValue(timeStamp, lBefore, lAfter, vBefore, vAfter);
				return value;
			}
		} else {
			try {
				int index = findByDate(timeStamp, true);
				return getValue(index);
			} catch (IllegalArgumentException e) {

			}
		}

		throw new IllegalArgumentException("TimeSeries.getValue(long): time stamp does not exist");
	}

	protected abstract V interpolateValue(long timeStamp, long lBefore, long lAfter, V vBefore, V vAfter);

	@Override
	/**
	 * retrieves the index for a given time stamp. In case no exact match is needed
	 * and not existing the index left (earlier) is returned.
	 */
	public int findByDate(long timeStamp, boolean requireExactMatch) throws IllegalArgumentException {
		if (getFirstTimestamp() > timeStamp)
			throw new IllegalArgumentException("Time stamp outside time interval");

		if (getLastTimestamp() < timeStamp)
			throw new IllegalArgumentException("Time stamp outside time interval");

		int index = interpolationSearch(0, timeStamps.size() - 1, timeStamp, requireExactMatch);

		if (index >= 0)
			return index;
		else {
			for (int i = 0; i < size(); i++) {
				if (getTimestamp(i) == timeStamp)
					return i;
				else if (getTimestamp(i) > timeStamp && !requireExactMatch)
					return i - 1;
			}
		}

		return -1;
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
			throw new IllegalArgumentException("TimeSeries: given time stamp does not exist");

		if (indexStart == indexEnd)
			return indexStart;

		if (indexEnd - indexStart == 1)
			if (!requireExactMatch)
				if (getTimestamp(indexEnd) == timeStamp)
					return indexEnd;
				else
					return indexStart;
			else {
				if (getTimestamp(indexStart) == timeStamp)
					return indexStart;
				else if (getTimestamp(indexEnd) == timeStamp)
					return indexEnd;
				throw new IllegalArgumentException("TimeSeries: given time stamp does not exist");
			}

		// interpolate appropriate index
		long l1 = getTimestamp(indexStart);
		long l2 = getTimestamp(indexEnd);

		if (l1 == timeStamp)
			return indexStart;

		if (l2 == timeStamp)
			return indexEnd;

		if (l1 > timeStamp && requireExactMatch)
			throw new IllegalArgumentException("TimeSeries: given time stamp does not exist");

		if (l2 < timeStamp && requireExactMatch)
			throw new IllegalArgumentException("TimeSeries: given time stamp does not exist");

		double deltaLStartToEnd = l2 - l1;
		double deltaLStartToTime = timeStamp - l1;
		double deltaIndex = indexEnd - indexStart;
		int newSplitIndex = indexStart + Math.max(1, (int) (deltaIndex * deltaLStartToTime / deltaLStartToEnd));

		long newLong = getTimestamp(newSplitIndex);

		if (newLong == timeStamp)
			return newSplitIndex;
		else if (newLong > timeStamp) // earlier bins
			return interpolationSearch(indexStart, newSplitIndex, timeStamp, requireExactMatch);
		else // later bins
			return interpolationSearch(newSplitIndex, indexEnd, timeStamp, requireExactMatch);
	}

	@Override
	public boolean containsTimestamp(long timeStamp) {
		if (isEmpty())
			return false;

		try {
			int index = findByDate(timeStamp, true);
			if (index >= 0)
				return true;
		} catch (IllegalArgumentException e) {
		}

		return false;
	}

	@Override
	public long getFirstTimestamp() {
		if (timeStamps.size() == 0)
			throw new NullPointerException("TimeSeries: time series empty");

		return timeStamps.get(0);
	}

	@Override
	public long getLastTimestamp() {
		if (timeStamps.size() == 0)
			throw new NullPointerException("TimeSeries: time series empty");

		return timeStamps.get(timeStamps.size() - 1);
	}

	@Override
	public List<Long> getTimestamps() {
		return Collections.unmodifiableList(timeStamps);
	}

	@Override
	public List<V> getValues() {
		return Collections.unmodifiableList(values);
	}

	@Override
	public void insert(long timeStamp, V value) {
		if (isEmpty() || timeStamp > getLastTimestamp()) {
			timeStamps.add(timeStamp);
			values.add(value);
		} else if (timeStamp < getFirstTimestamp()) {
			timeStamps.add(0, timeStamp);
			values.add(0, value);
		} else {
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
	public void replaceValue(int index, V value) throws IllegalArgumentException {
		if (index < 0 || index >= timeStamps.size())
			throw new IndexOutOfBoundsException("TimeSeries: index out of bounds");

		values.set(index, value);

		resetHash();
	}

	@Override
	public void replaceValue(long timeStamp, V value) throws IllegalArgumentException {
		int index = findByDate(timeStamp, true);

		if (index < 0 || index >= timeStamps.size())
			throw new IndexOutOfBoundsException("TimeSeries: timeStamp out of bounds");

		values.set(index, value);

		resetHash();
	}

}
