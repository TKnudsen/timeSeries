package com.github.TKnudsen.timeseries.data;

/**
 * <p>
 * Title: AbstractTimeValuePair
 * </p>
 * 
 * <p>
 * Description: Basic TimeValuePair functionality.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class AbstractTimeValuePair<T> implements ITimeValuePair<T> {

	protected final long timestamp;
	protected T value;

	/**
	 * @param timestamp
	 *            the time stamp
	 * @param value
	 *            the value
	 */
	public AbstractTimeValuePair(long timestamp, T value) {
		this.timestamp = timestamp;
		this.value = value;
	}

	/**
	 * equals function for a general time value pair.
	 * 
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof ITimeValuePair))
			return false;

		ITimeValuePair<?> tvp = (ITimeValuePair<?>) obj;
		if (timestamp == tvp.getTimestamp() && value == tvp.getValue())
			return true;
		return false;
	}

	@Override
	public String toString() {
		return String.format("[%d, %.3f]", timestamp, value);
	}

	/**
	 * @return the time stamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public void setValue(T value) {
		this.value = value;
	}
}
