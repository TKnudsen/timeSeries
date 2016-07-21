package com.github.TKnudsen.timeseries.operations.preprocessing;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.preprocessing.IDataProcessing;
import com.github.TKnudsen.timeseries.data.multivariate.TimeSeriesMultivariate;

/**
 * <p>
 * Title: PreprocessingRoutineMultivariate
 * </p>
 * 
 * <p>
 * Description: interface for preprocessing routines
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public interface IPreprocessingRoutineMultivariate extends IDataProcessing<TimeSeriesMultivariate> {

	public void process(List<TimeSeriesMultivariate> data);

}