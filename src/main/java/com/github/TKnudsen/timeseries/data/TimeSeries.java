package com.github.TKnudsen.timeseries.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.github.TKnudsen.ComplexDataObject.model.tools.MathFunctions;

/**
 * Abstract base class for time series storing ordered (time stamp, value) pairs
 * in parallel lists.
 *
 * <p>
 * Lists are accepted directly - no defensive copying for performance. Callers
 * must not modify the lists after passing them to the constructor.
 * </p>
 *
 * <p>
 * <b>Invariants:</b>
 * </p>
 * <ul>
 * <li>{@code timeStamps} and {@code values} are non-null</li>
 * <li>{@code timeStamps.size() == values.size()}</li>
 * <li>Time stamps are strictly ascending and unique</li>
 * </ul>
 *
 * <p>
 * Hash code is cached and invalidated on any structural mutation.
 * </p>
 *
 * @param <V> the value type
 *
 * @author Juergen Bernard
 * @version 2.0 revised February 2026
 */
public abstract class TimeSeries<V> implements ITimeSeries<V> {

	private final long id;
	private String name;
	private String description;

	protected final List<Long> timeStamps;
	protected final List<V> values;

	private V missingValueIndicator;

	private boolean hashDirty = true;
	private int cachedHash;

	// -----------------------------------------------------------------------
	// Construction
	// -----------------------------------------------------------------------

	protected TimeSeries() {
		this(MathFunctions.randomLong(), new ArrayList<>(), new ArrayList<>(), true);
	}

	public TimeSeries(List<Long> timeStamps, List<V> values) {
		this(MathFunctions.randomLong(), timeStamps, values, true);
	}

	public TimeSeries(long id, List<Long> timeStamps, List<V> values) {
		this(id, timeStamps, values, true);
	}

	/**
	 * Primary constructor. All other constructors delegate here.
	 *
	 * @param validate if {@code false}, invariants are assumed - use only for
	 *                 trusted inputs from internal factory paths
	 */
	protected TimeSeries(long id, List<Long> timeStamps, List<V> values, boolean validate) {
		this.id = id;
		this.timeStamps = timeStamps;
		this.values = values;
		this.missingValueIndicator = null;
		if (validate)
			validateInvariants();
		resetHash();
	}

	private void validateInvariants() {
		if (timeStamps == null)
			throw new IllegalArgumentException(getClass().getSimpleName() + ": timeStamps must not be null");
		if (values == null)
			throw new IllegalArgumentException(getClass().getSimpleName() + ": values must not be null");
		if (timeStamps.size() != values.size())
			throw new IllegalArgumentException(getClass().getSimpleName() + ": size mismatch (" + timeStamps.size()
					+ " vs " + values.size() + ")");
		for (int i = 0; i < timeStamps.size() - 1; i++)
			if (timeStamps.get(i) >= timeStamps.get(i + 1))
				throw new IllegalArgumentException(
						getClass().getSimpleName() + ": timestamps must be strictly ascending and unique");
	}

	protected final void resetHash() {
		hashDirty = true;
	}

	// -----------------------------------------------------------------------
	// Identity and equality
	// -----------------------------------------------------------------------

	@Override
	public final long getID() {
		return id;
	}

	@Override
	public final int hashCode() {
		if (!hashDirty)
			return cachedHash;

		int h = 23;
		for (V v : values) {
			long l = valueToHash(v);
			h = 31 * h + (int) (l ^ (l >>> 32));
		}
		// mix both halves of each time stamp to avoid hash collisions between
		// series whose time stamps differ only in the high 32 bits
		for (int i = 0; i < timeStamps.size(); i++) {
			long t = timeStamps.get(i);
			h = 23 * h + (int) (t ^ (t >>> 32));
		}
		cachedHash = h;
		hashDirty = false;
		return cachedHash;
	}

	/**
	 * Returns a {@code long} hash of a single value for use in {@link #hashCode()}.
	 */
	protected abstract long valueToHash(V value);

	@Override
	public final boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (getClass() != obj.getClass())
			return false;

		TimeSeries<?> other = (TimeSeries<?>) obj;

		int n = size();
		if (n != other.size())
			return false;
		if (hashCode() != other.hashCode())
			return false;

		for (int i = 0; i < n; i++) {
			if (getTimestamp(i) != other.getTimestamp(i))
				return false;
			if (!Objects.equals(getValue(i), other.getValue(i)))
				return false;
		}
		return true;
	}

	// -----------------------------------------------------------------------
	// Metadata
	// -----------------------------------------------------------------------

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final void setName(String name) {
		this.name = name;
	}

	@Override
	public final String getDescription() {
		return description;
	}

	@Override
	public final void setDescription(String desc) {
		this.description = desc;
	}

	// -----------------------------------------------------------------------
	// Size and missing value
	// -----------------------------------------------------------------------

	@Override
	public final int size() {
		return timeStamps.size();
	}

	@Override
	public final boolean isEmpty() {
		return timeStamps.isEmpty();
	}

	@Override
	public final V getMissingValueIndicator() {
		return missingValueIndicator;
	}

	public final void setMissingValueIndicator(V missingValueIndicator) {
		this.missingValueIndicator = missingValueIndicator;
	}

	// -----------------------------------------------------------------------
	// Index-based access
	// -----------------------------------------------------------------------

	@Override
	public final long getTimestamp(int index) {
		if (index < 0 || index >= timeStamps.size())
			throw new IndexOutOfBoundsException("TimeSeries: index " + index + " out of bounds for size " + size());
		return timeStamps.get(index);
	}

	@Override
	public final V getValue(int index) {
		if (index < 0 || index >= values.size())
			throw new IndexOutOfBoundsException("TimeSeries: index " + index + " out of bounds for size " + size());
		return values.get(index);
	}

	// -----------------------------------------------------------------------
	// Search primitives
	// -----------------------------------------------------------------------

	/**
	 * Exact-match binary search.
	 *
	 * @return the index of {@code timeStamp}, or {@code -1} if not found
	 */
	protected final int indexOfExact(long timeStamp) {
		if (isEmpty())
			return -1;

		long first = timeStamps.get(0);
		long last = timeStamps.get(timeStamps.size() - 1);
		if (timeStamp < first || timeStamp > last)
			return -1;

		int lo = 0;
		int hi = timeStamps.size() - 1;
		while (lo <= hi) {
			int mid = (lo + hi) >>> 1;
			long t = timeStamps.get(mid);
			if (t < timeStamp)
				lo = mid + 1;
			else if (t > timeStamp)
				hi = mid - 1;
			else
				return mid;
		}
		return -1;
	}

	/**
	 * Floor binary search.
	 *
	 * <p>
	 * Returns the largest index {@code i} such that
	 * {@code timeStamps[i] <= timeStamp}.
	 * </p>
	 *
	 * <p>
	 * <b>Precondition:</b> series is non-empty and {@code timeStamp} is within
	 * {@code [timeStamps[0], timeStamps[size-1]]}. Violating this precondition
	 * produces an unspecified result - callers are responsible for range validation
	 * before calling.
	 * </p>
	 */
	protected final int floorIndexInRange(long timeStamp) {
		int lo = 0;
		int hi = timeStamps.size() - 1;
		while (lo <= hi) {
			int mid = (lo + hi) >>> 1;
			if (timeStamps.get(mid) <= timeStamp)
				lo = mid + 1;
			else
				hi = mid - 1;
		}
		return hi;
	}

	// -----------------------------------------------------------------------
	// Timestamp-based access
	// -----------------------------------------------------------------------

	@Override
	public final V getValue(long timeStamp, boolean allowInterpolation)
			throws IndexOutOfBoundsException, IllegalArgumentException {
		if (isEmpty())
			throw new IllegalArgumentException("TimeSeries.getValue: series is empty");

		long first = timeStamps.get(0);
		long last = timeStamps.get(timeStamps.size() - 1);
		if (timeStamp < first || timeStamp > last)
			throw new IllegalArgumentException("TimeSeries.getValue: timestamp outside series range");

		// exact lookup - O(log n), covers the common case
		int exact = indexOfExact(timeStamp);
		if (exact >= 0)
			return getValue(exact);

		if (!allowInterpolation)
			throw new IllegalArgumentException("TimeSeries.getValue: timestamp not found");

		// interpolation - floor search is safe: range already validated above
		int left = floorIndexInRange(timeStamp);
		int right = left + 1;
		if (left < 0 || right >= size())
			throw new IndexOutOfBoundsException("TimeSeries.getValue: interpolation needs two neighbours");

		return interpolateValue(timeStamp, getTimestamp(left), getTimestamp(right), getValue(left), getValue(right));
	}

	/** Interpolates a value between two neighboring entries. */
	protected abstract V interpolateValue(long timeStamp, long lBefore, long lAfter, V vBefore, V vAfter);

	@Override
	public final int findByDate(long timeStamp, boolean requireExactMatch) throws IllegalArgumentException {
		if (isEmpty())
			throw new IllegalArgumentException("TimeSeries.findByDate: series is empty");

		long first = timeStamps.get(0);
		long last = timeStamps.get(timeStamps.size() - 1);
		if (timeStamp < first || timeStamp > last)
			throw new IllegalArgumentException(
					"TimeSeries.findByDate: timestamp outside series range [" + first + ", " + last + "]");

		// exact lookup first - avoids a second search for the common case
		int exact = indexOfExact(timeStamp);
		if (exact >= 0)
			return exact;

		if (!requireExactMatch)
			return floorIndexInRange(timeStamp);

		throw new IllegalArgumentException("TimeSeries.findByDate: timestamp " + timeStamp + " not found");
	}

	@Override
	public final boolean containsTimestamp(long timeStamp) {
		return indexOfExact(timeStamp) >= 0;
	}

	// -----------------------------------------------------------------------
	// Boundary access
	// -----------------------------------------------------------------------

	@Override
	public final long getFirstTimestamp() {
		if (isEmpty())
			throw new IllegalStateException("TimeSeries: series is empty");
		return timeStamps.get(0);
	}

	@Override
	public final long getLastTimestamp() {
		if (isEmpty())
			throw new IllegalStateException("TimeSeries: series is empty");
		return timeStamps.get(timeStamps.size() - 1);
	}

	@Override
	public final List<Long> getTimestamps() {
		return Collections.unmodifiableList(timeStamps);
	}

	@Override
	public final List<V> getValues() {
		return Collections.unmodifiableList(values);
	}

	// -----------------------------------------------------------------------
	// Mutation
	// -----------------------------------------------------------------------

	@Override
	public final void insert(long timeStamp, V value) {
		if (isEmpty()) {
			timeStamps.add(timeStamp);
			values.add(value);
			resetHash();
			return;
		}

		long first = timeStamps.get(0);
		long last = timeStamps.get(timeStamps.size() - 1);

		if (timeStamp > last) {
			timeStamps.add(timeStamp);
			values.add(value);
			resetHash();
			return;
		}
		if (timeStamp < first) {
			timeStamps.add(0, timeStamp);
			values.add(0, value);
			resetHash();
			return;
		}

		// in-range: exact check first to decide replace vs insert
		int exact = indexOfExact(timeStamp);
		if (exact >= 0) {
			values.set(exact, value);
			resetHash();
			return;
		}

		int left = floorIndexInRange(timeStamp);
		timeStamps.add(left + 1, timeStamp);
		values.add(left + 1, value);
		resetHash();
	}

	@Override
	public final void removeTimeValue(long timeStamp) {
		int idx = indexOfExact(timeStamp);
		if (idx < 0)
			return;
		timeStamps.remove(idx);
		values.remove(idx);
		resetHash();
	}

	@Override
	public final void removeTimeValue(int index) {
		if (index < 0 || index >= size())
			throw new IndexOutOfBoundsException("TimeSeries: index " + index + " out of bounds for size " + size());
		timeStamps.remove(index);
		values.remove(index);
		resetHash();
	}

	@Override
	public final void replaceValue(int index, V value) {
		if (index < 0 || index >= size())
			throw new IndexOutOfBoundsException("TimeSeries: index " + index + " out of bounds for size " + size());
		values.set(index, value);
		resetHash();
	}

	@Override
	public final void replaceValue(long timeStamp, V value) {
		int idx = findByDate(timeStamp, true);
		values.set(idx, value);
		resetHash();
	}
}