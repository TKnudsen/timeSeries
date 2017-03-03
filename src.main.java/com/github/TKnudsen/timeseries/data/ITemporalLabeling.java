package com.github.TKnudsen.timeseries.data;

import java.util.SortedMap;

import com.github.TKnudsen.ComplexDataObject.data.ranking.Ranking;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.primitives.TimeInterval;
import com.github.TKnudsen.timeseries.data.primitives.TimeIntervalLabel;

/**
 * <p>
 * Title: ITemporalLabeling
 * </p>
 * 
 * <p>
 * Description: models labeling tasks for time-oriented data
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public interface ITemporalLabeling<O extends Object> {

	public void addEventLabel(long timeStamp, O label);

	public void addTimeDurationLabel(long timeStamp, TimeDuration duration, O label);

	public void addTimeIntervalLabel(TimeInterval interval, O label);

	public void addTimeIntervalLabel(TimeIntervalLabel<O> timeIntervalLabel);

	public SortedMap<Long, O> getEventLabels();

	public Ranking<TimeIntervalLabel<O>> getIntervalLabels();
}
