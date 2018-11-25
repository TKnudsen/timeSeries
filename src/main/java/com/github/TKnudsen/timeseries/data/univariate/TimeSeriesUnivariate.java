package com.github.TKnudsen.timeseries.data.univariate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.github.TKnudsen.timeseries.data.TimeSeries;

/**
 * <p>
 * timeSeries
 * </p>
 * 
 * <p>
 * Models an univariate time series, i.e., data structure that stores univariate
 * phenomena observed over time, expressed with numerical values.
 * </p>
 * 
 * <p>
 * Copyright: (c) 2016-2018 Juergen Bernard,
 * https://github.com/TKnudsen/timeSeries
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.05
 */
public class TimeSeriesUnivariate extends TimeSeries<Double> implements ITimeSeriesUnivariate {

	protected SortedMap<String, Object> attributes = new TreeMap<String, Object>();

	/**
	 * used for JSON, reflection, serialization & stuff
	 */
	@SuppressWarnings("unused")
	private TimeSeriesUnivariate() {
		super();
	}

	public TimeSeriesUnivariate(List<Long> timeStamps, List<Double> values) {
		super(timeStamps, values);
	}

	public TimeSeriesUnivariate(List<Long> timeStamps, List<Double> values, Double missingValueIndicator) {
		super(timeStamps, values);

		setMissingValueIndicator(missingValueIndicator);
	}

	public TimeSeriesUnivariate(long id, List<Long> timeStamps, List<Double> values) {
		super(id, timeStamps, values);
	}

	public TimeSeriesUnivariate(long id, List<Long> timeStamps, List<Double> values, Double missingValueIndicator) {
		super(id, timeStamps, values);

		setMissingValueIndicator(missingValueIndicator);
	}

	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < this.size(); i++)
			stringBuffer
					.append(new Date(timeStamps.get(i)).toString() + ", " + String.format("%f", values.get(i)) + "\n");

		return stringBuffer.toString();
	}

	@Override
	public void add(String attribute, Object value) {
		attributes.put(attribute, value);
	}

	@Override
	public Object getAttribute(String attribute) {
		return attributes.get(attribute);
	}

	@Override
	public Class<?> getType(String attribute) {
		if (attributes.get(attribute) != null)
			return attributes.get(attribute).getClass();
		return null;
	}

	@Override
	public Set<String> keySet() {
		return attributes.keySet();
	}

	@Override
	public Map<String, Class<?>> getTypes() {
		Map<String, Class<?>> ret = new HashMap<>();
		for (String string : attributes.keySet())
			if (attributes.get(string) == null)
				ret.put(string, null);
			else
				ret.put(string, attributes.get(string).getClass());
		return ret;
	}

	@Override
	public Object removeAttribute(String attribute) {
		if (attributes.get(attribute) != null)
			return attributes.remove(attribute);
		return null;

		// no resetHash: attributes are not part of the value-building hash
		// components
	}

	@Override
	protected long valueToHash(Double value) {
		return Double.doubleToLongBits(value);
	}

	@Override
	protected Double interpolateValue(long timeStamp, long lBefore, long lAfter, Double vBefore, Double vAfter) {
		long deltaBefore = timeStamp - lBefore;
		Double value = vBefore + ((vAfter - vBefore) * ((double) deltaBefore / (lAfter - lBefore)));
		return value;
	}

}
