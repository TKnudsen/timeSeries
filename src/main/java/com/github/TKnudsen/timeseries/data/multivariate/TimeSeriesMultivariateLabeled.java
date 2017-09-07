package com.github.TKnudsen.timeseries.data.multivariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.symbolic.ISymbolicTimeSeries;
import com.github.TKnudsen.timeseries.data.univariate.symbolic.SymbolicTimeSeries;

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
 * @version 1.02
 */
public class TimeSeriesMultivariateLabeled extends TimeSeriesMultivariate {

	private ISymbolicTimeSeries labels;

	public TimeSeriesMultivariateLabeled(List<ITimeSeriesUnivariate> timeSeriesUnivariateList, List<String> labels) {
		super(timeSeriesUnivariateList);

		initializeLabels(labels);
	}

	public TimeSeriesMultivariateLabeled(long id, List<ITimeSeriesUnivariate> timeSeriesUnivariateList, List<String> labels) {
		super(id, timeSeriesUnivariateList);

		initializeLabels(labels);
	}

	public TimeSeriesMultivariateLabeled(List<ITimeSeriesUnivariate> timeSeriesUnivariateList, List<String> timeSeriesNames, List<String> labels) {
		super(timeSeriesUnivariateList, timeSeriesNames);

		initializeLabels(labels);
	}

	public TimeSeriesMultivariateLabeled(long id, List<ITimeSeriesUnivariate> timeSeriesUnivariateList, List<String> timeSeriesNames, List<String> labels) {
		super(id, timeSeriesUnivariateList, timeSeriesNames);

		initializeLabels(labels);
	}

	public TimeSeriesMultivariateLabeled(ITimeSeriesMultivariate timeSeriesMultivariate, List<String> labels) {
		this(timeSeriesMultivariate.getID(), timeSeriesMultivariate.getTimeSeriesList(), labels);

		initializeLabels(labels);
	}

	private void initializeLabels(List<String> labelInformation) {
		if (labelInformation == null)
			throw new NullPointerException();

		List<Long> timeStamps = getTimestamps();
		if (timeStamps.size() != labelInformation.size())
			throw new IllegalArgumentException("timeSeriesMultivariateLabeled: label count != time stamp count");

		labels = new SymbolicTimeSeries(new ArrayList<>(timeStamps), labelInformation);
	}

	public List<String> getLabels() {
		return labels.getValues();
	}

	public void setLabels(List<String> labels) {
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
