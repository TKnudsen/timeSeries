package com.github.TKnudsen.timeseries.operations.descriptors.univariate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeature;
import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeatureVector;
import com.github.TKnudsen.ComplexDataObject.model.descriptors.IDescriptor;
import com.github.TKnudsen.ComplexDataObject.model.preprocessing.ParameterSupportTools;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.TemporalQuantization;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * <p>
 * Title: PiecewiseAggregateApproximationDescriptor
 * </p>
 * 
 * <p>
 * Description: Piecewise Aggregate Approximation (PAA) was proposed by Keogh et
 * al. in the following publication:
 * 
 * Keogh, E,. Chakrabarti, K,. Pazzani, M. & Mehrotra.: Dimensionality Reduction
 * for Fast Similarity Search in Large Time Series Databases. Journal of
 * Knowledge and Information Systems, 2000.
 * 
 * The algorithm aggregates the values of a time series according to a constant
 * time interval (here: quantization). Thus, the algorithm is sensitive to the
 * value domain, but not to the temporal domain.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public class PiecewiseAggregateApproximationDescriptor implements ITimeSeriesUnivariateDescriptor {

	private TimeDuration quantization;

	public PiecewiseAggregateApproximationDescriptor(TimeDuration quantization) {
		this.quantization = quantization;
	}

	@Override
	public List<NumericalFeatureVector> transform(ITimeSeriesUnivariate input) {
		List<NumericalFeatureVector> featureVectors = new ArrayList<>();

		// create a time series with targeted time stamps
		// according to the quantization
		ITimeSeriesUnivariate clone = TimeSeriesTools.cloneTimeSeries(input);
		TemporalQuantization temporalQuantization = new TemporalQuantization(quantization);
		temporalQuantization.process(Arrays.asList(clone));

		// feed the additional information into the original time series
		ITimeSeriesUnivariate workingCopy = TimeSeriesTools.cloneTimeSeries(input);
		for (int i = 0; i < clone.size(); i++)
			workingCopy.insert(clone.getTimestamp(i), clone.getValue(i));

		List<NumericalFeature> features = new ArrayList<>();

		// create aggregate of the value domain for every quantization interval
		Long quantizationStart = workingCopy.getFirstTimestamp();
		Long quantizationEnd = quantizationStart + quantization.getDuration();
		int currentIndex = 0;
		int index = 1;
		while (quantizationEnd <= workingCopy.getLastTimestamp()) {
			Long currentTimeStamp = workingCopy.getTimestamp(currentIndex);
			double v = 0;
			double w = 0;
			while (currentTimeStamp <= quantizationEnd) {
				// create aggregate.
				// The weight of a value depends on the distance
				// to neighbors in the temporal domain
				double vCurrent = workingCopy.getValue(currentIndex);
				Long duration = getTemporalValidity(workingCopy, currentIndex, quantizationStart, quantizationEnd);
				v += vCurrent * duration;
				w += duration;

				currentIndex++;
				currentTimeStamp = workingCopy.getTimestamp(currentIndex);
			}

			features.add(new NumericalFeature("Aggregate " + index++, v / w));
			quantizationStart = quantizationEnd;
			quantizationEnd = quantizationStart + quantization.getDuration();
		}

		NumericalFeatureVector featureVector = new NumericalFeatureVector(features);
		featureVector.setMaster(input);
		featureVector.add("Descriptor", getName());
		if (input.getName() != null)
			featureVector.setName(input.getName());

		featureVectors.add(featureVector);
		return featureVectors;
	}

	private Long getTemporalValidity(ITimeSeriesUnivariate timeSeries, int currentIndex, Long quantizationStart, Long quantizationEnd) {
		Long duration = 0L;
		Long t = timeSeries.getTimestamp(currentIndex);

		if (currentIndex > 0)
			duration += (long) ((t - Math.max(timeSeries.getTimestamp(currentIndex - 1), quantizationStart)) * 0.5);

		if (currentIndex < timeSeries.size() - 1)
			duration += (long) ((Math.min(timeSeries.getTimestamp(currentIndex + 1), quantizationEnd) - t) * 0.5);

		return duration;
	}

	@Override
	public List<NumericalFeatureVector> transform(List<ITimeSeriesUnivariate> inputObjects) {
		List<NumericalFeatureVector> featureVectors = new ArrayList<>();

		for (ITimeSeriesUnivariate timeSeries : inputObjects)
			featureVectors.addAll(transform(timeSeries));

		return featureVectors;
	}

	@Override
	public String getName() {
		return "Piecewise Aggregate Approximation Descriptor, quantization = " + quantization;
	}

	@Override
	public String getDescription() {
		return getName() + ", calculates piecewise aggregates of a given time series and transforms these values to a NumericalFeatureVector.";
	}

	public TimeDuration getQuantization() {
		return quantization;
	}

	public void setQuantization(TimeDuration quantization) {
		this.quantization = quantization;
	}

	@Override
	public List<IDescriptor<ITimeSeriesUnivariate, Double, NumericalFeatureVector>> getAlternativeParameterizations(int count) {
		List<Long> longs = ParameterSupportTools.getAlternativeLongs(quantization.getDuration(), count);

		List<IDescriptor<ITimeSeriesUnivariate, Double, NumericalFeatureVector>> processors = new ArrayList<>();
		for (Long l : longs)
			processors.add(new PiecewiseAggregateApproximationDescriptor(new TimeDuration(quantization.getType(), l)));

		return processors;
	}

}
