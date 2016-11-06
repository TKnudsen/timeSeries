package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.preprocessing.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.ITimeValuePair;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.operations.preprocessing.ITimeSeriesPreprocessorUnivariate;
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
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public class PerceptualImportantPoints implements ITimeSeriesPreprocessorUnivariate {

	private int pipCount;

	public PerceptualImportantPoints(int pipCount) {
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

		double dist = Double.NaN;
		double pipYOffsetCurrent = Double.NEGATIVE_INFINITY;
		List<ITimeValuePair<Double>> subSequence = new ArrayList<ITimeValuePair<Double>>(getPipCount());
		int iterator = 1;
		int nextPipIndex = 0;

		for (int index = 0; index < getPipCount() - 2; index++) {
			iterator = 1;
			nextPipIndex = -1;
			pipYOffsetCurrent = Double.NEGATIVE_INFINITY;
			for (int i = 1; i < data.size(); i++) {
				if (data.getTimestamp(i) == pipTmp.get(iterator).getTimestamp()) {
					double steigung = (pipTmp.get(iterator).getValue() - pipTmp.get(iterator - 1).getValue()) / (pipTmp.get(iterator).getTimestamp() - pipTmp.get(iterator - 1).getTimestamp());
					double xAxisIntercept = pipTmp.get(iterator).getValue() - (pipTmp.get(iterator).getTimestamp() * steigung);
					for (int sub = 0; sub < subSequence.size(); sub++) {
						dist = Math.abs(steigung * subSequence.get(sub).getTimestamp() + xAxisIntercept - subSequence.get(sub).getValue());
						if (dist > pipYOffsetCurrent) {
							pipYOffsetCurrent = dist;
							nextPipIndex = i - subSequence.size() + sub;
						}
					}
					subSequence.clear();
					iterator++;
				} else {
					subSequence.add(TimeSeriesTools.getTimeValuePair(data, i));
				}
			}
			pipTmp.add(TimeSeriesTools.getTimeValuePair(data, nextPipIndex));
			Collections.sort(pipTmp);
		}

		// remove all data not matching the pipTmp result
		for (int i = 0; i < pipTmp.size(); i++) {
			while (pipTmp.get(i).getTimestamp() != data.getTimestamp(i))
				data.removeTimeValue(i);
		}
	}
}
