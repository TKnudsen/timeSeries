package com.github.TKnudsen.timeseries.operations;

import java.util.List;

import com.github.TKnudsen.timeseries.operations.preprocessing.IPreprocessingRoutineUnivariate;

/**
 * <p>
 * Title: ParameterGuidance
 * </p>
 * 
 * <p>
 * Description: An interface allowing PreprocessingRoutines to suggest
 * alternative parameteritzations.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011-2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */

public interface IParameterGuidance {
	public List<? extends IPreprocessingRoutineUnivariate> getAlternativeParameterizations(int maxNumber);

}
