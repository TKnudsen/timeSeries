package com.github.TKnudsen.timeseries.data;

/**
 * <p>
 * Title: TimeValuePair
 * </p>
 * 
 * <p>
 * Description: structure that contains a time stamp as key and a T as value
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015-2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public interface ITimeValuePair<V extends Object> extends Comparable<ITimeValuePair<V>> {

	/**
	 * @return the time stamp
	 */
	public long getTimestamp();

	/**
	 * @return the value
	 */
	public V getValue();

	/**
	 * sets the value
	 * 
	 * @param value value
	 */
	public void setValue(V value);
}
