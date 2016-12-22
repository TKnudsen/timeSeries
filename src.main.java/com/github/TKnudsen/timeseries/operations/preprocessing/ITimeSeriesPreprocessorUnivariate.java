package com.github.TKnudsen.timeseries.operations.preprocessing;

import java.util.List;

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
 * @version 1.03
 */
public interface ITimeSeriesPreprocessorUnivariate extends ITimeSeriesPreprocessing<ITimeSeriesUnivariate> {

	public void process(List<ITimeSeriesUnivariate> data);

}
