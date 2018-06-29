package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.uncertainty.processing;

import java.util.List;
import java.util.SortedMap;

import com.github.TKnudsen.ComplexDataObject.data.complexDataObject.ComplexDataObject;
import com.github.TKnudsen.ComplexDataObject.data.interfaces.ISelfDescription;
import com.github.TKnudsen.ComplexDataObject.data.uncertainty.IUncertainty;
import com.github.TKnudsen.ComplexDataObject.model.processors.IProcessingUncertaintyMeasure;
import com.github.TKnudsen.timeseries.data.ITimeSeries;
import com.github.TKnudsen.timeseries.data.IUncertaintyAtTimeStamp;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;

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
 * @version 1.02
 */
public abstract class TimeSeriesProcessingUncertaintyMeasure<TS extends ITimeSeries<?>, U extends IUncertainty<?>> extends ComplexDataObject
		implements IUncertaintyAtTimeStamp<U>, ISelfDescription, IProcessingUncertaintyMeasure<TS, U> {

	private ITimeSeriesMultivariate originalTimeSeries;

	private ITimeSeriesMultivariate processedTimeSeries;

	protected SortedMap<Long, U> uncertaintiesOverTime;

	public TimeSeriesProcessingUncertaintyMeasure(ITimeSeriesMultivariate originalTimeSeries,
			ITimeSeriesMultivariate processedTimeSeries) {
		this.originalTimeSeries = originalTimeSeries;
		this.processedTimeSeries = processedTimeSeries;
	}
	
	public TimeSeriesProcessingUncertaintyMeasure() {
		super();
	}

	public abstract void calculateUncertaintyMeasure();

	public ITimeSeriesMultivariate getOriginalTimeSeries() {
		return originalTimeSeries;
	}

	public ITimeSeriesMultivariate getProcessedTimeSeries() {
		return processedTimeSeries;
	}

	public List<Long> getTimestamps() {
		return originalTimeSeries.getTimestamps();
	}

	@Override
	public U getUncertainty(Long timeStamp) {
		if (uncertaintiesOverTime == null)
			calculateUncertaintyMeasure();

		if (uncertaintiesOverTime == null) {
			System.err.println(getName() + ": problems with the calculation of uncertainty information.");
			return null;
		}

		return uncertaintiesOverTime.get(timeStamp);
	}

	@Override
	public U getUncertainty(int index) {
		long timestamp = processedTimeSeries.getTimestamp(index);

		return getUncertainty(timestamp);
	}

}
