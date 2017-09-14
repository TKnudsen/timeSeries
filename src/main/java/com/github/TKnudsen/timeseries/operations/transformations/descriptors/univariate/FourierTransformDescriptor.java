package com.github.TKnudsen.timeseries.operations.transformations.descriptors.univariate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeature;
import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeatureVector;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataTransformationCategory;
import com.github.TKnudsen.ComplexDataObject.model.tools.DataConversion;
import com.github.TKnudsen.ComplexDataObject.model.transformations.descriptors.IDescriptor;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * <p>
 * Title: FourierTransformDescriptor
 * </p>
 * 
 * <p>
 * Description: Time Series Descriptor based on the Fast Fourier Transform
 * (FFT). As far as I know, the FFT was first applied on time series in the
 * following publication:
 * 
 * Authors: Rakesh Agrawal, Christos Faloutsos, Arun N. Swami
 * 
 * Published in: Proceedings of the 4th International Conference on Foundations
 * of Data Organization and Algorithms Pages 69-84 * October 13 - 15, 1993
 * 
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public class FourierTransformDescriptor implements ITimeSeriesUnivariateDescriptor {

	// parameters
	private int coefficientCount;

	private boolean useRealFeatures = false;

	private boolean useImaginaryFeatures = true;

	// model
	/**
	 * the array of FFT coefficients. can be used to calculate the inverted
	 * function.
	 */
	Double[][] complex;

	@SuppressWarnings("unused")
	private FourierTransformDescriptor() {
		this(3);
	}

	public FourierTransformDescriptor(int coefficientCount) {
		if (coefficientCount < 2)
			throw new IllegalArgumentException("FourierTransformDescriptor: coefficient needs to be at least 2");

		this.coefficientCount = coefficientCount;
	}

	@Override
	public List<IDescriptor<ITimeSeriesUnivariate, Double, NumericalFeatureVector>> getAlternativeParameterizations(int count) {
		List<Integer> integers = ParameterSupportTools.getAlternativeIntegers(coefficientCount, count);

		List<IDescriptor<ITimeSeriesUnivariate, Double, NumericalFeatureVector>> descriptors = new ArrayList<>();
		for (Integer i : integers)
			descriptors.add(new FourierTransformDescriptor(i));

		return descriptors;
	}

	@Override
	public List<NumericalFeatureVector> transform(ITimeSeriesUnivariate timeSeries) {
		if (timeSeries == null)
			return null;

		// check equidistancy
		if (!TimeSeriesTools.isEquidistant(timeSeries))
			throw new IllegalArgumentException(getName() + " requires an equidistant time series.");

		// convert value domain
		List<Double> values = timeSeries.getValues();
		double[] array = DataConversion.toPrimitives(values);

		// apply Fourier Transform
		complex = calculateFFT(array);

		List<NumericalFeature> features = new ArrayList<>();
		for (int i = 0; i < Math.min(coefficientCount, complex.length); i++) {
			if (useRealFeatures)
				features.add(new NumericalFeature("FT coefficient " + (i + 1) + " (real)", complex[i][0]));
			if (useImaginaryFeatures)
				features.add(new NumericalFeature("FT coefficient " + (i + 1) + " (imaginary)", complex[i][1]));
		}

		NumericalFeatureVector fv = new NumericalFeatureVector(features);

		return Arrays.asList(fv);
	}

	/**
	 * calculates the Fourier Coefficients from a given signal. Per design, it
	 * is assumed that the signal is equidistant.
	 * 
	 * @param signal
	 * @return
	 */
	private Double[][] calculateFFT(double[] signal) {
		if (signal == null)
			return null;

		// TODO needs validation
		complex = new Double[coefficientCount][2];
		for (int s = 0; s < Math.min(coefficientCount, signal.length); s++) {
			double real = 0;
			double imaginary = 0;
			for (int i = 0; i < signal.length; i++) {
				double e = -2 * Math.PI * s * i / (signal.length - 1);
				real += signal[i] * Math.cos(e);
				imaginary += signal[i] * Math.sin(e);
			}

			complex[s] = new Double[] { real / Math.sqrt(signal.length - 1), imaginary / Math.sqrt(signal.length - 1) };
		}

		return complex;
	}

	@Override
	public List<NumericalFeatureVector> transform(List<ITimeSeriesUnivariate> timeSeriesList) {
		if (timeSeriesList == null)
			return null;

		List<NumericalFeatureVector> fvs = new ArrayList<>();

		for (ITimeSeriesUnivariate timeSeries : timeSeriesList)
			fvs.addAll(transform(timeSeries));

		return fvs;
	}

	/**
	 * Inverts the descriptor operation. synthesizes a TimeSeries from the FFT
	 * coefficients. Uses only the imaginary parts.
	 * 
	 * @param quantization
	 * @param size
	 * @return
	 */
	public ITimeSeriesUnivariate invertFT(long quantization, int size) {
		if (complex == null)
			return null;

		double[] synthesizedSeries = new double[size];
		for (int i = 0; i < size; i++) {
			for (int f = 0; f < Math.min(coefficientCount, complex.length); f++) {
				double exponent = 2 * Math.PI * f * i / (size - 1.0);
				synthesizedSeries[i] += complex[f][0] * Math.cos(exponent);
				synthesizedSeries[i] -= complex[f][1] * Math.sin(exponent);
			}
			synthesizedSeries[i] = synthesizedSeries[i] / Math.sqrt((double) (size - 1.0));
		}

		List<Long> timeStamps = new ArrayList<>();
		List<Double> invertedValues = new ArrayList<>();
		for (int i = 0; i < synthesizedSeries.length; i++) {
			timeStamps.add(i * quantization);
			invertedValues.add(synthesizedSeries[i]);
		}

		ITimeSeriesUnivariate invertedFFT = new TimeSeriesUnivariate(timeStamps, invertedValues);

		return invertedFFT;
	}

	@Override
	public DataTransformationCategory getDataTransformationCategory() {
		return DataTransformationCategory.DESCRIPTOR;
	}

	@Override
	public String getName() {
		return "Fourier Transform Descriptor";
	}

	@Override
	public String getDescription() {
		return "Flanagan's Fast Fourier Transform applied on Time Series Data";
	}

	public int getCoefficientCount() {
		return coefficientCount;
	}

	public void setCoefficientCount(int coefficientCount) {
		this.coefficientCount = coefficientCount;
	}

	public boolean isUseRealFeatures() {
		return useRealFeatures;
	}

	public void setUseRealFeatures(boolean useRealFeatures) {
		this.useRealFeatures = useRealFeatures;
	}

	public boolean isUseImaginaryFeatures() {
		return useImaginaryFeatures;
	}

	public void setUseImaginaryFeatures(boolean useImaginaryFeatures) {
		this.useImaginaryFeatures = useImaginaryFeatures;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof FourierTransformDescriptor))
			return false;

		FourierTransformDescriptor other = (FourierTransformDescriptor) o;

		if (coefficientCount != other.coefficientCount)
			return false;
		if (useRealFeatures != other.useRealFeatures)
			return false;
		if (useImaginaryFeatures != other.useImaginaryFeatures)
			return false;

		return true;
	}
}
