package com.github.TKnudsen.timeseries.operations.tools;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.DMandML.model.tools.density.GaussianKernelDensityEstimator;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

/**
 * Estimates the density of a time series' VALUES via {@link
 * GaussianKernelDensityEstimator}, weighting every observation by the time
 * duration it represents (half the gap to its predecessor plus half the gap
 * to its successor) rather than counting every observation equally. Without
 * this, a non-equidistantly-sampled time series would bias the estimate
 * toward whatever value range happens to be sampled most densely in time,
 * even if that range is not actually more common -- it would just have more
 * data points recorded during it.
 *
 * @since 2026
 */
public class TemporalKernelDensityEstimator {

	private final GaussianKernelDensityEstimator estimator;

	public TemporalKernelDensityEstimator(double variance) {
		this.estimator = new GaussianKernelDensityEstimator(variance);
	}

	/**
	 * @param evaluationPoints value-domain grid to evaluate the density at, e.g.
	 *                         from {@link
	 *                         GaussianKernelDensityEstimator#createEvaluationGrid}
	 */
	public List<Double> estimateValueDensity(ITimeSeriesUnivariate timeSeries, List<Double> evaluationPoints) {
		List<Double> values = timeSeries.getValues();
		List<Double> weights = calculateDurationWeights(timeSeries.getTimestamps());

		return estimator.estimateDensity(values, weights, evaluationPoints);
	}

	private List<Double> calculateDurationWeights(List<Long> timestamps) {
		int n = timestamps.size();
		if (n < 2)
			throw new IllegalArgumentException(
					"TemporalKernelDensityEstimator: time series must have at least 2 observations");

		List<Double> weights = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			long before = (i > 0) ? timestamps.get(i) - timestamps.get(i - 1) : timestamps.get(1) - timestamps.get(0);
			long after = (i < n - 1) ? timestamps.get(i + 1) - timestamps.get(i)
					: timestamps.get(n - 1) - timestamps.get(n - 2);

			weights.add((before + after) / 2.0);
		}

		return weights;
	}

}
