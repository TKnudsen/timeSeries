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
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public interface ITimeSeries<T> extends IDObject, ISelfDescription {

	public int size();

	boolean isEmpty();

	T getMissingValueIndicator();

	/**
	 * index access
	 * 
	 * @param index
	 * @return
	 */
	long getTimestamp(int index);

	T getValue(int index);

	/**
	 * temporal access
	 * 
	 * @param timestamp
	 * @param requireExactMatch
	 * @return
	 * @throws IllegalArgumentException
	 */
	int findByDate(long timestamp, boolean requireExactMatch) throws IllegalArgumentException;

	/**
	 * temporal access
	 * 
	 * @param timeStamp
	 * @return
	 */
	boolean containsTimestamp(long timeStamp);

	/**
	 * temporal access
	 * 
	 * @param timestamp
	 * @param allowInterpolation
	 * @return
	 * @throws IndexOutOfBoundsException
	 * @throws IllegalArgumentException
	 */
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

	void insert(long timestamp, T value);

	void removeTimeValue(long timestamp);

	void removeTimeValue(int index);

	void replaceValue(int index, T value);

	void replaceValue(long timestamp, T value);

	/**
	 * time series can be renamed
	 * 
	 * @param name
	 */
	public void setName(String name);

	public void setDescription(String description);
}
