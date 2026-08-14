package com.github.TKnudsen.timeseries.operations.prediction;

import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * Compares {@link TimeSeriesTools#predict} (original) against
 * {@link TimeSeriesPrediction#predict} (revised) for externally provided time
 * series.
 *
 * <p>
 * Instantiate with a time series and call {@link #compare(long)} or
 * {@link #compareMultipleHorizons(long[])} to produce a readable report. This
 * class does not use any test framework -- it is intended to be run manually
 * with real data and inspected visually.
 * </p>
 */
public class TimeSeriesPredictionComparison {

	private static final long YEAR = TimeSeriesTools.YEAR_IN_MILLISECONDS;
	private static final long DAY = 86_400_000L;

	private static final String SEPARATOR_HEAVY = repeat('=', 70);
	private static final String SEPARATOR_LIGHT = repeat('-', 70);

	private final ITimeSeriesUnivariate timeSeries;
	private final String seriesLabel;

	/**
	 * @param timeSeries  the time series to predict from; must not be null
	 * @param seriesLabel a human-readable label for report output
	 */
	public TimeSeriesPredictionComparison(ITimeSeriesUnivariate timeSeries, String seriesLabel) {
		if (timeSeries == null)
			throw new NullPointerException("timeSeries must not be null");
		this.timeSeries = timeSeries;
		this.seriesLabel = seriesLabel;
	}

	/**
	 * Compares original and revised predictions at a single target timestamp and
	 * prints a report to stdout.
	 *
	 * @param target the target timestamp to predict
	 */
	public void compare(long target) {
		System.out.println(SEPARATOR_HEAVY);
		printSeriesHeader();
		System.out.println("Target : " + target);
		long horizonDays = (target - timeSeries.getLastTimestamp()) / DAY;
		System.out.println("Horizon: " + horizonDays + " days beyond last timestamp");
		System.out.println(SEPARATOR_LIGHT);

		double[] original = TimeSeriesTools.predict(timeSeries, target);
		double[] revised = TimeSeriesPrediction.predict(timeSeries, target);

		System.out.printf("%-20s  value = %12.4f   uncertainty = %.4f%n", "Original:", original[0], original[1]);
		System.out.printf("%-20s  value = %12.4f   uncertainty = %.4f%n", "Revised:", revised[0], revised[1]);

		double valueDelta = Math.abs(original[0] - revised[0]);
		double uncDelta = Math.abs(original[1] - revised[1]);
		System.out.printf("%-20s  |dValue| = %.4f   |dUncertainty| = %.4f%n", "Delta:", valueDelta, uncDelta);

		if (valueDelta > Math.abs(original[0]) * 0.1)
			System.out.println("  WARNING: Value diverges by more than 10% of original");
		if (uncDelta > 0.2)
			System.out.println("  WARNING: Uncertainty diverges by more than 0.2");

		System.out.println();
	}

	/**
	 * Compares original and revised predictions at multiple target timestamps and
	 * prints a combined table to stdout.
	 *
	 * @param targets array of target timestamps to predict
	 */
	public void compareMultipleHorizons(long[] targets) {
		System.out.println(SEPARATOR_HEAVY);
		printSeriesHeader();
		System.out.println();
		System.out.printf("%-12s  %-14s  %-14s  %-10s  %-10s  %-8s  %-8s%n", "Horizon", "Orig.Value", "Rev.Value",
				"Orig.Unc", "Rev.Unc", "|dVal|", "|dUnc|");
		System.out.println(SEPARATOR_LIGHT);

		for (long target : targets) {
			long horizonDays = (target - timeSeries.getLastTimestamp()) / DAY;

			double[] original;
			double[] revised;
			try {
				original = TimeSeriesTools.predict(timeSeries, target);
				revised = TimeSeriesPrediction.predict(timeSeries, target);
			} catch (Exception e) {
				System.out.printf("%-12s  ERROR: %s%n", horizonDays + "d", e.getMessage());
				continue;
			}

			double valueDelta = Math.abs(original[0] - revised[0]);
			double uncDelta = Math.abs(original[1] - revised[1]);
			String flags = buildFlags(original, revised, valueDelta, uncDelta);

			System.out.printf("%-12s  %-14.4f  %-14.4f  %-10.4f  %-10.4f  %-8.4f  %-8.4f  %s%n", horizonDays + "d",
					original[0], revised[0], original[1], revised[1], valueDelta, uncDelta, flags);
		}
		System.out.println();
	}

	/**
	 * Convenience method: compares at standard horizons relative to the last
	 * timestamp: 1 week, 1 month, 3 months, 6 months, 1 year, 2 years, 5 years.
	 */
	public void compareStandardHorizons() {
		long last = timeSeries.getLastTimestamp();
		long[] horizons = { last + 7 * DAY, last + 30 * DAY, last + 90 * DAY, last + 180 * DAY, last + YEAR,
				last + 2 * YEAR, last + 5 * YEAR };
		compareMultipleHorizons(horizons);
	}

	/**
	 * Compares full-window vs. restricted-window predictions using the revised
	 * implementation, to quantify the effect of the window optimization.
	 *
	 * @param target      the target timestamp
	 * @param maxAgeStamp the restricted window start timestamp
	 */
	public void compareWindows(long target, long maxAgeStamp) {
		System.out.println(SEPARATOR_HEAVY);
		System.out.println("Window comparison for: " + seriesLabel);
		System.out.println("Target   : " + target);
		System.out.println("MaxAge   : " + maxAgeStamp);
		long windowDays = (timeSeries.getLastTimestamp() - maxAgeStamp) / DAY;
		System.out.println("Window   : " + windowDays + " days");
		System.out.println(SEPARATOR_LIGHT);

		double[] fullWindow = TimeSeriesPrediction.predict(timeSeries, target);
		double[] smallWindow = TimeSeriesPrediction.predict(timeSeries, target, maxAgeStamp);

		System.out.printf("%-20s  value = %12.4f   uncertainty = %.4f%n", "Full window:", fullWindow[0], fullWindow[1]);
		System.out.printf("%-20s  value = %12.4f   uncertainty = %.4f%n", "Restricted window:", smallWindow[0],
				smallWindow[1]);
		System.out.printf("%-20s  |dValue| = %.4f   |dUncertainty| = %.4f%n", "Delta:",
				Math.abs(fullWindow[0] - smallWindow[0]), Math.abs(fullWindow[1] - smallWindow[1]));
		System.out.println();
	}

	// -----------------------------------------------------------------------
	// Private helpers
	// -----------------------------------------------------------------------

	private void printSeriesHeader() {
		System.out.println("Series : " + seriesLabel);
		System.out.println("Size   : " + timeSeries.size() + " points");
		System.out
				.println("Range  : [" + timeSeries.getFirstTimestamp() + " .. " + timeSeries.getLastTimestamp() + "]");
	}

	private String buildFlags(double[] original, double[] revised, double valueDelta, double uncDelta) {
		StringBuilder sb = new StringBuilder();
		double relDelta = original[0] != 0 ? valueDelta / Math.abs(original[0]) : 0;
		if (relDelta > 0.1)
			sb.append("WARN:VAL ");
		if (uncDelta > 0.2)
			sb.append("WARN:UNC ");
		if (revised[1] < original[1])
			sb.append("unc-down ");
		if (revised[1] > original[1])
			sb.append("unc-up ");
		return sb.toString().trim();
	}

	private static String repeat(char c, int n) {
		StringBuilder sb = new StringBuilder(n);
		for (int i = 0; i < n; i++)
			sb.append(c);
		return sb.toString();
	}
}