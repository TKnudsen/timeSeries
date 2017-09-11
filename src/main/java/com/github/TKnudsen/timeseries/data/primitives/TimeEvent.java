package com.github.TKnudsen.timeseries.data.primitives;

import java.util.Date;

import com.github.TKnudsen.ComplexDataObject.data.interfaces.ISelfDescription;

/**
 * <p>
 * Title: TimeEvent
 * </p>
 * 
 * <p>
 * Description: temporal information about an event (with no temporal duration).
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class TimeEvent implements ISelfDescription {

	/**
	 * 
	 */
	private final String name;

	/**
	 * 
	 */
	private final String description;

	/**
	 * 
	 */
	private final Date date;

	public TimeEvent(String name, Date date, String description) {
		super();
		this.name = name;
		this.description = description;
		this.date = date;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public Date getDate() {
		return date;
	}

}
