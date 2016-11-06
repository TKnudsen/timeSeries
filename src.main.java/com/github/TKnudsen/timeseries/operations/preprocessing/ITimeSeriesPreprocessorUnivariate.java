package com.github.TKnudsen.timeseries.operations.preprocessing;

import java.util.List;

import com.github.TKnudsen.ComplexDataObject.model.preprocessing.IDataProcessing;
import com.github.TKnudsen.timeseries.data.univariate.ITimeSeriesUnivariate;

/**
 * <p>
 * Title: PreprocessingRoutineUnivariate
 * </p>
 * 
 * <p>
 * Description: interface for preprocessing routines
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015-2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public interface ITimeSeriesPreprocessorUnivariate extends IDataProcessing<ITimeSeriesUnivariate> {

	public void process(List<ITimeSeriesUnivariate> data);

}
