package com.github.TKnudsen.timeseries.data.univariate;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.github.TKnudsen.timeseries.data.TimeSeries;

/**
 * Univariate time series storing {@link Double} values indexed by {@code long}
 * time stamps (milliseconds since epoch).
 *
 * <p>
 * Adds:
 * </p>
 * <ul>
 * <li>Linear interpolation between neighbors</li>
 * <li>Optional key-value attributes for metadata (not part of hash or
 * equality)</li>
 * <li>Truncated {@link #toString()} for large series</li>
 * </ul>
 *
 * <p>
 * Copyright: (c) 2016-2026 Juergen Bernard,
 * https://github.com/TKnudsen/timeSeries
 * </p>
 *
 * @author Juergen Bernard
 * @version 2.0 revised in February 2026
 */
public class TimeSeriesUnivariate extends TimeSeries<Double> implements ITimeSeriesUnivariate {

	private static final int TOSTRING_MAX_ROWS_DEFAULT = 50;
	private static final int TOSTRING_BYTES_PER_ROW = 48;

	/** Optional metadata attributes - not included in hash or equality. */
	protected final SortedMap<String, Object> attributes = new TreeMap<>();

	// -----------------------------------------------------------------------
	// Construction
	// -----------------------------------------------------------------------

	/** For JSON / reflection / serialization only. */
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

	// -----------------------------------------------------------------------
	// String representation
	// -----------------------------------------------------------------------

	@Override
	public String toString() {
		return toString(TOSTRING_MAX_ROWS_DEFAULT);
	}

	/**
	 * Human-readable summary of the series, showing at most {@code maxRows} data
	 * rows.
	 *
	 * <p>
	 * Time stamps are rendered as ISO-8601 instants for readability. For large
	 * series the output is truncated.
	 * </p>
	 *
	 * @param maxRows maximum number of data rows; clamped to {@code [0, size()]}
	 * @return summary string
	 */
	public String toString(int maxRows) {
		int n = size();
		if (n == 0)
			return "TimeSeriesUnivariate: empty";

		int shown = Math.min(n, Math.max(0, maxRows));
		StringBuilder sb = new StringBuilder(128 + shown * TOSTRING_BYTES_PER_ROW);

		sb.append("Start:\t").append(Instant.ofEpochMilli(getTimestamp(0))).append(" (").append(getValue(0))
				.append(")\n");
		sb.append("End:\t").append(Instant.ofEpochMilli(getTimestamp(n - 1))).append(" (").append(getValue(n - 1))
				.append("), size: ").append(n).append("\n");

		for (int i = 0; i < shown; i++)
			sb.append(Instant.ofEpochMilli(getTimestamp(i))).append(",\t").append(getValue(i)).append('\n');

		if (shown < n)
			sb.append("...\n").append("rows shown: ").append(shown).append(" of ").append(n).append('\n');

		return sb.toString();
	}

	// -----------------------------------------------------------------------
	// Attribute store
	// -----------------------------------------------------------------------

	@Override
	public void add(String attribute, Object value) {
		if (attribute == null)
			throw new IllegalArgumentException("attribute must not be null");
		attributes.put(attribute, value);
	}

	@Override
	public Object getAttribute(String attribute) {
		return attributes.get(attribute);
	}

	@Override
	public Class<?> getType(String attribute) {
		Object v = attributes.get(attribute);
		return v != null ? v.getClass() : null;
	}

	@Override
	public Set<String> keySet() {
		return Collections.unmodifiableSet(attributes.keySet());
	}

	@Override
	public Map<String, Class<?>> getTypes() {
		Map<String, Class<?>> result = new HashMap<>((int) (attributes.size() / 0.75f) + 1);
		for (Map.Entry<String, Object> e : attributes.entrySet()) {
			Object v = e.getValue();
			result.put(e.getKey(), v != null ? v.getClass() : null);
		}
		return result;
	}

	@Override
	public Object removeAttribute(String attribute) {
		return attributes.remove(attribute);
		// attributes are not part of the hash - no resetHash needed
	}

	// -----------------------------------------------------------------------
	// TimeSeries hooks
	// -----------------------------------------------------------------------

	@Override
	protected long valueToHash(Double value) {
		return value == null ? 0L : Double.doubleToLongBits(value);
	}

	@Override
	protected Double interpolateValue(long timeStamp, long lBefore, long lAfter, Double vBefore, Double vAfter) {
		if (vBefore == null || vAfter == null)
			return getMissingValueIndicator();

		long denom = lAfter - lBefore;
		if (denom == 0)
			return vBefore;

		double alpha = (double) (timeStamp - lBefore) / (double) denom;
		return vBefore + (vAfter - vBefore) * alpha;
	}
}