package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.uncertainty.processing;

import java.util.SortedMap;

import com.github.TKnudsen.ComplexDataObject.data.complexDataObject.ComplexDataObject;
import com.github.TKnudsen.ComplexDataObject.data.interfaces.ISelfDescription;
import com.github.TKnudsen.ComplexDataObject.data.uncertainty.IUncertainty;
import com.github.TKnudsen.ComplexDataObject.model.processors.IProcessingUncertaintyMeasure;
import com.github.TKnudsen.timeseries.data.ITimeSeries;
import com.github.TKnudsen.timeseries.data.ITimeSeriesListener;
import com.github.TKnudsen.timeseries.data.IUncertaintyAtTimeStamp;
import com.github.TKnudsen.timeseries.data.TimeSeriesEvent;
import com.github.TKnudsen.timeseries.data.multivariate.TimeSeriesMultivariate;

/**
 * <p>
 * Title: TimeSeriesProcessingUncertaintyMeasure
 * </p>
 * 
 * <p>
 * Description: Basic class for calculations of value uncertainty information of
 * a (time series) processor.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.05
 */
public abstract class TimeSeriesProcessingUncertaintyMeasure<ITimeSeriesMultivariate, U extends IUncertainty<?>>
		extends ComplexDataObject implements IUncertaintyAtTimeStamp<U>, ISelfDescription,
		IProcessingUncertaintyMeasure<ITimeSeriesMultivariate, U>, ITimeSeriesListener {

	// TODO decide whether the uncertainties should rather be handed to some
	// obeserver instantly. Goal: avoid state variables in the measures.
	protected SortedMap<Long, U> uncertaintiesOverTime;

	@Override
	public U getUncertainty(Long timeStamp) {
		if (uncertaintiesOverTime == null) {
			System.err.println(getName() + ": no uncertainty information calculated yet.");
			return null;
		}

		return uncertaintiesOverTime.get(timeStamp);
	}

	@Override
	public U getUncertainty(int index) {
		throw new IllegalArgumentException(getName()
				+ "index-based access to uncertainty information of time series is unsafe and thus deprecated.");
	}

	@Override
	public void valueDomainChanged(TimeSeriesEvent learningDataEvent) {
		ITimeSeries<?> oldTimeSeries = learningDataEvent.getOldTimeSeries();
		if (oldTimeSeries.getClass().isAssignableFrom(TimeSeriesMultivariate.class))
			calculateUncertainty((ITimeSeriesMultivariate) learningDataEvent.getOldTimeSeries(),
					(ITimeSeriesMultivariate) learningDataEvent.getTimeSeries());
	}

	@Override
	public void temporalDomainChanged(TimeSeriesEvent learningDataEvent) {
		ITimeSeries<?> oldTimeSeries = learningDataEvent.getOldTimeSeries();
		if (oldTimeSeries.getClass().isAssignableFrom(TimeSeriesMultivariate.class))
			calculateUncertainty((ITimeSeriesMultivariate) learningDataEvent.getOldTimeSeries(),
					(ITimeSeriesMultivariate) learningDataEvent.getTimeSeries());
	}

	public SortedMap<Long, U> getUncertaintiesOverTime() {
		return uncertaintiesOverTime;
	}

}
