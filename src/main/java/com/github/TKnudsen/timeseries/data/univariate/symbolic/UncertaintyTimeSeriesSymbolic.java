package com.github.TKnudsen.timeseries.data.univariate.symbolic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.string.LabelUncertainty;
import com.github.TKnudsen.timeseries.data.ITemporalUncertainty;

/**
 * <p>
 * Title: UncertaintyTimeSeriesSymbolic
 * </p>
 * 
 * <p>
 * Description: Uncertainty data model for univariate symbolic time series.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class UncertaintyTimeSeriesSymbolic extends SymbolicTimeSeries implements ITemporalUncertainty<LabelUncertainty> {

	private List<LabelUncertainty> labelUncertainties;

	public UncertaintyTimeSeriesSymbolic(List<Long> timeStamps, List<String> values, Collection<Map<String, Double>> labelUncertainties) {
		super(timeStamps, values);

		this.labelUncertainties = new ArrayList<>();
		for (Map<String, Double> distribution : labelUncertainties) {
			this.labelUncertainties.add(new LabelUncertainty(distribution));
		}
	}

	public UncertaintyTimeSeriesSymbolic(List<Long> timeStamps, List<String> values, List<LabelUncertainty> labelUncertainties) {
		super(timeStamps, values);

		this.labelUncertainties = labelUncertainties;
	}

	@Override
	public LabelUncertainty getUncertainty(Long timeStamp) {
		List<Long> timestamps = getTimestamps();
		for (int i = 0; i < timestamps.size(); i++)
			if (timestamps.get(i).equals(timeStamp))
				return getUncertainty(i);

		return null;
	}

	@Override
	public LabelUncertainty getUncertainty(int index) {
		return labelUncertainties.get(index);
	}
}