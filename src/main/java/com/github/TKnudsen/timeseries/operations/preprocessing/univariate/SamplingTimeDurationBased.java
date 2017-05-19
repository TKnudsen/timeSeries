package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.primitives.TimeQuantization;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

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
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class SamplingTimeDurationBased implements ITimeSeriesUnivariatePreprocessor {

	private TimeDuration timeDurationKernel;

	@SuppressWarnings("unused")
	private SamplingTimeDurationBased() {
		timeDurationKernel = new TimeDuration(TimeQuantization.SECONDS, 3);
	}

	public SamplingTimeDurationBased(TimeQuantization timeQuantization, long count) {
		timeDurationKernel = new TimeDuration(timeQuantization, count);
	}

	public SamplingTimeDurationBased(TimeDuration timeDurationKernel) {
		this.timeDurationKernel = timeDurationKernel;
	}

	@Override
	public void process(List<ITimeSeriesUnivariate> data) {
		if (data == null)
			return;

		for (ITimeSeriesUnivariate timeSeries : data)
			process(timeSeries);
	}

	private void process(ITimeSeriesUnivariate timeSeries) {
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
	public List<IDataProcessor<ITimeSeriesUnivariate>> getAlternativeParameterizations(int count) {
		List<IDataProcessor<ITimeSeriesUnivariate>> processors = new ArrayList<>();

		long typeFactor = timeDurationKernel.getTypeFactor();

		List<Long> alternativeLongs = ParameterSupportTools.getAlternativeLongs(typeFactor, count);

		for (Long l : alternativeLongs)
			if (l > 0)
				processors.add(new SamplingTimeDurationBased(timeDurationKernel.getType(), l));

		return processors;
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_REDUCTION;
	}

}
