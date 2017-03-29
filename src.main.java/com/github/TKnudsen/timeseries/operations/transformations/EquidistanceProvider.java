package com.github.TKnudsen.timeseries.operations.transformations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.processors.complexDataObject.DataTransformationCategory;
import com.github.TKnudsen.ComplexDataObject.model.transformations.IDataTransformation;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;
import com.github.TKnudsen.timeseries.data.univariate.TimeSeriesUnivariate;

/**
 * <p>
 * Title: EquidistanceProvider
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public class EquidistanceProvider implements IDataTransformation<ITimeSeriesUnivariate, ITimeSeriesUnivariate> {

	private TimeDuration equidistance;
	private ValueAscertainType type;

	public EquidistanceProvider(TimeDuration equidistance, ValueAscertainType type) {
		this.equidistance = equidistance;
		this.type = type;
	}

	@Override
	public List<ITimeSeriesUnivariate> transform(ITimeSeriesUnivariate input) {

		List<ITimeSeriesUnivariate> resultList = new ArrayList<>();

		List<Long> timeStamps = new ArrayList<>();
		List<Double> values = new ArrayList<>();
		long timeDuaration = equidistance.getDuration();
		long currentTimeStamp = 0;

		while (input.getLastTimestamp() > currentTimeStamp) {
			timeStamps.add(currentTimeStamp);

			Double value = null;

			if (ValueAscertainType.LAST_EXISTING == type)
				value = workOutValueLastExisting(input, currentTimeStamp);
			if (ValueAscertainType.LINEAR_INTERPOLATION == type)
				value = workOutValueLinearInterpolation(input, currentTimeStamp);

			values.add(value);

			currentTimeStamp += timeDuaration;
		}

		if (ValueAscertainType.LAST_EXISTING == type) {
			values.add(input.getValue(input.getLastTimestamp(), false));
			timeStamps.add(currentTimeStamp);
		}

		ITimeSeriesUnivariate timeSeries = new TimeSeriesUnivariate(input.getID(), timeStamps, values, input.getMissingValueIndicator());

		timeSeries.setDescription(input.getDescription());
		timeSeries.setName(input.getName());
		Iterator<String> iterator = input.getTypes().keySet().iterator();
		while (iterator.hasNext()) {
			String attribute = iterator.next();
			timeSeries.add(attribute, input.getAttribute(attribute));
		}

		resultList.add(timeSeries);
		return resultList;
	}

	@Override
	public List<ITimeSeriesUnivariate> transform(List<ITimeSeriesUnivariate> inputObjects) {
		List<ITimeSeriesUnivariate> resultList = new ArrayList<>();

		for (ITimeSeriesUnivariate ts : inputObjects) {
			resultList.add(transform(ts).get(0));
		}
		return resultList;
	}

	private Double workOutValueLastExisting(ITimeSeriesUnivariate input, long timeStamp) {
		return input.getValue(input.findByDate(timeStamp, false));
	}

	private Double workOutValueLinearInterpolation(ITimeSeriesUnivariate input, long timeStamp) {
		return input.getValue(timeStamp, true);
	}

	@Override
	public DataTransformationCategory getDataTransformationCategory() {
		// TODO may be changed to data cleaning in future.
		return DataTransformationCategory.DESCRIPTOR;
	}

	public enum ValueAscertainType {
		LINEAR_INTERPOLATION, LAST_EXISTING
	}
}