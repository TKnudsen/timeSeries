package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.uncertainty.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.TreeMap;

import org.apache.commons.math3.analysis.function.Sqrt;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.NumericalUncertainty;
import com.github.TKnudsen.ComplexDataObject.model.tools.StatisticsSupport;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;

import smile.math.distance.MahalanobisDistance;
import weka.core.matrix.Matrix;
import weka.estimators.MahalanobisEstimator;

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
 * @author Juergen Bernard
 * @version 1.01
 */
public class RelativeValueDomainModificationMeasure
		extends TimeSeriesProcessingUncertaintyMeasure<ITimeSeriesMultivariate, NumericalUncertainty> {

	private static Double epsilon = 0.000000001;
	private Double samplingRate = 0.4;

	public RelativeValueDomainModificationMeasure(ITimeSeriesMultivariate originalTS,
			ITimeSeriesMultivariate processedTimeSeries, Double samplingRate) {
		this(originalTS, processedTimeSeries);
		this.samplingRate = samplingRate;
	}

	public RelativeValueDomainModificationMeasure(ITimeSeriesMultivariate originalTS,
			ITimeSeriesMultivariate processedTimeSeries) {
		super(originalTS, processedTimeSeries);
	}

	public RelativeValueDomainModificationMeasure() {
		super();
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
	public void calculateUncertaintyMeasure() {
		uncertaintiesOverTime = new TreeMap<>();

		int maxSampleSize = getOriginalTimeSeries().getTimestamps().size();
		List<Double> sampleRelatives = new ArrayList<>();
		Random samplingGenerator = new Random();
		List<StatisticsSupport> samplingStatsList = new ArrayList<StatisticsSupport>();
		
		List<Double> originalValues;
		List<Double> modifiedValues;
		List<Double> relatives;
		
		for(int i = 0; i <= maxSampleSize*this.samplingRate; ++i) {
			int randIdx = samplingGenerator.nextInt(maxSampleSize);
			Long timeStamp = getOriginalTimeSeries().getTimestamp(randIdx);
			originalValues = getOriginalTimeSeries().getValue(timeStamp, false);
			modifiedValues = null;

			relatives = new ArrayList<>();
			try {
				modifiedValues = getProcessedTimeSeries().getValue(timeStamp, false);
			} catch (Exception e) {
				// System.err.println(
				// getName() + ": unable to retrieve time stamp of original time series in
				// modified time series");
				// uncertaintiesOverTime.put(timeStamp, new NumericalUncertainty(relatives));
				continue;
			}
			
			ListIterator<Double> modIt = modifiedValues.listIterator();
			ListIterator<Double> origIt = originalValues.listIterator();
			while (origIt.hasNext()) {
				int idx = origIt.nextIndex();
				
				StatisticsSupport stats = null;
				if(!samplingStatsList.isEmpty() && idx < samplingStatsList.size())
					stats = samplingStatsList.get(idx);
				if(stats == null) {
					stats = new StatisticsSupport(sampleRelatives);
					samplingStatsList.add(idx, stats);
				}
				
				Double origVal = origIt.next();
				Double modVal = modIt.next();
				
				if(modVal == null)
					stats.addValue(1.0);
				else 
					stats.addValue(getRelativeValue(origVal, modVal));
			}
		}
		
		for (Long timeStamp : getOriginalTimeSeries().getTimestamps()) {
			originalValues = getOriginalTimeSeries().getValue(timeStamp, false);
			modifiedValues = null;

			relatives = new ArrayList<>();
			try {
				modifiedValues = getProcessedTimeSeries().getValue(timeStamp, false);
			} catch (Exception e) {
				// System.err.println(
				// getName() + ": unable to retrieve time stamp of original time series in
				// modified time series");
				// uncertaintiesOverTime.put(timeStamp, new NumericalUncertainty(relatives));
				continue;
			}


			
//			// TODO please validate
			for (int i = 0; i < originalValues.size(); i++) {
				//we assume that our distribution is normally distributed
				StatisticsSupport currentStat = samplingStatsList.get(i);
				if(currentStat.getVariance() > 0) {
					NormalDistribution nd = new NormalDistribution(currentStat.getMean(), Math.sqrt(currentStat.getVariance()));
					
					Double originalValue = originalValues.get(i);
					Double modifiedValue = modifiedValues.get(i);
					if(modifiedValue == null)
						relatives.add(1.0);
					
					// relative difference
					Double relativeValue = getRelativeValue(originalValue, modifiedValue);
					// normalized relative difference
					Double normalizedRelDif = Math.abs(relativeValue - currentStat.getMean());
					//cumulative probability
					Double cumProb = nd.cumulativeProbability(normalizedRelDif);
					// subtracted by 0.5 (due to this being highest density around mean
					cumProb -= 0.5;
					// take absolute, to compensate for small variations around mean
					cumProb = Math.abs(cumProb);
					
					// absolute value of difference between cumulative prob. of relative difference, normalized by mean
					relatives.add(Math.abs(cumProb));

				} else {
					relatives.add(0.0);
				}
			}

			uncertaintiesOverTime.put(timeStamp, new NumericalUncertainty(relatives));
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