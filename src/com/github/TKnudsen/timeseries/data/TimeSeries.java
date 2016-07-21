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
public interface TimeSeries<T> extends IDObject, ISelfDescription {

	public int size();

	boolean isEmpty();

	T getMissingValueIndicator();

	long getTimestamp(int index);

	T getValue(int index);

	T getValue(long timestamp, boolean allowInterpolation);

	long getFirstTimestamp();

	long getLastTimestamp();

	/**
	 * @return the sorted list of timestamps
	 */
	List<Long> getTimestamps();

	/**
	 * @return the list of values following the order of their timestamps
	 */
	List<T> getValues();

	/**
	 * @param timestamp
	 *            the timestamp
	 * @return if the is timestamp present: positive index representing the
	 *         position of the timestamp, if the timestamp is not present:
	 *         negative value that can be converted to the calculated position
	 *         to insert the timestamp by calculating (-index - 1)
	 */
	int findByDate(long timestamp);

	boolean containsTimestamp(long timestamp);

	void insert(long timstamp, T value);

	void removeTimeValue(long timestamp);

	void removeTimeValue(int index);

	void replaceValue(int index, T value);

	void replaceValue(long timestamp, T value);
}
