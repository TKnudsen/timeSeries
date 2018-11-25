package com.github.TKnudsen.timeseries.test.operations.transformations.descriptors.univariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeatureVector;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.transformations.descriptors.univariate.FourierTransformDescriptor;

public class FourierTransformDescriptorTest {

	public static void main(String[] args) {

		List<Long> timeStamps = new ArrayList<>();
		List<Double> values = new ArrayList<>();

		int size = 32;
		for (int i = 0; i < size; i++) {
			timeStamps.add(i * 1000L);
			values.add(Math.sin(i * 2 * Math.PI / size));
		}

		ITimeSeriesUnivariate timeSeries = new TimeSeriesUnivariate(timeStamps, values);

		System.out.println(timeSeries);

		FourierTransformDescriptor fft = new FourierTransformDescriptor(10);
		List<NumericalFeatureVector> fv = fft.transform(timeSeries);

		ITimeSeriesUnivariate invertFT = fft.invertFT(1000, size);
		System.out.println(invertFT);
	}

}
