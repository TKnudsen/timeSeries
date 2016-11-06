package com.github.TKnudsen.timeseries.data;

import java.util.List;

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
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public interface ITimeSeries<T> extends IDObject, ISelfDescription {

	public int size();

	boolean isEmpty();

	T getMissingValueIndicator();

	long getTimestamp(int index);

	T getValue(int index);

	T getValue(long timestamp, boolean allowInterpolation) throws IndexOutOfBoundsException, IllegalArgumentException;

	long getFirstTimestamp();

	long getLastTimestamp();

	/**
	 * @return the sorted list of time stamps
	 */
	List<Long> getTimestamps();

	/**
	 * @return the list of values following the order of their time stamps
	 */
	List<T> getValues();

	int findByDate(long timestamp, boolean requireExactMatch) throws IllegalArgumentException;

	boolean containsTimestamp(long timeStamp);

	void insert(long timestamp, T value);

	void removeTimeValue(long timestamp);

	void removeTimeValue(int index);

	void replaceValue(int index, T value);

	void replaceValue(long timestamp, T value);
}
