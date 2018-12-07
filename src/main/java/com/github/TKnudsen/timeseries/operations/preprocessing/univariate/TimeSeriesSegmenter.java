package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import com.github.TKnudsen.ComplexDataObject.data.interfaces.ISelfDescription;
import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.TimeSeriesProcessor;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * <p>
 * Title: TimeSeriesSegmenter
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.05
 */
public class TimeSeriesSegmenter extends TimeSeriesProcessor<ITimeSeriesUnivariate> implements ISelfDescription {

	private TimeDuration timeInterval = null;

	@SuppressWarnings("unused")
	private TimeSeriesSegmenter() {
	}

	public TimeSeriesSegmenter(TimeDuration timeInterval) {
		this.timeInterval = timeInterval;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void process(List<ITimeSeriesUnivariate> timeSeries) {
		if (timeSeries == null)
			return;

		List<ITimeSeriesUnivariate> retList = new ArrayList<>();

		for (ITimeSeriesUnivariate timeSeriesUnivariate : timeSeries) {
			retList.addAll(TimeSeriesTools.segmentTimeSeries(timeSeriesUnivariate, getTimeInterval()));
		}

		// this is somehow critical since the original list is altered. However,
		// that's the problem with TS segmentation...
		timeSeries = retList;
	}

	@Override
	public String toString() {
		return "TimeSeriesSegmenter";
	}

	@Override
	public String getName() {
		return "Timeseries Segmenter";
	}

	@Override
	public String getDescription() {
		if (getTimeInterval() != null)
			return "Timeseries Segmenter: segmentation pattern: " + getTimeInterval().toString();
		return "Timeseries Segmenter";
	}

	public TimeDuration getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(TimeDuration timeInterval) {
		this.timeInterval = timeInterval;
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_SEGMENTATION;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesUnivariate>> getAlternativeParameterizations(int count) {
		List<Long> longs = ParameterSupportTools.getAlternativeLongs(timeInterval.getDuration(), count);

		List<IDataProcessor<ITimeSeriesUnivariate>> processors = new ArrayList<>();
		for (Long l : longs)
			processors.add(new TimeSeriesSegmenter(new TimeDuration(timeInterval.getType(), l)));

		return processors;
	}
}
