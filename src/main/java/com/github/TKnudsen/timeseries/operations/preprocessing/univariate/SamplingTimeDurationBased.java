package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
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
 * Copyright: Copyright (c) 2017-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public class SamplingTimeDurationBased extends
		com.github.TKnudsen.timeseries.operations.preprocessing.SamplingTimeDurationBased<ITimeSeriesUnivariate> {

	private TimeDuration timeDurationKernel;

	@SuppressWarnings("unused")
	private SamplingTimeDurationBased() {
		this(TimeQuantization.SECONDS, 3L);
	}

	public SamplingTimeDurationBased(TimeQuantization timeQuantization, long count) {
		this(new TimeDuration(timeQuantization, count));
	}

	public SamplingTimeDurationBased(TimeDuration timeDurationKernel) {
		super(timeDurationKernel);
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

}
