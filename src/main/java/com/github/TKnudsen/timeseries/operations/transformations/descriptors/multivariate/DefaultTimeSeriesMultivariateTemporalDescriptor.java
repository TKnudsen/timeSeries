package com.github.TKnudsen.timeseries.operations.transformations.descriptors.multivariate;

import java.util.ArrayList;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeature;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataTransformationCategory;
import com.github.TKnudsen.timeseries.data.multivariate.ITimeSeriesMultivariate;

/**
 * <p>
 * Title: DefaultTimeSeriesMultivariateTemporalDescriptor
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
 * @version 1.02
 */
public class DefaultTimeSeriesMultivariateTemporalDescriptor extends DefaultTimeSeriesMultivariateDescriptor {

	private long kernelTime = 100L;

	/**
	 * for serialization/reflection purposes
	 */
	private DefaultTimeSeriesMultivariateTemporalDescriptor() {
		this(100L);
	}

	public DefaultTimeSeriesMultivariateTemporalDescriptor(long kernelTime) {
		this.kernelTime = kernelTime;
	}

	@Override
	protected List<NumericalFeature> getFeaturesForIndex(ITimeSeriesMultivariate timeSeriesMultivariate,
			List<String> attributeNames, int index) {

		// identify indieces of time series minus/plus the kernelTime
		Long currentTimeStamp = timeSeriesMultivariate.getTimestamp(index);

		int indexMinor = index;
		for (int i = index; i > -1; i--)
			if (timeSeriesMultivariate.getTimestamp(i) >= currentTimeStamp - kernelTime)
				indexMinor = i;

		int indexMajor = index;
		for (int i = index; i < timeSeriesMultivariate.size(); i++)
			if (timeSeriesMultivariate.getTimestamp(i) <= currentTimeStamp + kernelTime)
				indexMajor = i;

		List<NumericalFeature> features = new ArrayList<>();

		// add features for former time stamp
		List<Double> values = timeSeriesMultivariate.getValue(indexMinor);
		for (int d = 0; d < values.size(); d++) {
			NumericalFeature feature = new NumericalFeature(attributeNames.get(d) + "-t", values.get(d));
			features.add(feature);
		}

		// current time stamp
		values = timeSeriesMultivariate.getValue(index);
		for (int d = 0; d < values.size(); d++) {
			NumericalFeature feature = new NumericalFeature(attributeNames.get(d), values.get(d));
			features.add(feature);
		}

		// add features for future time stamp
		values = timeSeriesMultivariate.getValue(indexMajor);
		for (int d = 0; d < values.size(); d++) {
			NumericalFeature feature = new NumericalFeature(attributeNames.get(d) + "+t", values.get(d));
			features.add(feature);
		}

		return features;
	}

	@Override
	public DataTransformationCategory getDataTransformationCategory() {
		return DataTransformationCategory.DESCRIPTOR;
	}

	@Override
	public String getDescription() {
		return "Transforms the multivariate value domain of every timestamp into a NumericalFeatureVector. Adds former and later timestamps depending on the kernel to covert the temporal domain.";
	}

	@Override
	public String getName() {
		return "DefaultTimeSeriesMultivariateTemporalDescriptor";
	}

	public long getKernelTime() {
		return kernelTime;
	}

	public void setKernelTime(long kernelTime) {
		this.kernelTime = kernelTime;
	}

}
