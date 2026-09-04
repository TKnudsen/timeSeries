package com.github.TKnudsen.timeseries.operations.tools;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.DMandML.model.tools.density.GaussianKernelDensityEstimator;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

/**
 * Estimates the density of a time series' TIMESTAMPS (not its values) via
 * {@link GaussianKernelDensityEstimator} -- i.e. how bursty or sparse the
 * sampling itself is over time, independent of what values were recorded.
 * Every observation counts equally here (unlike {@link
 * TemporalKernelDensityEstimator}'s value distribution): counting how often
 * the series was sampled at a given time is exactly the point, not a bias to
 * correct for.
 *
 * @since 2026
 */
public class TimestampDensityCalculator {

	private final GaussianKernelDensityEstimator estimator;

	public TimestampDensityCalculator(double variance) {
		this.estimator = new GaussianKernelDensityEstimator(variance);
	}

	/**
	 * @param evaluationTimestamps time-domain grid to evaluate the density at
	 */
	public List<Double> estimateTimestampDensity(ITimeSeriesUnivariate timeSeries,
			List<Long> evaluationTimestamps) {
		List<Double> timestampsAsSamples = new ArrayList<>();
		for (Long timestamp : timeSeries.getTimestamps())
			timestampsAsSamples.add(timestamp.doubleValue());

		List<Double> evaluationPoints = new ArrayList<>();
		for (Long evaluationTimestamp : evaluationTimestamps)
			evaluationPoints.add(evaluationTimestamp.doubleValue());

		return estimator.estimateDensity(timestampsAsSamples, evaluationPoints);
	}

	/** Evenly-spaced timestamp grid across [minTime, maxTime]. */
	public static List<Long> createEvaluationGrid(long minTime, long maxTime, int numSamples) {
		List<Long> grid = new ArrayList<>();

		double quantization = (maxTime - minTime) / (double) numSamples;
		for (double t = minTime; t <= maxTime; t += quantization)
			grid.add((long) t);

		return grid;
	}

}
