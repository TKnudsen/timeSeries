package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.NumericalUncertainty;
import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.IProcessingUncertaintyMeasure;
import com.github.TKnudsen.ComplexDataObject.model.processors.IUncertainDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.uncertainty.processing.RelativeValueDomainModificationMeasure;
import com.github.TKnudsen.timeseries.operations.preprocessing.univariate.MovingAverage;

/**
 * <p>
 * Title: OutlierTreatmentMovingAverageBased
 * </p>
 * 
 * <p>
 * Description: Replaces values that are farer away from the calculated moving
 * average than a given standard deviation ratio. Replaces with the minimum
 * maximum value that is still allowed. The temporal domain is untouched.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.05
 */
public class OutlierTreatmentMovingAverageBased extends DimensionBasedTimeSeriesMultivariateProcessor 
	implements IUncertainDataProcessor<ITimeSeriesMultivariate, NumericalUncertainty> {

	// standard deviation ratio
	double stdDeviationRatio;

	// moving average options
	private int elements;
	private boolean considerFutureValues;

	private MovingAverage movingAverage;

	public OutlierTreatmentMovingAverageBased() {
		this(2.96, 3, true);
	}

	public OutlierTreatmentMovingAverageBased(double stdDeviationRatio, int elements) {
		this(stdDeviationRatio, elements, true);
	}

	public OutlierTreatmentMovingAverageBased(double stdDeviationRatio, int elements, boolean considerFutureValues) {
		this.stdDeviationRatio = stdDeviationRatio;
		this.elements = elements;
		this.considerFutureValues = considerFutureValues;
	}

	public OutlierTreatmentMovingAverageBased(double stdDeviationRatio, MovingAverage movingAverage) {
		this.stdDeviationRatio = stdDeviationRatio;
		this.movingAverage = movingAverage;
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
					alternatives.add(new OutlierTreatmentMovingAverageBased(std, ele, considerFutureValues));
				if (alternatives.size() == count)
					return alternatives;
			}

		return alternatives;
	}

	@Override
	protected void initializeUnivariateTimeSeriesProcessor() {
		if (movingAverage == null)
			setUnivariateTimeSeriesProcessor(new com.github.TKnudsen.timeseries.operations.preprocessing.univariate.OutlierTreatmentMovingAverageBased(stdDeviationRatio, elements, considerFutureValues));
		else
			setUnivariateTimeSeriesProcessor(new com.github.TKnudsen.timeseries.operations.preprocessing.univariate.OutlierTreatmentMovingAverageBased(stdDeviationRatio, movingAverage));
	}

	public double getStdDeviationRatio() {
		return stdDeviationRatio;
	}

	public int getElements() {
		return elements;
	}

	public boolean isConsiderFutureValues() {
		return considerFutureValues;
	}

	@Override
	public IProcessingUncertaintyMeasure<ITimeSeriesMultivariate, NumericalUncertainty> getUncertaintyMeasure(ITimeSeriesMultivariate originalTS, ITimeSeriesMultivariate processedTS) {
		return new RelativeValueDomainModificationMeasure(originalTS, processedTS);
	}
}
