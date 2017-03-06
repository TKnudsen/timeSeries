package com.github.TKnudsen.timeseries.operations.tools;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.ITemporalLabeling;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.multivariate.TimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.multivariate.TimeSeriesMultivariateLabeled;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

/**
 * <p>
 * Title: TimeSeriesMultivariateTools
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class TimeSeriesMultivariateTools {

	public static double getMinValue(Long timeStamp, ITimeSeriesMultivariate timeSeriesMultivariate) {
		if (timeStamp == null || timeSeriesMultivariate == null)
			return Double.NaN;

		if (!timeSeriesMultivariate.containsTimestamp(timeStamp))
			return Double.NaN;

		double min = Double.POSITIVE_INFINITY;
		for (ITimeSeriesUnivariate ts : timeSeriesMultivariate.getTimeSeriesList())
			min = Math.min(min, ts.getValue(timeStamp, false));

		return min;
	}

	public static double getMaxValue(Long timeStamp, ITimeSeriesMultivariate timeSeriesMultivariate) {
		if (timeStamp == null || timeSeriesMultivariate == null)
			return Double.NaN;

		if (!timeSeriesMultivariate.containsTimestamp(timeStamp))
			return Double.NaN;

		double max = Double.NEGATIVE_INFINITY;
		for (ITimeSeriesUnivariate ts : timeSeriesMultivariate.getTimeSeriesList())
			max = Math.max(max, ts.getValue(timeStamp, false));

		return max;
	}

	public static double getMeanValue(Long timeStamp, ITimeSeriesMultivariate timeSeriesMultivariate) {
		if (timeStamp == null || timeSeriesMultivariate == null)
			return Double.NaN;

		if (!timeSeriesMultivariate.containsTimestamp(timeStamp))
			return Double.NaN;

		double mean = 0;
		double count = 0;
		for (ITimeSeriesUnivariate ts : timeSeriesMultivariate.getTimeSeriesList()) {
			mean += ts.getValue(timeStamp, false);
			count += 1;
		}

		return mean / count;
	}

	/**
	 * Provides a clone of a given multivariate time series.
	 * 
	 * @param timeSeries
	 * @return
	 */
	public static ITimeSeriesMultivariate cloneTimeSeries(ITimeSeriesMultivariate timeSeries) {
		if (timeSeries == null)
			return null;

		List<ITimeSeriesUnivariate> timeSeriesUnivariateList = new ArrayList<>();
		for (int i = 0; i < timeSeries.getDimensionality(); i++) {
			timeSeriesUnivariateList.add(TimeSeriesTools.cloneTimeSeries(timeSeries.getTimeSeries(i)));
		}

		ITimeSeriesMultivariate returnTimeSeries = new TimeSeriesMultivariate(timeSeriesUnivariateList);
		if (timeSeries.getName() != null)
			returnTimeSeries.setName(new String(timeSeries.getName()));
		else
			returnTimeSeries.setName(null);

		if (timeSeries.getDescription() != null)
			returnTimeSeries.setDescription(new String(timeSeries.getDescription()));
		else
			returnTimeSeries.setDescription(null);

		if (timeSeries instanceof ITemporalLabeling<?>) {
			ITemporalLabeling<?> returnTimeSeriesLabeled = new TimeSeriesMultivariateLabeled(returnTimeSeries);
			returnTimeSeriesLabeled.setEventLabels(TimeSeriesLabelingTools.cloneEventLabels(((TimeSeriesMultivariateLabeled) timeSeries).getEventLabels()));
			returnTimeSeriesLabeled.setIntervalLabels(TimeSeriesLabelingTools.cloneIntervalLabels(((TimeSeriesMultivariateLabeled) timeSeries).getIntervalLabels()));
			return (ITimeSeriesMultivariate) returnTimeSeriesLabeled;
		}

		return returnTimeSeries;
	}
}
