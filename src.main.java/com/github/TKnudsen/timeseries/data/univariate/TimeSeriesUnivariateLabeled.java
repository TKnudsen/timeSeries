package com.github.TKnudsen.timeseries.data.univariate;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.github.TKnudsen.ComplexDataObject.data.ranking.Ranking;
import com.github.TKnudsen.timeseries.data.ITemporalLabeling;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.primitives.TimeInterval;
import com.github.TKnudsen.timeseries.data.primitives.TimeIntervalLabel;

/**
 * <p>
 * Title: TimeSeriesUnivariateLabeled
 * </p>
 * 
 * <p>
 * Description: wraps an univariate time series with labeling support.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public class TimeSeriesUnivariateLabeled extends TimeSeriesUnivariate implements ITemporalLabeling<String> {

	private SortedMap<Long, String> eventLabels = new TreeMap<>();
	private Ranking<TimeIntervalLabel<String>> intervalLabels = new Ranking<TimeIntervalLabel<String>>();

	public TimeSeriesUnivariateLabeled(List<Long> timestamps, List<Double> values) {
		super(timestamps, values);
	}

	public TimeSeriesUnivariateLabeled(List<Long> timestamps, List<Double> values, Double missingValueIndicator) {
		super(timestamps, values, missingValueIndicator);
	}

	public TimeSeriesUnivariateLabeled(long id, List<Long> timestamps, List<Double> values) {
		super(id, timestamps, values);
	}

	public TimeSeriesUnivariateLabeled(long id, List<Long> timestamps, List<Double> values, Double missingValueIndicator) {
		super(id, timestamps, values, missingValueIndicator);
	}

	public TimeSeriesUnivariateLabeled(ITimeSeriesUnivariate ts) {
		super(ts.getID(), ts.getTimestamps(), ts.getValues(), ts.getMissingValueIndicator());
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
