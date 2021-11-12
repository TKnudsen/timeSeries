package com.github.TKnudsen.timeseries.operations.preprocessing;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.TKnudsen.timeseries.data.ITimeSeries;
import com.github.TKnudsen.timeseries.data.ITimeSeriesListener;
import com.github.TKnudsen.timeseries.data.TimeSeriesEvent;

/**
 * <p>
 * Title: TimeSeriesProcessor
 * </p>
 * 
 * <p>
 * Description: provides the basic functionality for notifications of listeners
 * to change of temporal or value domain. Remaining problem: List of TS vs TS.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public abstract class TimeSeriesProcessor<TS extends ITimeSeries<?>> implements ITimeSeriesPreprocessor<TS> {

	@JsonIgnore
	private final List<ITimeSeriesListener> timeSeriesListeners = new CopyOnWriteArrayList<ITimeSeriesListener>();

	public final void addTimeSeriesListener(ITimeSeriesListener timeSeriesListener) {
		timeSeriesListeners.add(timeSeriesListener);
	}

	public final void removeTimeSeriesListener(ITimeSeriesListener timeSeriesListener) {
		timeSeriesListeners.remove(timeSeriesListener);
	}

	public final void removeTimeSeriesListeners() {
		while (!timeSeriesListeners.isEmpty())
			timeSeriesListeners.remove(0);
	}

	/**
	 * This method has to be called when the values of a time series have been
	 * changed and {@link ITimeSeriesListener} instances should be informed.
	 * 
	 * @param oldTimeSeries the old time series before processing
	 * @param newTimeSeries the new time series after processing
	 */
	public final void fireValueDomainChanged(TS oldTimeSeries, TS newTimeSeries) {
		if (!timeSeriesListeners.isEmpty()) {
			TimeSeriesEvent event = new TimeSeriesEvent(this, newTimeSeries, oldTimeSeries);
			for (ITimeSeriesListener timeSeriesListener : timeSeriesListeners)
				timeSeriesListener.valueDomainChanged(event);
		}
	}

	/**
	 * This method has to be called when the temporal domain of a time series have
	 * been changed and {@link ITimeSeriesListener} instances should be informed.
	 * 
	 * @param oldTimeSeries the old time series before processing
	 * @param newTimeSeries the new time series after processing
	 */
	public final void fireTemporalDomainChangedChanged(TS oldTimeSeries, TS newTimeSeries) {
		if (!timeSeriesListeners.isEmpty()) {
			TimeSeriesEvent event = new TimeSeriesEvent(this, newTimeSeries, oldTimeSeries);
			for (ITimeSeriesListener timeSeriesListener : timeSeriesListeners)
				timeSeriesListener.temporalDomainChanged(event);
		}
	}
}
