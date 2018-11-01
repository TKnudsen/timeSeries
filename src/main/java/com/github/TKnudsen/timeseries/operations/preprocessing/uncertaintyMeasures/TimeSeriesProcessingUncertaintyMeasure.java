package com.github.TKnudsen.timeseries.operations.preprocessing.uncertaintyMeasures;

import java.util.SortedMap;

import com.github.TKnudsen.ComplexDataObject.data.complexDataObject.ComplexDataObject;
import com.github.TKnudsen.ComplexDataObject.data.interfaces.ISelfDescription;
import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.IValueUncertainty;
import com.github.TKnudsen.ComplexDataObject.model.processors.IProcessingUncertaintyMeasure;
import com.github.TKnudsen.timeseries.data.ITimeSeries;
import com.github.TKnudsen.timeseries.data.ITimeSeriesListener;
import com.github.TKnudsen.timeseries.data.IUncertaintyAtTimeStamp;
import com.github.TKnudsen.timeseries.data.TimeSeriesEvent;

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
 * @version 1.06
 */
public abstract class TimeSeriesProcessingUncertaintyMeasure<TS extends ITimeSeries<?>> extends ComplexDataObject
		implements IUncertaintyAtTimeStamp<IValueUncertainty<Double>>, ISelfDescription,
		IProcessingUncertaintyMeasure<TS, IValueUncertainty<Double>>, ITimeSeriesListener<TS> {

	// TODO decide whether the uncertainties should rather be handed to some
	// obeserver instantly. Goal: avoid state variables in the measures.
	protected SortedMap<Long, IValueUncertainty<Double>> uncertaintiesOverTime;

	@Override
	public IValueUncertainty<Double> getUncertainty(Long timeStamp) {
		if (uncertaintiesOverTime == null) {
			System.err.println(getName() + ": no uncertainty information calculated yet.");
			return null;
		}

		return uncertaintiesOverTime.get(timeStamp);
	}

	@Override
	public IValueUncertainty<Double> getUncertainty(int index) {
		throw new IllegalArgumentException(getName()
				+ "index-based access to uncertainty information of time series is unsafe and thus deprecated.");
	}

	@Override
	public void valueDomainChanged(TimeSeriesEvent<TS> learningDataEvent) {
		calculateUncertainty(learningDataEvent.getOldTimeSeries(), learningDataEvent.getTimeSeries());
	}

	@Override
	public void temporalDomainChanged(TimeSeriesEvent<TS> learningDataEvent) {
		calculateUncertainty((TS) learningDataEvent.getOldTimeSeries(), (TS) learningDataEvent.getTimeSeries());
	}

	public SortedMap<Long, IValueUncertainty<Double>> getUncertaintiesOverTime() {
		return uncertaintiesOverTime;
	}

	public void addUncertaintiesOverTime(SortedMap<Long, IValueUncertainty<Double>> uncertainties) {
		this.uncertaintiesOverTime = uncertainties;
	}
}
