package com.github.TKnudsen.timeseries.operations.preprocessing.features;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeatureVector;
import com.github.TKnudsen.ComplexDataObject.data.interfaces.IDObject;
import com.github.TKnudsen.ComplexDataObject.model.processors.IDataProcessor;
import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataProcessingCategory;
import com.github.TKnudsen.timeseries.data.ITemporalLabeling;
import com.github.TKnudsen.timeseries.operations.tools.TimeSeriesLabelingTools;

/**
 * <p>
 * Title: TimeSeriesFeatureVectorLabelingProcessor
 * </p>
 *
 * <p>
 * Description:
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2016-2018
 * </p>
 *
 * @author Juergen Bernard
 * @version 1.02
 * 
 */
public class TimeSeriesFeatureVectorLabelingProcessor implements IDataProcessor<NumericalFeatureVector> {

	private String classLabelName = "class";
	private String timeStampAttribute = "StartTime";

	public TimeSeriesFeatureVectorLabelingProcessor(String classLabelName) {
		this.classLabelName = classLabelName;
	}

	public TimeSeriesFeatureVectorLabelingProcessor(String classLabelName, String timeStampAttribute) {
		this.classLabelName = classLabelName;
		this.timeStampAttribute = timeStampAttribute;
	}

	@Override
	public void process(List<NumericalFeatureVector> data) {
		for (NumericalFeatureVector fv : data) {
			if (fv.getMaster() == null)
				fv.add(classLabelName, "noClass");
			IDObject master = fv.getMaster();
			if (master instanceof ITemporalLabeling<?>) {
				ITemporalLabeling<?> ts = (ITemporalLabeling<?>) master;
				Long l = getTimeStamp(ts, fv);
				String label = TimeSeriesLabelingTools.getLabel(ts, l).toString();

				if (label == null)
					fv.add(classLabelName, "noClass");
				else
					fv.add(classLabelName, label);
			} else
				fv.add(classLabelName, "noClass");
		}

	}

	private Long getTimeStamp(ITemporalLabeling<?> ts, NumericalFeatureVector fv) {
		if (fv == null || ts == null)
			return null;

		if (fv.getAttribute(timeStampAttribute) == null)
			return null;

		try {
			Long l = (Long) fv.getAttribute(timeStampAttribute);
			return l;
		} catch (Exception e) {

		}

		return null;
	}

	@Override
	public DataProcessingCategory getPreprocessingCategory() {
		return DataProcessingCategory.SECONDARY_DATA_PROVIDER;
	}

	public String getClassLabelName() {
		return classLabelName;
	}

	public void setClassLabelName(String classLabelName) {
		this.classLabelName = classLabelName;
	}

	public String getTimeStampAttribute() {
		return timeStampAttribute;
	}

	public void setTimeStampAttribute(String timeStampAttribute) {
		this.timeStampAttribute = timeStampAttribute;
	}

}
