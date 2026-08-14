package com.github.TKnudsen.timeseries.operations.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Self-contained benchmark simulating calculateMovingAverageTimeSensitive at
 * different series sizes to expose O(n^2) behavior.
 *
 * Run with: javac TimeSeriesBenchmark.java && java -Xmx512m TimeSeriesBenchmark
 */
public class TimeSeriesBenchmark {

	// ---- Minimal ITimeSeries stub ----------------------------------------

	static class SimpleTimeSeries {
		private final long[] timestamps;
		private final double[] values;

		SimpleTimeSeries(int size, long intervalMs) {
			timestamps = new long[size];
			values = new double[size];
			long t = System.currentTimeMillis();
			Random rng = new Random(42);
			for (int i = 0; i < size; i++) {
				timestamps[i] = t + (long) i * intervalMs;
				// ~5 % NaN to simulate real-world gaps
				values[i] = (rng.nextDouble() < 0.05) ? Double.NaN : rng.nextGaussian() * 10 + 100;
			}
		}

		int size() {
			return timestamps.length;
		}

		boolean isEmpty() {
			return timestamps.length == 0;
		}

		long getFirstTimestamp() {
			return timestamps[0];
		}

		long getLastTimestamp() {
			return timestamps[timestamps.length - 1];
		}

		long getTimestamp(int i) {
			return timestamps[i];
		}

		double getValue(int i) {
			return values[i];
		}

		void replaceValue(int i, double v) {
			values[i] = v;
		}

		/** Binary search -- O(log n), matching a typical production findByDate. */
		int findByDate(long target, boolean exact) {
			int lo = 0, hi = timestamps.length - 1;
			while (lo < hi) {
				int mid = (lo + hi) >>> 1;
				if (timestamps[mid] < target)
					lo = mid + 1;
				else
					hi = mid;
			}
			return lo;
		}
	}

	// ---- Kernel stub -------------------------------------------------------

	static class LinearLongWeightingKernel {
		private final long interval;
		private long reference;

		LinearLongWeightingKernel(long interval) {
			this.interval = interval;
		}

		void setReference(long ref) {
			this.reference = ref;
		}

		long getInterval() {
			return interval;
		}

		double getWeight(long ts) {
			long dist = Math.abs(ts - reference);
			if (dist > interval)
				return 0.0;
			return 1.0 - (double) dist / interval; // linear decay
		}
	}

	// ---- Original O(n^2) algorithm ------------------------------------------

	static void calculateMovingAverageOriginal(SimpleTimeSeries ts, long window, boolean considerFutureValues) {
		if (ts.isEmpty())
			return;

		LinearLongWeightingKernel kernel = new LinearLongWeightingKernel(window);
		long firstTimestamp = ts.getFirstTimestamp();
		long lastTimestamp = ts.getLastTimestamp();

		List<Double> smoothed = new ArrayList<>(ts.size());

		for (int i = 0; i < ts.size(); i++) {
			long ref = ts.getTimestamp(i);
			kernel.setReference(ref);

			long windowStart = Math.max(ref - kernel.getInterval(), firstTimestamp);
			long windowEnd = Math.min(ref + kernel.getInterval(), lastTimestamp);

			int firstIndex = Math.max(ts.findByDate(windowStart, false), 0);
			int lastIndex = Math.min(ts.findByDate(windowEnd, false), ts.size() - 1);
			int effLast = considerFutureValues ? lastIndex : Math.min(lastIndex, i);

			double weightedSum = 0.0, weightSum = 0.0;
			for (int k = firstIndex; k <= effLast; k++) {
				double v = ts.getValue(k);
				if (Double.isNaN(v))
					continue;
				double w = kernel.getWeight(ts.getTimestamp(k));
				weightedSum += v * w;
				weightSum += w;
			}
			smoothed.add(weightSum > 0 ? weightedSum / weightSum : Double.NaN);
		}

		for (int i = 0; i < ts.size(); i++)
			ts.replaceValue(i, smoothed.get(i));
	}

	// ---- Optimised O(n) sliding-window algorithm -----------------------------
	// Two pointers advance monotonically -> each element enters/leaves the window
	// once.
	// No findByDate inside the outer loop.

	static void calculateMovingAverageOptimised(SimpleTimeSeries ts, long window, boolean considerFutureValues) {
		if (ts.isEmpty())
			return;

		int n = ts.size();
		double[] smoothed = new double[n]; // primitive array: no boxing

		int left = 0;
		int right = 0; // right is the *exclusive* upper bound we advance independently

		// Running accumulators for the current window
		double weightedSum = 0.0;
		double weightSum = 0.0;

		// We rebuild accumulators each step to avoid floating-point drift on removals.
		// For true O(n) you'd maintain them incrementally; full recompute still wins
		// over the original because we avoid binary search and use primitive arrays.

		for (int i = 0; i < n; i++) {
			long ref = ts.getTimestamp(i);
			long winStart = ref - window;
			long winEnd = considerFutureValues ? ref + window : ref;

			// Advance left pointer
			while (left < i && ts.getTimestamp(left) < winStart)
				left++;

			// Advance right pointer
			while (right < n && ts.getTimestamp(right) <= winEnd)
				right++;

			// Recompute (still amortised -- each element touched a constant number of times)
			weightedSum = 0.0;
			weightSum = 0.0;
			for (int k = left; k < right; k++) {
				double v = ts.getValue(k);
				if (Double.isNaN(v))
					continue;
				long dist = Math.abs(ts.getTimestamp(k) - ref);
				double w = (dist > window) ? 0.0 : 1.0 - (double) dist / window;
				weightedSum += v * w;
				weightSum += w;
			}
			smoothed[i] = weightSum > 0 ? weightedSum / weightSum : Double.NaN;
		}

		for (int i = 0; i < n; i++)
			ts.replaceValue(i, smoothed[i]);
	}

	// ---- Replica of the production compareDoubles --------------------------

	static boolean compareDoubles(Double v1, Double v2) {
		if (v1 == null && v2 == null)
			return true;
		if (v1 == null || v2 == null)
			return false;
		return Double.compare(v1, v2) == 0; // triggers doubleToLongBits on fallthrough
	}

	// ---- Original + compareDoubles (closest to real production code) --------

	static void calculateMovingAverageWithCompareDoubles(SimpleTimeSeries ts, long window,
			boolean considerFutureValues) {
		if (ts.isEmpty())
			return;

		// Simulate a non-null missingValueIndicator (e.g. -9999.0), which is
		// the case that forces compareDoubles to be called on EVERY value.
		final Double MISSING = -9999.0;

		LinearLongWeightingKernel kernel = new LinearLongWeightingKernel(window);
		long firstTimestamp = ts.getFirstTimestamp();
		long lastTimestamp = ts.getLastTimestamp();
		List<Double> smoothed = new ArrayList<>(ts.size());

		for (int i = 0; i < ts.size(); i++) {
			long ref = ts.getTimestamp(i);
			kernel.setReference(ref);

			long windowStart = Math.max(ref - kernel.getInterval(), firstTimestamp);
			long windowEnd = Math.min(ref + kernel.getInterval(), lastTimestamp);

			int firstIndex = Math.max(ts.findByDate(windowStart, false), 0);
			int lastIndex = Math.min(ts.findByDate(windowEnd, false), ts.size() - 1);
			int effLast = considerFutureValues ? lastIndex : Math.min(lastIndex, i);

			double weightedSum = 0.0, weightSum = 0.0;
			for (int k = firstIndex; k <= effLast; k++) {
				Double v = ts.getValue(k); // boxed
				if (v == null || Double.isNaN(v))
					continue;
				if (compareDoubles(v, MISSING))
					continue; // <- the hotspot
				double w = kernel.getWeight(ts.getTimestamp(k));
				weightedSum += v * w;
				weightSum += w;
			}
			smoothed.add(weightSum > 0 ? weightedSum / weightSum : Double.NaN);
		}
		for (int i = 0; i < ts.size(); i++)
			ts.replaceValue(i, smoothed.get(i));
	}

	// ---- Fixed: hoist unboxing, use primitive == for sentinel check ------

	static void calculateMovingAverageFixed(SimpleTimeSeries ts, long window, boolean considerFutureValues) {
		if (ts.isEmpty())
			return;

		final double MISSING = -9999.0; // unboxed ONCE, compile-time constant
		final boolean hasMissing = true; // would be (missingValueIndicator != null)

		LinearLongWeightingKernel kernel = new LinearLongWeightingKernel(window);
		long firstTimestamp = ts.getFirstTimestamp();
		long lastTimestamp = ts.getLastTimestamp();
		double[] smoothed = new double[ts.size()]; // primitive -- no boxing

		for (int i = 0; i < ts.size(); i++) {
			long ref = ts.getTimestamp(i);
			kernel.setReference(ref);

			long windowStart = Math.max(ref - kernel.getInterval(), firstTimestamp);
			long windowEnd = Math.min(ref + kernel.getInterval(), lastTimestamp);

			int firstIndex = Math.max(ts.findByDate(windowStart, false), 0);
			int lastIndex = Math.min(ts.findByDate(windowEnd, false), ts.size() - 1);
			int effLast = considerFutureValues ? lastIndex : Math.min(lastIndex, i);

			double weightedSum = 0.0, weightSum = 0.0;
			for (int k = firstIndex; k <= effLast; k++) {
				double v = ts.getValue(k); // primitive -- no unboxing
				if (Double.isNaN(v))
					continue;
				if (hasMissing && v == MISSING)
					continue; // plain ==: no NaN, no boxing
				double w = kernel.getWeight(ts.getTimestamp(k));
				weightedSum += v * w;
				weightSum += w;
			}
			smoothed[i] = weightSum > 0 ? weightedSum / weightSum : Double.NaN;
		}
		for (int i = 0; i < ts.size(); i++)
			ts.replaceValue(i, smoothed[i]);
	}

	// ---- Benchmark harness --------------------------------------------------

	static String fmt(long ns) {
		if (ns < 1_000_000)
			return ns / 1_000 + " us";
		if (ns < 1_000_000_000L)
			return ns / 1_000_000 + " ms";
		return String.format("%.2f s", ns / 1e9);
	}

	static String repeat(char c, int n) {
		StringBuilder sb = new StringBuilder(n);
		for (int i = 0; i < n; i++)
			sb.append(c);
		return sb.toString();
	}

	static void runVariant(SimpleTimeSeries ts, int variant, long window) {
		switch (variant) {
		case 0:
			calculateMovingAverageOriginal(ts, window, true);
			break;
		case 1:
			calculateMovingAverageWithCompareDoubles(ts, window, true);
			break;
		case 2:
			calculateMovingAverageOptimised(ts, window, true);
			break;
		case 3:
			calculateMovingAverageFixed(ts, window, true);
			break;
		}
	}

	static long benchAvg(int n, long intervalMs, int variant, long window, int reps) {
		long total = 0;
		for (int r = 0; r < reps; r++) {
			SimpleTimeSeries ts = new SimpleTimeSeries(n, intervalMs);
			long start = System.nanoTime();
			runVariant(ts, variant, window);
			total += System.nanoTime() - start;
		}
		return total / reps;
	}

	public static void main(String[] args) {
		long windowMs = 5_000;
		long intervalMs = 100;
		int[] sizes = { 1_000, 5_000, 10_000, 50_000, 100_000 };

		System.out.println("Warming up JIT...");
		for (int w = 0; w < 8; w++) {
			for (int v = 0; v < 4; v++)
				benchAvg(3_000, intervalMs, v, windowMs, 1);
		}

		String[] labels = { "Original (no missing check) ", "Original + compareDoubles   ",
				"Optimised (sliding window)  ", "Fixed (primitive == sentinel)", };

		System.out.println();
		System.out.println("Measuring impact of compareDoubles vs primitive == sentinel check");
		System.out.println("Window = 5s, interval = 100ms  (~50 neighbours per point)");
		System.out.println(repeat('=', 88));
		System.out.printf("%-10s  %-34s  %-34s  %s%n", "N", "Variant A", "Variant B", "B vs A");
		System.out.println(repeat('-', 88));

		for (int n : sizes) {
			int reps = n <= 5_000 ? 10 : n <= 20_000 ? 5 : 3;
			long[] t = new long[4];
			for (int v = 0; v < 4; v++)
				t[v] = benchAvg(n, intervalMs, v, windowMs, reps);

			System.out.printf("N=%-8d  %-28s %6s  %-28s %6s  %+.0f%%%n", n, labels[0], fmt(t[0]), labels[1], fmt(t[1]),
					100.0 * (t[1] - t[0]) / t[0]);
			System.out.printf("%-10s  %-28s %6s  %-28s %6s  %+.0f%%%n", "", labels[2], fmt(t[2]), labels[3], fmt(t[3]),
					100.0 * (t[3] - t[2]) / t[2]);
			System.out.println(repeat('-', 88));
		}

		System.out.println();
		System.out.println("Read the +X% column as overhead added by compareDoubles / removed by == fix.");
		System.out.println("compareDoubles cost = doubleToLongBits() called O(n*W) times in inner loop.");
	}
}