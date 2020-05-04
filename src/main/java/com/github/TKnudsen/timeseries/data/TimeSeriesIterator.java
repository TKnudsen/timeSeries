package com.github.TKnudsen.timeseries.data;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

class TimeSeriesIterator<V> implements Iterator<Entry<Long, V>> {

	private final List<Long> timeStamps;
	private final List<V> values;

	private int index = 0;

	/**
	 * iterates over timestamps and returns time value pairs. With this iterator,
	 * traversing the time stamps but retrieving the values can be done with linear
	 * time. Otherwise, values have to be looked up using timeStamps as primary
	 * keys, runtime would be quadratic in the worst case.
	 * 
	 * @param timeStamps
	 * @param values
	 */
	public TimeSeriesIterator(List<Long> timeStamps, List<V> values) {
		this.timeStamps = timeStamps;
		this.values = values;
	}

	public boolean hasNext() {
		if (index < timeStamps.size())
			return true;
		else
			return false;
	}

	public Entry<Long, V> next() {
		if (this.hasNext()) {
			Long timeStamp = timeStamps.get(index);
			V v = values.get(index++);

			return new AbstractMap.SimpleEntry<Long, V>(timeStamp, v);
		} else
			return null;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove operation not allowed when using the iterator");
	}
}
