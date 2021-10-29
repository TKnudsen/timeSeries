package com.github.TKnudsen.timeseries.data.multivariate;

import java.util.List;

import com.github.TKnudsen.timeseries.data.ITimeSeries;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

/**
 * 
 * TimeSeries
 *
 * Copyright: (c) 2016-2018 Juergen Bernard,
 * https://github.com/TKnudsen/TimeSeries<br>
 * 
 * TimeSeriesMultivariate interface
 *
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public interface ITimeSeriesMultivariate extends ITimeSeries<List<Double>> {

	public int getDimensionality();

	public List<ITimeSeriesUnivariate> getTimeSeriesList();

	public ITimeSeriesUnivariate getTimeSeries(String attributeName);

	public ITimeSeriesUnivariate getTimeSeries(int attributeIndex);

	public ITimeSeriesUnivariate removeTimeSeries(String attributeName);

	public ITimeSeriesUnivariate removeTimeSeries(int attributeIndex);

	public List<String> getAttributeNames();

	public String getAttributeName(int attributeIndex);

	public List<String> getAttributeDescriptions();

	public String getAttributeDescription(int attributeIndex);

	public Double getValue(int index, String attribute);
}
