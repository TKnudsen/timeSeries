package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

/**
 * <p>
 * Title: OutlierTreatmentLocalStandardDeviationBased
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * @author Christian Ritter
 * @version 1.01
 */
public class OutlierTreatmentLocalStandardDeviationBased implements ITimeSeriesUnivariatePreprocessor {

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
	public void process(List<ITimeSeriesUnivariate> data) {
		for (ITimeSeriesUnivariate timeSeries : data)
			process(timeSeries);
	}

	private void process(ITimeSeriesUnivariate timeSeries) {

		if (timeSeries == null)
			return;

		int size = timeSeries.size();

		for (int i = 0; i < size; i++) {
			int start = Math.max(0, i - elements);
			int end = Math.min(i + (considerFutureValues ? elements : 0), size);
			double[] vals = new double[end - start];
			int index = 0;
			for (int j = start; j < end; j++) {
				vals[index] = timeSeries.getValue(j);
			}
			DescriptiveStatistics ds = new DescriptiveStatistics(vals);
			if (Math.abs(timeSeries.getValue(i) - ds.getMean()) > ds.getStandardDeviation() * stdDeviationRatio) {
				timeSeries.replaceValue(i, outlierValue);
			}
		}
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_CLEANING;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesUnivariate>> getAlternativeParameterizations(int count) {
		List<IDataProcessor<ITimeSeriesUnivariate>> alternatives = new ArrayList<>();

		int sqrt = (int) Math.sqrt(count);

		List<Double> alternativeDoubles = ParameterSupportTools.getAlternativeDoubles(stdDeviationRatio, sqrt + 1);
		List<Integer> alternativeElements = ParameterSupportTools.getAlternativeIntegers(elements, sqrt);

		for (Double std : alternativeDoubles)
			for (Integer ele : alternativeElements) {
				if (std > 0 && ele > 0)
					alternatives.add(new OutlierTreatmentMovingAverageBased(std, ele));
				if (alternatives.size() == count)
					return alternatives;
			}

		return alternatives;
	}

}
