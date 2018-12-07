package com.github.TKnudsen.timeseries.operations.preprocessing.uncertaintyMeasures;

import com.github.TKnudsen.ComplexDataObject.data.complexDataObject.ComplexDataObject;
import com.github.TKnudsen.ComplexDataObject.data.interfaces.ISelfDescription;
import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.IValueUncertainty;

import com.github.TKnudsen.timeseries.data.ITimeSeries;
import com.github.TKnudsen.timeseries.data.ITimeSeriesListener;
import com.github.TKnudsen.timeseries.data.TimeSeriesEvent;
import com.github.TKnudsen.timeseries.data.uncertainty.ITimeSeriesValueUncertaintyCalculationResult;

/**
 * <p>
 * timeSeries
 * </p>
 * 
 * <p>
 * Basic class for calculations of value uncertainty information. The measure
 * can be attached to a process that produces uncertainties using the listener
 * concept.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.10
 */
public abstract class TimeSeriesUncertaintyMeasure<TS extends ITimeSeries<?>, U extends IValueUncertainty, VU>
		extends ComplexDataObject
		implements ISelfDescription, ITimeSeriesValueUncertaintyMeasure<TS, VU>, ITimeSeriesListener<TS> {
//	implements IProcessingUncertaintyMeasure<TS, U>,IUncertaintyAtTimeStamp<U>

	// TODO decide whether the uncertainties should rather be handed to some
	// observer instantly. Goal: avoid state variables in the measures. The problem
	// is that working with resulting uncertainties often only makes sense as soon
	// as the process that produced uncertainties is ready.
//	/**
//	 * @deprecated use timeSeriesValueUncertainty
//	 */
//	protected SortedMap<Long, U> uncertaintiesOverTime;

	protected ITimeSeriesValueUncertaintyCalculationResult<VU> timeSeriesValueUncertainty;

//	@Override
//	public U getUncertainty(Long timeStamp) {
//		if (uncertaintiesOverTime == null) {
//			System.err.println(getName() + ": no uncertainty information calculated yet.");
//			return null;
//		}
//
//		return uncertaintiesOverTime.get(timeStamp);
//	}

//	@Override
//	public U getUncertainty(int index) {
//		throw new IllegalArgumentException(getName()
//				+ "index-based access to uncertainty information of time series is unsafe and thus deprecated.");
//	}

	@Override
	public void valueDomainChanged(TimeSeriesEvent<TS> timeSeriesEvent) {
//		calculateUncertainty(timeSeriesEvent.getOldTimeSeries(), timeSeriesEvent.getTimeSeries());
		timeSeriesValueUncertainty = compute(timeSeriesEvent.getOldTimeSeries(), timeSeriesEvent.getTimeSeries());
	}

	@Override
	public void temporalDomainChanged(TimeSeriesEvent<TS> timeSeriesEvent) {
//		calculateUncertainty((TS) timeSeriesEvent.getOldTimeSeries(), (TS) timeSeriesEvent.getTimeSeries());
		timeSeriesValueUncertainty = compute(timeSeriesEvent.getOldTimeSeries(), timeSeriesEvent.getTimeSeries());
	}

//	public SortedMap<Long, U> getUncertaintiesOverTime() {
//		return uncertaintiesOverTime;
//	}

//	public void addUncertaintiesOverTime(SortedMap<Long, U> uncertainties) {
//		this.uncertaintiesOverTime = uncertainties;
//	}

	public ITimeSeriesValueUncertaintyCalculationResult<?> getTimeSeriesValueUncertainty() {
		return timeSeriesValueUncertainty;
	}

}
