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

import flanagan.complex.Complex;
import flanagan.math.FourierTransform;

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
 * Michael Thomas Flanagan's Java Scientific Library is used to apply the FFT.
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

	// model
	FourierTransform fft = new FourierTransform();

	@SuppressWarnings("unused")
	private FourierTransformDescriptor() {
		this.coefficientCount = 3;
	}

	public FourierTransformDescriptor(int coefficientCount) {
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
		fft.setData(array);
		fft.transform();

		double[] transformedDataAsAlternate = fft.getTransformedDataAsAlternate();

		List<NumericalFeature> features = new ArrayList<>();
		for (int i = 0; i < coefficientCount; i++) {
			features.add(new NumericalFeature("FT coefficient " + (i + 1) + " (real)", transformedDataAsAlternate[i * 2]));
			features.add(new NumericalFeature("FT coefficient " + (i + 1) + " (imaginary)", transformedDataAsAlternate[i * 2 + 1]));
		}

		NumericalFeatureVector fv = new NumericalFeatureVector(features);

		return Arrays.asList(fv);
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
	 * Inverts the descriptor operation. synthesizes a TimeSeries from the FT
	 * coefficients.
	 * 
	 * @param quantization
	 * @param size
	 * @return
	 */
	public ITimeSeriesUnivariate invertFT(long quantization, int size) {
		Complex[] complex = fft.getTransformedDataAsComplex();

		double[] synthesizedSeries = new double[size];
		for (int i = 0; i < size; i++) {
			for (int f = 0; f < coefficientCount; f++) {
				double exponent = 2 * Math.PI * f * i / (size - 1.0);
				synthesizedSeries[i] += complex[f].getImag() * Math.cos(exponent);
				synthesizedSeries[i] -= complex[f].getReal() * Math.sin(exponent);
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
		return "Flanagan's Fourier Transform applied on Time Series Data";
	}

	public int getCoefficientCount() {
		return coefficientCount;
	}

	public void setCoefficientCount(int coefficientCount) {
		this.coefficientCount = coefficientCount;
	}
}
