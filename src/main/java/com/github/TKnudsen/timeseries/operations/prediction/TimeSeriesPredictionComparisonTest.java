package com.github.TKnudsen.timeseries.operations.prediction;

import java.util.Date;

import com.github.TKnudsen.ComplexDataObject.data.time.TimeDuration;
import com.github.TKnudsen.ComplexDataObject.data.time.TimeQuantization;
import com.github.TKnudsen.ComplexDataObject.model.tools.DateTools;
import com.github.TKnudsen.timeseries.data.dataGeneration.TimeSeriesGenerator;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * Manual executor for {@link TimeSeriesPredictionComparison}.
 *
 * <p>
 * Generates synthetic time series at different scales and runs the full
 * comparison suite: single-horizon, standard-horizons table, and window
 * comparison. Intended to be run from the IDE or command line and inspected
 * visually.
 * </p>
 */
public class TimeSeriesPredictionComparisonTest {

	private static final long DAY = 86_400_000L;
	private static final long YEAR = TimeSeriesTools.YEAR_IN_MILLISECONDS;

	public static void main(String[] args) {

		// --- Series 1: short high-frequency series (1 hour, 2-minute quantization) ---
		Date startShort = DateTools.createDate(2016, 4, 3, 2, 0, 0, 0);
		Date endShort = DateTools.createDate(2016, 4, 3, 3, 0, 0, 0);
		TimeDuration quantShort = new TimeDuration(TimeQuantization.MINUTES, 2);
		ITimeSeriesUnivariate tsShort = TimeSeriesGenerator.generateSyntheticTimeSeriesUnivariate(startShort.getTime(),
				endShort.getTime(), quantShort, true);

		runComparison(tsShort, "Short high-frequency (1h, 2min quantization)");

		// --- Series 2: medium daily series (2 years, daily quantization) ---
		Date startMedium = DateTools.createDate(2020, 1, 1, 0, 0, 0, 0);
		Date endMedium = DateTools.createDate(2022, 1, 1, 0, 0, 0, 0);
		TimeDuration quantDaily = new TimeDuration(TimeQuantization.DAYS, 1);
		ITimeSeriesUnivariate tsMedium = TimeSeriesGenerator
				.generateSyntheticTimeSeriesUnivariate(startMedium.getTime(), endMedium.getTime(), quantDaily, true);

		runComparison(tsMedium, "Medium daily (2 years, daily quantization)");

		// --- Series 3: long daily series (10 years) -- stresses O(n^2) original ---
		Date startLong = DateTools.createDate(2010, 1, 1, 0, 0, 0, 0);
		Date endLong = DateTools.createDate(2020, 1, 1, 0, 0, 0, 0);
		ITimeSeriesUnivariate tsLong = TimeSeriesGenerator.generateSyntheticTimeSeriesUnivariate(startLong.getTime(),
				endLong.getTime(), quantDaily, true);

		runComparison(tsLong, "Long daily (10 years, daily quantization)");

		// --- Performance comparison on long series ---
		runPerformanceComparison(tsLong, "Performance: long daily (10 years)");
	}

	// -----------------------------------------------------------------------
	// Private helpers
	// -----------------------------------------------------------------------

	/**
	 * Runs the full comparison suite for a single time series: single-horizon,
	 * standard-horizons table, and window comparison.
	 */
	private static void runComparison(ITimeSeriesUnivariate ts, String label) {
		TimeSeriesPredictionComparison comparison = new TimeSeriesPredictionComparison(ts, label);

		long last = ts.getLastTimestamp();

		// Single horizon: 30 days ahead
		comparison.compare(last + 30 * DAY);

		// Standard horizons table
		comparison.compareStandardHorizons();

		// Window comparison: full window vs. last 90 days
		long maxAge = last - 90 * DAY;
		if (maxAge > ts.getFirstTimestamp())
			comparison.compareWindows(last + YEAR, maxAge);
		else
			System.out.println("(Window comparison skipped -- series shorter than 90 days)");

		System.out.println();
	}

	private static void runPerformanceComparison(ITimeSeriesUnivariate ts, String label) {
		System.out.println(repeat('=', 70));
		System.out.println("Performance comparison: " + label);
		System.out.println("Series size: " + ts.size() + " points");
		System.out.println(repeat('-', 70));

		long target = ts.getLastTimestamp() + YEAR;

		// Warm up
		TimeSeriesTools.predict(ts, target);
		TimeSeriesPrediction.predict(ts, target);

		// Original
		long t0 = System.currentTimeMillis();
		for (int i = 0; i < 10; i++)
			TimeSeriesTools.predict(ts, target);
		long originalMs = System.currentTimeMillis() - t0;

		// Revised (full window)
		t0 = System.currentTimeMillis();
		for (int i = 0; i < 10; i++)
			TimeSeriesPrediction.predict(ts, target);
		long revisedMs = System.currentTimeMillis() - t0;

		// Revised (windowed: last 90 days)
		long maxAge = ts.getLastTimestamp() - 90 * DAY;
		t0 = System.currentTimeMillis();
		for (int i = 0; i < 10; i++)
			TimeSeriesPrediction.predict(ts, target, maxAge);
		long windowedMs = System.currentTimeMillis() - t0;

		System.out.printf("%-30s  %d ms (10 runs)%n", "Original (TimeSeriesTools):", originalMs);
		System.out.printf("%-30s  %d ms (10 runs)%n", "Revised full window:", revisedMs);
		System.out.printf("%-30s  %d ms (10 runs)%n", "Revised 90-day window:", windowedMs);

		if (originalMs > 0)
			System.out.printf("Speedup (full window):    %.1fx%n", (double) originalMs / Math.max(1, revisedMs));
		if (originalMs > 0)
			System.out.printf("Speedup (90-day window):  %.1fx%n", (double) originalMs / Math.max(1, windowedMs));

		System.out.println();
	}

	private static String repeat(char c, int n) {
		StringBuilder sb = new StringBuilder(n);
		for (int i = 0; i < n; i++)
			sb.append(c);
		return sb.toString();
	}
}