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
 * @version 1.02
 */
public final class TimeSeriesEvent<TS extends ITimeSeries<?>> extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3094406853801488793L;

	private final TS timeSeries;
	private TS oldTimeSeries;

	public TimeSeriesEvent(Object source, TS timeSeries) {
		this(source, timeSeries, null);
	}

	public TimeSeriesEvent(Object source, TS timeSeries, TS oldTimeSeries) {
		super(source);

		this.timeSeries = timeSeries;
		this.oldTimeSeries = oldTimeSeries;
	}

	public TS getTimeSeries() {
		return timeSeries;
	}

	public TS getOldTimeSeries() {
		return oldTimeSeries;
	}

}
