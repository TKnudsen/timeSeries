package com.github.TKnudsen.timeseries.operations.workflow.univariate;

import com.github.TKnudsen.ComplexDataObject.data.enums.NormalizationType;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.ITimeSeriesUnivariatePreprocessor;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.normalization.MinMaxNormalization;

public class NormalizationRoutineFactory {
	public static ITimeSeriesUnivariatePreprocessor createNormalizationRoutine(NormalizationType normalizationType) {
		ITimeSeriesUnivariatePreprocessor normalizer = null;

		if (normalizationType.equals(NormalizationType.normalizeMinMax))
			normalizer = new MinMaxNormalization();
		else if (normalizationType.equals(NormalizationType.normalizeMinMaxGlobal))
			normalizer = new MinMaxNormalization(true);
		else if (normalizationType.equals(NormalizationType.normalizeMinMaxBinWise))
			normalizer = new MinMaxNormalization();
		else if (normalizationType.equals(NormalizationType.offsetTranslation))
			throw new IllegalArgumentException("Offset Translation not yet supported in time series lib");
		else if (normalizationType.equals(NormalizationType.amplitudeScaling))
			throw new IllegalArgumentException("Amplitude Scaling not yet supported in time series lib");

		return normalizer;
	}
}
