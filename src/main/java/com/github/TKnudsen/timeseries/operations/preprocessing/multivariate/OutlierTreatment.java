package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;

/**
 * <p>
 * Title: OutlierTreatment
 * </p>
 * 
 * <p>
 * Description: Replaces the value domain for values higher/lower than a given
 * multiple of the standard deviation. Replaces with the minimum maximum value
 * that is still allowed. The temporal domain is untouched.
 * 
 * Disclaimer: uses a global std and not local. Implementation is not really
 * sophisticated.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public class OutlierTreatment extends DimensionBasedTimeSeriesMultivariateProcessor {

	// standard deviation ratio
	double stdDeviationRatio;

	public OutlierTreatment() {
		this(2.96);
	}

	public OutlierTreatment(double stdDeviationRatio) {
		this.stdDeviationRatio = stdDeviationRatio;
	}

	@Override
	public List<IDataProcessor<ITimeSeriesMultivariate>> getAlternativeParameterizations(int count) {
		List<IDataProcessor<ITimeSeriesMultivariate>> alternatives = new ArrayList<>();

		List<Double> alternativeDoubles = ParameterSupportTools.getAlternativeDoubles(stdDeviationRatio, count);

		for (Double std : alternativeDoubles) {
			if (std > 0)
				alternatives.add(new OutlierTreatment(std));
			if (alternatives.size() == count)
				return alternatives;
		}

		return alternatives;
	}

	@Override
	protected void initializeUnivariateTimeSeriesProcessor() {
		setUnivariateTimeSeriesProcessor(new com.github.TKnudsen.timeseries.operations.preprocessing.univariate.OutlierTreatment(stdDeviationRatio));
	}

	public double getStdDeviationRatio() {
		return stdDeviationRatio;
	}
}
