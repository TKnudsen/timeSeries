package com.github.TKnudsen.timeseries.data.multivariate;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.github.TKnudsen.ComplexDataObject.data.ranking.Ranking;
import com.github.TKnudsen.timeseries.data.ITemporalLabeling;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.primitives.TimeInterval;
import com.github.TKnudsen.timeseries.data.primitives.TimeIntervalLabel;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

/**
 * <p>
 * Title: TimeSeriesMultivariateLabeled
 * </p>
 * 
 * <p>
 * Description: wraps a multivariate time series with labeling support.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public class TimeSeriesMultivariateLabeled extends TimeSeriesMultivariate implements ITemporalLabeling<String> {

	private SortedMap<Long, String> eventLabels = new TreeMap<>();
	private Ranking<TimeIntervalLabel<String>> intervalLabels = new Ranking<TimeIntervalLabel<String>>();

//	private TimeSeriesMultivariateLabeled(Ranking<TimeIntervalLabel<String>> intervalLabels) {
//		super();
//	}
//	
//	private TimeSeriesMultivariateLabeled(SortedMap<Long, String> eventLabels,Ranking<TimeIntervalLabel<String>> intervalLabels) {
//		super();
//	}
	
	@SuppressWarnings("unused")
	private TimeSeriesMultivariateLabeled() {
		super();
	}
	
	public TimeSeriesMultivariateLabeled(List<ITimeSeriesUnivariate> timeSeriesUnivariateList, List<String> timeSeriesNames) {
		super(timeSeriesUnivariateList, timeSeriesNames);
	}

	public TimeSeriesMultivariateLabeled(long id, List<ITimeSeriesUnivariate> timeSeriesUnivariateList, List<String> timeSeriesNames) {
		super(id, timeSeriesUnivariateList, timeSeriesNames);
	}

	public TimeSeriesMultivariateLabeled(ITimeSeriesMultivariate tsm) {
		super(tsm.getID(), tsm.getTimeSeriesList(), tsm.getAttributeNames());
	}

	@Override
	public void addEventLabel(long timeStamp, String label) {
		eventLabels.put(timeStamp, label);
	}

	@Override
	public void addTimeDurationLabel(long timeStamp, TimeDuration duration, String label) {
		if (duration == null)
			eventLabels.put(timeStamp, label);
		else {
			TimeIntervalLabel<String> labeledTimeInterval = new TimeIntervalLabel<String>(timeStamp, timeStamp + duration.getDuration(), label);
			getIntervalLabels().add(labeledTimeInterval);
		}
	}

	@Override
	public void addTimeIntervalLabel(TimeInterval interval, String label) {
		TimeIntervalLabel<String> timeIntervalLabel = new TimeIntervalLabel<String>(interval, label);
		getIntervalLabels().add(timeIntervalLabel);
	}

	@Override
	public void addTimeIntervalLabel(TimeIntervalLabel<String> timeIntervalLabel) {
		getIntervalLabels().add(timeIntervalLabel);
	}

	@Override
	public SortedMap<Long, String> getEventLabels() {
		return eventLabels;
	}

	public void setEventLabels(SortedMap<Long, String> eventLabels) {
		this.eventLabels = eventLabels;
	}

	@Override
	public Ranking<TimeIntervalLabel<String>> getIntervalLabels() {
		return intervalLabels;
	}

	public void setIntervalLabels(Ranking<TimeIntervalLabel<String>> intervalLabels) {
		this.intervalLabels = intervalLabels;
	}
}
