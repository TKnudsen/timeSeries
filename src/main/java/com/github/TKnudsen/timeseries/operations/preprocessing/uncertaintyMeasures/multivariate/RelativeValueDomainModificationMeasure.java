package com.github.TKnudsen.timeseries.operations.preprocessing.uncertaintyMeasures.multivariate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.IValueUncertainty;
import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.ValueUncertainty;
import com.github.TKnudsen.ComplexDataObject.data.uncertainty.distribution.ValueUncertaintyDistribution;
import com.github.TKnudsen.ComplexDataObject.model.tools.DataConversion;
import com.github.TKnudsen.ComplexDataObject.model.tools.StatisticsSupport;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;
import com.github.TKnudsen.timeseries.data.uncertainty.ITimeSeriesValueUncertaintyCalculationResult;
import com.github.TKnudsen.timeseries.data.uncertainty.multivariate.TimeSeriesMultivariateValueUncertaintyCalculationResult;
import com.github.TKnudsen.timeseries.data.uncertainty.multivariate.UncertaintyMultivariateTimeSeries;

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
 * @author Christian Bors
 * @version 1.07
 */
public class RelativeValueDomainModificationMeasure extends TimeSeriesMultivariateUncertaintyMeasure {

	private static Double epsilon = 0.000000001;
	private Double samplingRate = 0.4;

	/**
	 * representation of value uncertainty with a ValueUncertaintyDistribution for
	 * every time stamp and dimension
	 */
	private boolean calculateDistributionUncertainty;

	public RelativeValueDomainModificationMeasure() {
		super();
	}

	public RelativeValueDomainModificationMeasure(Double samplingRate) {
		this.samplingRate = samplingRate;
	}

	public RelativeValueDomainModificationMeasure(boolean calculateDistributionUncertainty) {
		this.calculateDistributionUncertainty = calculateDistributionUncertainty;
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
	public ITimeSeriesValueUncertaintyCalculationResult<List<IValueUncertainty>> compute(
			ITimeSeriesMultivariate originalTimeSeries, ITimeSeriesMultivariate processedTimeSeries) {
		// uncertaintiesOverTime = new TreeMap<>();

		int maxSampleSize = originalTimeSeries.getTimestamps().size();
		Random samplingGenerator = new Random();
		List<StatisticsSupport> samplingStatsList = new ArrayList<>();
		List<NormalDistribution> normalDistributions = new ArrayList<>();

		List<Double> originalValues;
		List<Double> modifiedValues;

		Iterator<Long> tsIt;
		if (originalTimeSeries.getTimestamps().size() > processedTimeSeries.getTimestamps().size()) {
			tsIt = originalTimeSeries.getTimestamps().iterator();
		} else {
			tsIt = processedTimeSeries.getTimestamps().iterator();
		}

		for (int i = 0; i < processedTimeSeries.getDimensionality(); ++i)
			samplingStatsList.add(new StatisticsSupport(new ArrayList<>()));

		int samplesNeeded = (int) (maxSampleSize * this.samplingRate);
		// this loop calculates a random number based on the timeStamp size
		while (samplesNeeded > 0 && tsIt.hasNext()) {
			int rand = samplingGenerator.nextInt(maxSampleSize);

			// only add value if it is below the samples needed value
			if (rand < samplesNeeded) {
				Long timeStamp = tsIt.next();
				originalValues = originalTimeSeries.getValue(timeStamp, false);

				try {
					modifiedValues = processedTimeSeries.getValue(timeStamp, false);
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

		List<Long> timeStamps = new ArrayList<>();
		List<List<IValueUncertainty>> valueUncertaintiesAllDimensionsOverTime = new ArrayList<>();

		for (Long timeStamp : originalTimeSeries.getTimestamps()) {
			originalValues = originalTimeSeries.getValue(timeStamp, false);
			modifiedValues = null;

			try {
				modifiedValues = processedTimeSeries.getValue(timeStamp, false);
			} catch (Exception e) {
				// System.err.println(
				// getName() + ": unable to retrieve time stamp of original time series in
				// modified time series");
				// uncertaintiesOverTime.put(timeStamp, new NumericalUncertainty(relatives));
				continue;
			}

			List<Double> deviations = new ArrayList<>(originalValues.size());
			List<IValueUncertainty> valueUncertainties = new ArrayList<>();

			// // TODO please validate
			for (int i = 0; i < originalValues.size(); i++) {

				double vu = 0;
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
					vu = Math.abs(cumProb);
				}

				deviations.add(vu); // cumProb or 0.0

				if (calculateDistributionUncertainty)
					valueUncertainties.add(new ValueUncertaintyDistribution(
							DataConversion.doublePrimitivesToList(samplingStatsList.get(i).getValues())));
				else
					valueUncertainties.add(new ValueUncertainty(vu));
			}

			// uncertaintiesOverTime.put(timeStamp, new
			// ValueUncertaintyDistribution(deviations));

			timeStamps.add(timeStamp);
			valueUncertaintiesAllDimensionsOverTime.add(valueUncertainties);
		}

		timeSeriesValueUncertainty = new TimeSeriesMultivariateValueUncertaintyCalculationResult(
				new UncertaintyMultivariateTimeSeries(timeStamps, valueUncertaintiesAllDimensionsOverTime), true);

		return timeSeriesValueUncertainty;
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

	public boolean isCalculateDistributionUncertainty() {
		return calculateDistributionUncertainty;
	}

	public void setCalculateDistributionUncertainty(boolean calculateDistributionUncertainty) {
		this.calculateDistributionUncertainty = calculateDistributionUncertainty;
	}

}