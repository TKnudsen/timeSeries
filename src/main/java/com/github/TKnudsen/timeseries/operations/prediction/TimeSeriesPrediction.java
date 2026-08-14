package com.github.TKnudsen.timeseries.operations.prediction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * <p>
 * Predicts future values of a uni-variate time series using a weighted
 * aggregation of pairwise segment trends observed within a recent time window.
 * </p>
 *
 * <p>
 * Algorithm:
 * </p>
 * <ol>
 * <li>Filter time stamps to [{@code maxAgeTimeStamp}, lastTimestamp]</li>
 * <li>For each ordered pair (k, l), compute local trend and extrapolate to the
 * target time stamp</li>
 * <li>Weight each pair by recency:
 * {@code w = 1 - ((target-k) + (target-l)) / maxAgeDuration}</li>
 * <li>Return weighted average prediction with combined uncertainty</li>
 * </ol>
 *
 * <p>
 * Uncertainty is the maximum of:
 * </p>
 * <ul>
 * <li><b>Value uncertainty</b>: weighted std dev normalized by
 * {@code abs(weightedMean)} -- avoids sign and near-zero issues</li>
 * <li><b>Temporal uncertainty</b>: {@code 1 - decay^years}, reaching ~0.67 at 1
 * year (decay=0.33)</li>
 * </ul>
 *
 * <p>
 * Temporal uncertainty profiles:
 * </p>
 *
 * <pre>
 * decay=0.33 (multi-point): 1mo~=0.13, 3mo~=0.34, 6mo~=0.52, 1yr~=0.67, 2yr~=0.89
 * decay=0.50 (single-point): 1mo~=0.06, 3mo~=0.16, 6mo~=0.29, 1yr~=0.50, 2yr~=0.75
 * </pre>
 *
 * @version 2.0
 * @since 2015
 */
public final class TimeSeriesPrediction {

	/**
	 * Temporal decay base for multi-point prediction uncertainty. At 1 year ahead:
	 * uncertainty = 1 - 0.33^1 ~= 0.67.
	 */
	private static final double TEMPORAL_DECAY_MULTI = 0.33;

	/**
	 * Temporal decay base for single-point fallback uncertainty. More conservative
	 * since no trend information is available. At 1 year ahead: uncertainty = 1 -
	 * 0.5^1 = 0.50.
	 */
	private static final double TEMPORAL_DECAY_SINGLE = 0.5;

	private TimeSeriesPrediction() {
	}

	/**
	 * Predicts the value at {@code target} using all available time stamps as the
	 * look-back window.
	 *
	 * @param timeSeries the source time series; must not be null or empty
	 * @param target     target time stamp; must be &ge; firstTimestamp
	 * @return {@code double[2]}: [0] predicted value, [1] uncertainty in [0, 1]
	 * @throws NullPointerException      if timeSeries is null
	 * @throws IndexOutOfBoundsException if series is empty or target &lt;
	 *                                   firstTimestamp
	 */
	public static double[] predict(ITimeSeriesUnivariate timeSeries, long target) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");
		if (timeSeries.isEmpty())
			throw new IndexOutOfBoundsException("predict: cannot predict for an empty time series");
		return predict(timeSeries, target, timeSeries.getFirstTimestamp());
	}

	/**
	 * Predicts the value at {@code target} using only time stamps on or after
	 * {@code maxAgeTimeStamp} as the look-back window.
	 *
	 * <p>
	 * Special cases:
	 * </p>
	 * <ul>
	 * <li>Target within series to interpolated value, uncertainty = 0.0</li>
	 * <li>Series has fewer than 2 points to last value with temporal uncertainty
	 * only</li>
	 * <li>All pairs have zero or negative weight to single-point fallback</li>
	 * </ul>
	 *
	 * @param timeSeries      the source time series; must not be null or empty
	 * @param target          target time stamp; must be &ge; firstTimestamp
	 * @param maxAgeTimeStamp look-back window start; must be &le; target
	 * @return {@code double[2]}: [0] predicted value, [1] uncertainty in [0, 1]
	 * @throws NullPointerException      if timeSeries is null
	 * @throws IndexOutOfBoundsException if series is empty, target &lt;
	 *                                   firstTimestamp, or maxAgeTimeStamp &gt;
	 *                                   target
	 */
	public static double[] predict(ITimeSeriesUnivariate timeSeries, long target, long maxAgeTimeStamp) {
		Objects.requireNonNull(timeSeries, "timeSeries must not be null");

		if (timeSeries.isEmpty())
			throw new IndexOutOfBoundsException("predict: cannot predict for an empty time series");

		if (target < timeSeries.getFirstTimestamp())
			throw new IndexOutOfBoundsException(
					"predict: target=" + target + " is before firstTimestamp=" + timeSeries.getFirstTimestamp());

		if (maxAgeTimeStamp > target)
			throw new IndexOutOfBoundsException("predict: maxAgeTimeStamp must not be greater than target");

		if (target <= timeSeries.getLastTimestamp())
			return new double[] { TimeSeriesTools.getInterpolatedValue(timeSeries, target), 0.0 };

		if (timeSeries.size() < 2)
			return singlePointFallback(timeSeries, target);

		return predictWeighted(timeSeries, target, maxAgeTimeStamp);
	}

	// -----------------------------------------------------------------------
	// Private implementation
	// -----------------------------------------------------------------------

	private static double[] predictWeighted(ITimeSeriesUnivariate timeSeries, long target, long maxAgeTimeStamp) {

		// factor of 2: each pair contributes two time stamps' ages to weight
		// denominator
		long maxAgeDuration = (target - maxAgeTimeStamp) * 2L;

		// filter to window -- key optimization vs. original O(n^2) over full series
		List<Long> window = new ArrayList<>();
		for (long t : timeSeries.getTimestamps())
			if (t >= maxAgeTimeStamp)
				window.add(t);

		long lastTimestamp = timeSeries.getLastTimestamp();
		double lastValue = timeSeries.getValue(lastTimestamp, false);
		long deltaTimeUnknown = target - lastTimestamp;

		List<Double> predictions = new ArrayList<>();
		List<Double> weights = new ArrayList<>();

		for (int ki = 0; ki < window.size(); ki++) {
			long k = window.get(ki);
			for (int li = ki + 1; li < window.size(); li++) {
				long l = window.get(li);

				double w = 1.0 - ((target - k) + (target - l)) / (double) maxAgeDuration;
				if (w <= 0)
					continue;

				double vk = timeSeries.getValue(k, false);
				double vl = timeSeries.getValue(l, false);
				if (Double.isNaN(vk) || Double.isNaN(vl))
					continue;

				double trend = (vl - vk) / (double) (l - k);
				predictions.add(lastValue + trend * deltaTimeUnknown);
				weights.add(w);
			}
		}

		if (predictions.isEmpty())
			return singlePointFallback(timeSeries, target);

		double weightedAvg = weightedMean(predictions, weights);
		double weightedStd = weightedStdDev(predictions, weights, weightedAvg);
		double valueUncertainty = computeValueUncertainty(weightedStd, weightedAvg);
		double temporalUncertainty = computeTemporalUncertainty(deltaTimeUnknown, TEMPORAL_DECAY_MULTI);

		return new double[] { weightedAvg,
				Math.max(0.0, Math.min(1.0, Math.max(valueUncertainty, temporalUncertainty))) };
	}

	private static double[] singlePointFallback(ITimeSeriesUnivariate timeSeries, long target) {
		long delta = target - timeSeries.getLastTimestamp();
		double value = timeSeries.getValue(timeSeries.getLastTimestamp(), false);
		double uncertainty = computeTemporalUncertainty(delta, TEMPORAL_DECAY_SINGLE);
		return new double[] { value, Math.max(0.0, Math.min(1.0, uncertainty)) };
	}

	// -----------------------------------------------------------------------
	// Statistics helpers -- package-visible for testing
	// -----------------------------------------------------------------------

	static double weightedMean(List<Double> values, List<Double> weights) {
		double sum = 0.0;
		double weightSum = 0.0;
		for (int i = 0; i < values.size(); i++) {
			sum += values.get(i) * weights.get(i);
			weightSum += weights.get(i);
		}
		return weightSum == 0 ? Double.NaN : sum / weightSum;
	}

	/**
	 * Weighted standard deviation using reliability weights:
	 * {@code sqrt( sum(w_i * (v_i - mean)^2) / sum(w_i) )}.
	 */
	static double weightedStdDev(List<Double> values, List<Double> weights, double mean) {
		double varianceSum = 0.0;
		double weightSum = 0.0;
		for (int i = 0; i < values.size(); i++) {
			double dev = values.get(i) - mean;
			varianceSum += weights.get(i) * dev * dev;
			weightSum += weights.get(i);
		}
		return weightSum == 0 ? Double.NaN : Math.sqrt(varianceSum / weightSum);
	}

	/**
	 * Normalizes std by {@code abs(mean)}. Returns 1.0 when mean is near zero
	 * (uncertainty undefined). Capped at 1.0.
	 */
	static double computeValueUncertainty(double std, double mean) {
		double absMean = Math.abs(mean);
		if (absMean < 1e-10)
			return 1.0;
		return Math.min(1.0, std / absMean);
	}

	/**
	 * Temporal uncertainty: {@code 1 - decay^years}, bounded to [0, 1].
	 *
	 * @param deltaMs prediction horizon in milliseconds
	 * @param decay   exponential decay base; must be in (0, 1)
	 */
	static double computeTemporalUncertainty(long deltaMs, double decay) {
		double years = deltaMs / (double) TimeSeriesTools.YEAR_IN_MILLISECONDS;
		return Math.min(1.0, Math.max(0.0, 1.0 - Math.pow(decay, years)));
	}
}