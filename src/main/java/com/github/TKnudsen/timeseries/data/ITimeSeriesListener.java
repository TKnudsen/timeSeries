package com.github.TKnudsen.timeseries.data;

import java.util.EventListener;

/**
 * <p>
 * Title: ITimeSeriesListener
 * </p>
 * 
 * <p>
 * Description: Interface for classes that may be attached to a producer of
 * {@link TimeSeriesEvent} to be informed about changes in the data.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public interface ITimeSeriesListener<TS extends ITimeSeries<?>> extends EventListener {

	/**
	 * Will be called when the values of a time series have been changed.
	 * 
	 * @param timeSeriesEvent
	 */
	void valueDomainChanged(TimeSeriesEvent<TS> timeSeriesEvent);

	/**
	 * Will be called when the time / temporal information of a time series have
	 * been changed.
	 * 
	 * @param timeSeriesEvent
	 */
	void temporalDomainChanged(TimeSeriesEvent<TS> timeSeriesEvent);
}
