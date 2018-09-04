package com.github.TKnudsen.timeseries.data;

import java.util.EventObject;

/**
 * <p>
 * Title: TimeSeriesEvent
 * </p>
 * 
 * <p>
 * Description: An event that informs {@link ITimeSeriesListener} about a change
 * in a {@link ITimeSeries}
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public final class TimeSeriesEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3094406853801488793L;

	private final ITimeSeries<?> timeSeries;
	private ITimeSeries<?> oldTimeSeries;

	public TimeSeriesEvent(Object source, ITimeSeries<?> timeSeries) {
		this(source, timeSeries, null);
	}

	public TimeSeriesEvent(Object source, ITimeSeries<?> timeSeries, ITimeSeries<?> oldTimeSeries) {
		super(source);

		this.timeSeries = timeSeries;
		this.oldTimeSeries = oldTimeSeries;
	}

	public ITimeSeries<?> getTimeSeries() {
		return timeSeries;
	}

	public ITimeSeries<?> getOldTimeSeries() {
		return oldTimeSeries;
	}

}
