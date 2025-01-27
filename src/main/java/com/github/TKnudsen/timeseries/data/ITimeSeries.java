package com.github.TKnudsen.timeseries.data;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.github.TKnudsen.ComplexDataObject.data.interfaces.IDObject;
import com.github.TKnudsen.ComplexDataObject.data.interfaces.ISelfDescription;

/**
 * <p>
 * Title: TimeSeries
 * </p>
 * 
 * <p>
 * Description: TimeSeries interface
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017-2024
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.05
 */
public interface ITimeSeries<T> extends IDObject, ISelfDescription, Iterable<Entry<Long, T>> {

	public int size();

	boolean isEmpty();

	T getMissingValueIndicator();

	/**
	 * index access
	 * 
	 * @param index the index in the
	 * @return the time stamp as long
	 */
	long getTimestamp(int index);

	T getValue(int index);

	/**
	 * Retrieves the index for a given time stamp.
	 * 
	 * In case that no exact match is needed and not existing the index left
	 * (earlier) should be returned.
	 * 
	 * @param timeStamp         the time stamp as long
	 * @param requireExactMatch whether an exact match is needed
	 * @return index
	 * @throws IllegalArgumentException if an illegal argument is given
	 */
	int findByDate(long timeStamp, boolean requireExactMatch) throws IllegalArgumentException;

	/**
	 * temporal access
	 * 
	 * @param timeStamp time stamp
	 * @return boolean
	 */
	boolean containsTimestamp(long timeStamp);

	/**
	 * temporal access
	 * 
	 * @param timeStamp          time stamp
	 * @param allowInterpolation allow interpolation?
	 * @return value
	 * @throws IndexOutOfBoundsException e
	 * @throws IllegalArgumentException  e
	 */
	T getValue(long timeStamp, boolean allowInterpolation) throws IndexOutOfBoundsException, IllegalArgumentException;

	long getFirstTimestamp();

	/**
	 * convenient method for quicker value access.
	 * 
	 * @return
	 */
	default T getFirstValue() {
		return getValue(getFirstTimestamp(), false);
	}

	long getLastTimestamp();

	/**
	 * convenient method for quicker value access.
	 * 
	 * @return
	 */
	default T getLastValue() {
		return getValue(getLastTimestamp(), false);
	}

	/**
	 * @return the sorted list of time stamps
	 */
	List<Long> getTimestamps();

	/**
	 * @return the list of values following the order of their time stamps
	 */
	List<T> getValues();

	void insert(long timeStamp, T value);

	void removeTimeValue(long timeStamp);

	void removeTimeValue(int index);

	void replaceValue(int index, T value);

	void replaceValue(long timeStamp, T value);

	/**
	 * time series can be renamed
	 * 
	 * @param name name
	 */
	public void setName(String name);

	public void setDescription(String description);

	/**
	 * iterates over timestamps and returns time value pairs. With this iterator,
	 * traversing the time stamps but retrieving the values can be done with linear
	 * time. Otherwise, values have to be looked up using timeStamps as primary
	 * keys, runtime would be quadratic in the worst case.
	 */
	default Iterator<Entry<Long, T>> iterator() {
		return new TimeSeriesIterator<T>(getTimestamps(), getValues());
	}
}
