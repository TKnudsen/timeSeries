package com.github.TKnudsen.timeseries.data.multivariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.symbolic.univariate.ITimeSeriesUnivariateSymbolic;
import com.github.TKnudsen.timeseries.data.symbolic.univariate.TimeSeriesUnivariateSymbolic;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

public class TimeSeriesMultivariateLabeled extends TimeSeriesMultivariate {

	private ITimeSeriesUnivariateSymbolic labels;

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

		labels = new TimeSeriesUnivariateSymbolic(new ArrayList<>(timeStamps), labelInformation);
	}

	public List<String> getLabels() {
		return labels.getValues();
	}

	public void setLabels(List<String> labels) {
		initializeLabels(labels);
	}

	public ITimeSeriesUnivariateSymbolic getLabelsTimeSeries() {
		return labels;
	}

	public void setLabelsTimeSeries(ITimeSeriesUnivariateSymbolic labels) {
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
