package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.uncertainty.processing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.TreeMap;

import org.apache.commons.math3.distribution.NormalDistribution;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.distribution.NumericalDistributionUncertainty;
import com.github.TKnudsen.ComplexDataObject.model.tools.StatisticsSupport;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.uncertaintyMeasures.TimeSeriesProcessingUncertaintyMeasure;

/**
 * <p>
 * Title: RelativeValueDomainModificationMeasure
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
 * @author Juergen Bernard, Christian Bors
 * @version 1.04
 */
public class RelativeValueDomainModificationMeasure
		extends TimeSeriesProcessingUncertaintyMeasure<ITimeSeriesMultivariate> {

	private static Double epsilon = 0.000000001;
	private Double samplingRate = 0.4;

	public RelativeValueDomainModificationMeasure() {
		super();
	}

	public RelativeValueDomainModificationMeasure(Double samplingRate) {
		this.samplingRate = samplingRate;
	}

	@Override
	public String getName() {
		return "RelativeValueDomainModificationMeasure";
	}

	@Override
	public String getDescription() {
		return "Calculates the relative change of the value domains of a given MVTS over time";
	}

	@Override
	public void calculateUncertainty(ITimeSeriesMultivariate originalData, ITimeSeriesMultivariate processedData) {
		uncertaintiesOverTime = new TreeMap<>();

		int maxSampleSize = originalData.getTimestamps().size();
		Random samplingGenerator = new Random();
		List<StatisticsSupport> samplingStatsList = new ArrayList<>();
		List<NormalDistribution> normalDistributions = new ArrayList<>();

		List<Double> originalValues;
		List<Double> modifiedValues;

		Iterator<Long> tsIt;
		if (originalData.getTimestamps().size() > processedData.getTimestamps().size()) {
			tsIt = originalData.getTimestamps().iterator();
		} else {
			tsIt = processedData.getTimestamps().iterator();
		}

		for (int i = 0; i < processedData.getDimensionality(); ++i)
			samplingStatsList.add(new StatisticsSupport(new ArrayList<>()));

		int samplesNeeded = (int) (maxSampleSize * this.samplingRate);
		// this loop calculates a random number based on the timeStamp size
		while (samplesNeeded > 0 && tsIt.hasNext()) {
			int rand = samplingGenerator.nextInt(maxSampleSize);

			// only add value if it is below the samples needed value
			if (rand < samplesNeeded) {
				Long timeStamp = tsIt.next();
				originalValues = originalData.getValue(timeStamp, false);

				try {
					modifiedValues = processedData.getValue(timeStamp, false);
				} catch (Exception e) {
					continue;
				}
				ListIterator<Double> modIt = modifiedValues.listIterator();
				ListIterator<Double> origIt = originalValues.listIterator();
				while (origIt.hasNext()) {
					int dimensionIdx = origIt.nextIndex();
					Double origVal = origIt.next();
					Double modVal = modIt.next();

					if (modVal == null)
						samplingStatsList.get(dimensionIdx).addValue(1.0);
					else
						samplingStatsList.get(dimensionIdx).addValue(getRelativeValue(origVal, modVal));

				}
				samplesNeeded--;
			} else {
				tsIt.next();
			}
		}

		for (StatisticsSupport stats : samplingStatsList) {
			if (stats.getVariance() > 0)
				normalDistributions.add(new NormalDistribution(stats.getMean(), Math.sqrt(stats.getVariance())));
			else
				normalDistributions.add(null);
		}

		for (Long timeStamp : originalData.getTimestamps()) {
			originalValues = originalData.getValue(timeStamp, false);
			modifiedValues = null;

			try {
				modifiedValues = processedData.getValue(timeStamp, false);
			} catch (Exception e) {
				// System.err.println(
				// getName() + ": unable to retrieve time stamp of original time series in
				// modified time series");
				// uncertaintiesOverTime.put(timeStamp, new NumericalUncertainty(relatives));
				continue;
			}

			List<Double> deviations = new ArrayList<>(originalValues.size());
			// // TODO please validate
			for (int i = 0; i < originalValues.size(); i++) {
				// we assume that our relative deviation is normally distributed
				if (samplingStatsList.get(i).getVariance() > 0) {
					Double originalValue = originalValues.get(i);
					Double modifiedValue = modifiedValues.get(i);
					if (originalValue == null ^ modifiedValue == null) // exclusive or, change is maximal if either
																		// value is null
						deviations.add(1.0);

					// relative difference
					Double relativeValue = getRelativeValue(originalValue, modifiedValue);
					// normalized relative difference
					Double normalizedRelDif = Math.abs(relativeValue - samplingStatsList.get(i).getMean());
					// cumulative probability
					Double cumProb = normalDistributions.get(i).cumulativeProbability(normalizedRelDif);
					// subtracted by 0.5 (due to this being highest density around mean
					cumProb -= 0.5;
					// take absolute, to compensate for small variations around mean
					cumProb = Math.abs(cumProb);

					// absolute value of difference between cumulative prob. of relative difference,
					// normalized by mean
					deviations.add(Math.abs(cumProb));
				} else {
					deviations.add(0.0);
				}
			}
			uncertaintiesOverTime.put(timeStamp, new NumericalDistributionUncertainty(deviations));
		}
	}

	private double getRelativeValue(double originalValue, double modifiedValue) {
		// relative change.
		if (originalValue == 0)
			if (modifiedValue != 0)
				return 1.0;// from zero to something means 100% change. ARGHH
			else
				return 0.0;
		else if (modifiedValue == originalValue)
			return 0.0;
		else {
			double relativeValue = (modifiedValue - originalValue) / originalValue;

			if (Math.abs(relativeValue) < epsilon)
				return 0.0;
			return relativeValue;
		}
	}
}