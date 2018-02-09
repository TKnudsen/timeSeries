package com.github.TKnudsen.timeseries.data.multivariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.symbolic.ISymbolicTimeSeries;
import com.github.TKnudsen.timeseries.data.univariate.symbolic.UnivariateSymbolicTimeSeries;

/**
 * <p>
 * Title: TimeSeriesMultivariateLabeled
 * </p>
 * 
 * <p>
 * Description: Extends a TimeSeriesMultivariate with a data model for labels.
 * Implementation is based on a symbolic univariate time series for the label
 * information.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public class TimeSeriesMultivariateLabeled extends TimeSeriesMultivariate {

	private ISymbolicTimeSeries labels;

	public TimeSeriesMultivariateLabeled(List<ITimeSeriesUnivariate> timeSeriesUnivariateList, List<String> labelsForTimeStamps) {
		super(timeSeriesUnivariateList);

		initializeLabels(labelsForTimeStamps);
	}

	public TimeSeriesMultivariateLabeled(long id, List<ITimeSeriesUnivariate> timeSeriesUnivariateList, List<String> labelsForTimeStamps) {
		super(id, timeSeriesUnivariateList);

		initializeLabels(labelsForTimeStamps);
	}

	public TimeSeriesMultivariateLabeled(List<ITimeSeriesUnivariate> timeSeriesUnivariateList, List<String> timeSeriesNames, List<String> labelsForTimeStamps) {
		super(timeSeriesUnivariateList, timeSeriesNames);

		initializeLabels(labelsForTimeStamps);
	}

	public TimeSeriesMultivariateLabeled(long id, List<ITimeSeriesUnivariate> timeSeriesUnivariateList, List<String> timeSeriesNames, List<String> labelsForTimeStamps) {
		super(id, timeSeriesUnivariateList, timeSeriesNames);

		initializeLabels(labelsForTimeStamps);
	}

	public TimeSeriesMultivariateLabeled(ITimeSeriesMultivariate timeSeriesMultivariate, List<String> labelsForTimeStamps) {
		this(timeSeriesMultivariate.getID(), timeSeriesMultivariate.getTimeSeriesList(), labelsForTimeStamps);
	}

	private void initializeLabels(List<String> labelsForTimeStamps) {
		if (labelsForTimeStamps == null)
			throw new NullPointerException();

		List<Long> timeStamps = getTimestamps();
		if (timeStamps.size() != labelsForTimeStamps.size())
			throw new IllegalArgumentException("timeSeriesMultivariateLabeled: label count != time stamp count");

		labels = new UnivariateSymbolicTimeSeries(new ArrayList<>(timeStamps), labelsForTimeStamps);
	}

	public List<String> getLabelsForTimeStamps() {
		return labels.getValues();
	}

	public void setLabelsForTimeStamps(List<String> labels) {
		initializeLabels(labels);
	}

	public ISymbolicTimeSeries getLabelsTimeSeries() {
		return labels;
	}

	public void setLabelsTimeSeries(ISymbolicTimeSeries labels) {
		this.labels = labels;
	}

	@Override
	public void removeTimeValue(long timestamp) {
		super.removeTimeValue(timestamp);
		labels.removeTimeValue(timestamp);
	}

	@Override
	public void removeTimeValue(int index) {
		super.removeTimeValue(index);
		labels.removeTimeValue(index);
	}
}
