package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.ParameterSupportTools;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.ITimeValuePair;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesTools;

/**
 * <p>
 * Title: PerceptualImportantPoints
 * </p>
 * 
 * <p>
 * Description: Perceptual important points preprocessing routine wich can be
 * used for data reduction purposes. The timeseries to be processed is
 * subsequently reduced to the size of a given pipCount. The criterion of
 * reduction is based on the preservation of perceived points (min/max values).
 * 
 * The implementation of the algorithm is in accordance to the publication
 * 
 * Chung, F.L., Fu, T.C., Luk, R., Ng, V., Flexible Time Series Pattern Matching
 * Based on Perceptually Important Points. In: Workshop on Learning from
 * Temporal and Spatial Data at IJCAI (2001) 1-7
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.05
 */
public class PerceptuallyImportantPoints implements ITimeSeriesUnivariatePreprocessor {

	private int pipCount;

	public PerceptuallyImportantPoints(int pipCount) {
		if (pipCount < 2)
			throw new IllegalArgumentException("PIP: parameter value <2");

		this.pipCount = pipCount;
	}

	public int getPipCount() {
		return pipCount;
	}

	public void setPipCount(int pipCount) {
		if (pipCount < 2)
			throw new IllegalArgumentException("PIP: parameter value <2");

		this.pipCount = pipCount;
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.DATA_REDUCTION;
	}

	@Override
	public void process(List<ITimeSeriesUnivariate> data) {
		for (ITimeSeriesUnivariate timeSeries : data)
			process(timeSeries);
	}

	public void process(ITimeSeriesUnivariate data) {
		if (data == null)
			return;
		if (data.size() < pipCount)
			return;

		List<ITimeValuePair<Double>> pipTmp = new ArrayList<>();
		pipTmp.add(TimeSeriesTools.getTimeValuePair(data, 0));
		pipTmp.add(TimeSeriesTools.getTimeValuePair(data, data.size() - 1));

		List<ITimeValuePair<Double>> subSequence = new ArrayList<ITimeValuePair<Double>>(getPipCount());

		double dist = Double.NaN;
		double pipYOffsetCurrent = Double.NEGATIVE_INFINITY;
		int pipIterator = 1;
		int nextPipIndex = 0;

		for (int index = 0; index < getPipCount() - 2; index++) {
			pipIterator = 1;
			nextPipIndex = -1;
			pipYOffsetCurrent = Double.NEGATIVE_INFINITY;
			for (int i = 1; i < data.size(); i++) {
				if (data.getTimestamp(i) == pipTmp.get(pipIterator).getTimestamp()) {
					double steigung = (pipTmp.get(pipIterator).getValue() - pipTmp.get(pipIterator - 1).getValue()) / (pipTmp.get(pipIterator).getTimestamp() - pipTmp.get(pipIterator - 1).getTimestamp());
					double xAxisIntercept = pipTmp.get(pipIterator).getValue() - (pipTmp.get(pipIterator).getTimestamp() * steigung);
					for (int sub = 0; sub < subSequence.size(); sub++) {
						dist = Math.abs(steigung * subSequence.get(sub).getTimestamp() + xAxisIntercept - subSequence.get(sub).getValue());
						if (dist > pipYOffsetCurrent) {
							pipYOffsetCurrent = dist;
							nextPipIndex = i - subSequence.size() + sub;
						}
					}
					subSequence.clear();
					pipIterator++;
				} else {
					subSequence.add(TimeSeriesTools.getTimeValuePair(data, i));
				}
			}
			//// TODO: nextPipIndex == -1
			if (nextPipIndex >= data.size() || nextPipIndex < 0)
				continue;
			//////
			pipTmp.add(TimeSeriesTools.getTimeValuePair(data, nextPipIndex));
			Collections.sort(pipTmp);
		}

		// remove all data not matching the pipTmp result
		for (int i = 0; i < pipTmp.size(); i++) {
			while (pipTmp.get(i).getTimestamp() != data.getTimestamp(i))
				data.removeTimeValue(i);
		}
	}

	@Override
	public List<IDataProcessor<ITimeSeriesUnivariate>> getAlternativeParameterizations(int count) {
		List<Integer> integers = ParameterSupportTools.getAlternativeIntegers(pipCount, count);

		List<IDataProcessor<ITimeSeriesUnivariate>> processors = new ArrayList<>();
		for (Integer i : integers)
			processors.add(new PerceptuallyImportantPoints(i));

		return processors;
	}
}
