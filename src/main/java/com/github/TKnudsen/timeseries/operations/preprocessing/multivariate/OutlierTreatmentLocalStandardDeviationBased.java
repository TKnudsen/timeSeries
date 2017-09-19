package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;

/**
 * <p>
 * Title: OutlierTreatmentLocalStandardDeviationBased
 * </p>
 * 
 * <p>
 * Description: Replaces the value domain for values higher/lower than a given multiple of the local standard deviation. The value domains of every individual IUnivariateTimeSeries are used to calculate the std. Replaces with a given value (standard
 * is NaN). The temporal domain is untouched.
 * </p>
 * 
 * @author Christian Ritter
 * @version 1.01
 */
public class OutlierTreatmentLocalStandardDeviationBased extends DimensionBasedTimeSeriesMultivariateProcessor {

	// standard deviation ratio
	double stdDeviationRatio;

	// the value that is assigned to an outlier
	double outlierValue;

	// moving average options
	private int elements;
	private boolean considerFutureValues;

	public OutlierTreatmentLocalStandardDeviationBased() {
		this(2.96, 3, true, Double.NaN);
	}

	public OutlierTreatmentLocalStandardDeviationBased(double stdDeviationRatio, int elements) {
		this(stdDeviationRatio, elements, true, Double.NaN);
	}

	public OutlierTreatmentLocalStandardDeviationBased(double stdDeviationRatio, int elements, boolean considerFutureValues) {
		this(stdDeviationRatio, elements, considerFutureValues, Double.NaN);
	}

	public OutlierTreatmentLocalStandardDeviationBased(double stdDeviationRatio, int elements, boolean considerFutureValues, double outlierValue) {
		this.stdDeviationRatio = stdDeviationRatio;
		this.elements = elements;
		this.considerFutureValues = considerFutureValues;
		this.outlierValue = outlierValue;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesMultivariate>> getAlternativeParameterizations(int count) {
		List<IDataProcessor<ITimeSeriesMultivariate>> alternatives = new ArrayList<>();

		int sqrt = (int) Math.sqrt(count);

		List<Double> alternativeDoubles = ParameterSupportTools.getAlternativeDoubles(stdDeviationRatio, sqrt + 1);
		List<Integer> alternativeElements = ParameterSupportTools.getAlternativeIntegers(elements, sqrt);

		for (Double std : alternativeDoubles)
			for (Integer ele : alternativeElements) {
				if (std > 0 && ele > 0)
					alternatives.add(new OutlierTreatmentLocalStandardDeviationBased(std, ele));
				if (alternatives.size() == count)
					return alternatives;
			}

		return alternatives;
	}

	@Override
	protected void initializeUnivariateTimeSeriesProcessor() {
		setUnivariateTimeSeriesProcessor(new com.github.TKnudsen.timeseries.operations.preprocessing.univariate.OutlierTreatmentLocalStandardDeviationBased(stdDeviationRatio, elements, considerFutureValues, outlierValue));
	}

}
