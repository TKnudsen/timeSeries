package com.github.TKnudsen.timeseries.data.univariate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.IUncertaintyQuantitative;
import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.NumericalUncertainty;
import com.github.TKnudsen.timeseries.data.ITemporalUncertainty;

public class UncertaintyTimeSeries extends TimeSeriesUnivariate implements ITemporalUncertainty<IUncertaintyQuantitative<Double>> {

	private List<IUncertaintyQuantitative<Double>> uncertainties;

	public UncertaintyTimeSeries(List<Long> timeStamps, List<Double> values, List<IUncertaintyQuantitative<Double>> uncertainties) {
		super(timeStamps, values);
	}

	public UncertaintyTimeSeries(List<Long> timeStamps, List<Double> values, Collection<List<Double>> uncertainties) {
		super(timeStamps, values);

		this.uncertainties = new ArrayList<>();
		for (List<Double> u : uncertainties)
			this.uncertainties.add(new NumericalUncertainty(u));
	}

	@Override
	public IUncertaintyQuantitative<Double> getUncertainty(Long timeStamp) {
		List<Long> timestamps = getTimestamps();
		for (int i = 0; i < timestamps.size(); i++)
			if (timestamps.get(i).equals(timeStamp))
				return getUncertainty(i);

		return null;
	}

	@Override
	public IUncertaintyQuantitative<Double> getUncertainty(int index) {
		return uncertainties.get(index);
	}

}
