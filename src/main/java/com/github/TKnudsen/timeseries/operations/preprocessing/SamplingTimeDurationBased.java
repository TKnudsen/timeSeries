package com.github.TKnudsen.timeseries.operations.preprocessing;

import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;

import java.util.List;

import com.github.TKnudsen.timeseries.data.ITimeSeries;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.primitives.TimeQuantization;

/**
 * <p>
 * Title: SamplingTimeDurationBased
 * </p>
 * 
 * <p>
 * Description: applies sampling based on a given time duration. all time-value
 * pairs within an interval < the duration are removed successively.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public abstract class SamplingTimeDurationBased<TS extends ITimeSeries<?>> extends TimeSeriesProcessor<TS> {

	protected TimeDuration timeDurationKernel;

	@SuppressWarnings("unused")
	private SamplingTimeDurationBased() {
		this(TimeQuantization.SECONDS, 3L);
	}

	public SamplingTimeDurationBased(TimeQuantization timeQuantization, long count) {
		this(new TimeDuration(timeQuantization, count));
	}

	public SamplingTimeDurationBased(TimeDuration timeDurationKernel) {
		this.timeDurationKernel = timeDurationKernel;
	}

	private void process(TS timeSeries) {
		if (timeSeries == null || timeSeries.size() == 0)
			return;

		long timeStamp = timeSeries.getFirstTimestamp();
		for (int i = 1; i < timeSeries.size(); i++) {
			if (timeSeries.getTimestamp(i) - timeDurationKernel.getDuration() < timeStamp) {
				timeSeries.removeTimeValue(i--);
			} else
				timeStamp = timeSeries.getTimestamp(i);
		}
	}

	@Override
	public void process(List<TS> data) {
		if (data == null)
			return;

		for (TS timeSeries : data)
			process(timeSeries);
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_REDUCTION;
	}

	public TimeDuration getTimeDurationKernel() {
		return timeDurationKernel;
	}

	public void setTimeDurationKernel(TimeDuration timeDurationKernel) {
		this.timeDurationKernel = timeDurationKernel;
	}
}
