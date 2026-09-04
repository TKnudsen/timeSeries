package com.github.TKnudsen.timeseries.operations.tools;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.DMandML.model.tools.density.GaussianKernelSmoother;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;

/**
 * Smooths a time series via Gaussian kernel regression ({@link
 * GaussianKernelSmoother}): every output value is a Gaussian-weighted
 * average of the input series' values, weighted by time-distance to that
 * output's timestamp. The bandwidth is in the series' own timestamp units
 * (typically milliseconds).
 *
 * @since 2026
 */
public class TimeSeriesGaussianSmoother {

	private final GaussianKernelSmoother smoother;

	public TimeSeriesGaussianSmoother(double bandwidth) {
		this.smoother = new GaussianKernelSmoother(bandwidth);
	}

	/** Smooths at the same timestamps as the input series. */
	public ITimeSeriesUnivariate smooth(ITimeSeriesUnivariate timeSeries) {
		return smooth(timeSeries, timeSeries.getTimestamps());
	}

	public ITimeSeriesUnivariate smooth(ITimeSeriesUnivariate timeSeries, List<Long> evaluationTimestamps) {
		List<Double> x = new ArrayList<>();
		for (Long timestamp : timeSeries.getTimestamps())
			x.add(timestamp.doubleValue());

		List<Double> evaluationPoints = new ArrayList<>();
		for (Long timestamp : evaluationTimestamps)
			evaluationPoints.add(timestamp.doubleValue());

		List<Double> smoothedValues = smoother.smooth(x, timeSeries.getValues(), evaluationPoints);

		return new TimeSeriesUnivariate(evaluationTimestamps, smoothedValues);
	}

	public double getBandwidth() {
		return smoother.getBandwidth();
	}

	public void setBandwidth(double bandwidth) {
		smoother.setBandwidth(bandwidth);
	}

}
