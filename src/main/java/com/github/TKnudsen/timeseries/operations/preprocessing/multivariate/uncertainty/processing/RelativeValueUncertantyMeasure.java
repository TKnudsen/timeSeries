package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.uncertainty.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.NumericalUncertainty;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;

/**
 * <p>
 * Title: RelativeValueUncertantyMeasure
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public class RelativeValueUncertantyMeasure
		extends TimeSeriesProcessingUncertaintyMeasure<ITimeSeriesMultivariate, NumericalUncertainty> {

	private static Double epsilon = 0.000000001;

	public RelativeValueUncertantyMeasure() {
		super();
	}

	@Override
	public String getName() {
		return "RelativeValueUncertantyMeasure";
	}

	@Override
	public String getDescription() {
		return "Calculates the relative change of the value domains of a given MVTS over time";
	}

	@Override
	public void calculateUncertainty(ITimeSeriesMultivariate originalTimeSeries,
			ITimeSeriesMultivariate processedTimeSeries) {

		uncertaintiesOverTime = new TreeMap<>();

		for (Long timeStamp : originalTimeSeries.getTimestamps()) {
			List<Double> originalValues = originalTimeSeries.getValue(timeStamp, false);
			List<Double> modifiedValues = null;

			List<Double> relatives = new ArrayList<>();
			try {
				modifiedValues = processedTimeSeries.getValue(timeStamp, false);
			} catch (Exception e) {
				// System.err.println(
				// getName() + ": unable to retrieve time stamp of original time series in
				// modified time series");
				// uncertaintiesOverTime.put(timeStamp, new NumericalUncertainty(relatives));
				continue;
			}

			// TODO please validate
			for (int i = 0; i < originalValues.size(); i++) {
				if (modifiedValues == null)
					relatives.add(1.0); // no value means 100% change
				else if (modifiedValues.get(i) == null)
					relatives.add(1.0); // no value means 100% change
				else {
					// relative change.
					if (originalValues.get(i) == 0)
						if (modifiedValues.get(i) != 0)
							relatives.add(1.0);// from zero to something means 100% change. ARGHH
						else
							relatives.add(0.0);
					else if (modifiedValues.get(i) == originalValues.get(i))
						relatives.add(0.0);
					else {
						double rel = (modifiedValues.get(i) - originalValues.get(i)) / originalValues.get(i);
						// if(Math.abs(rel) > 1) {
						// System.out.println("Error, relative value larger than 1: " + rel);
						// }
						if (Math.abs(rel) < epsilon)
							rel = 0.0;
						relatives.add(rel);
					}
				}
			}

			uncertaintiesOverTime.put(timeStamp, new NumericalUncertainty(relatives));
		}
	}

}
