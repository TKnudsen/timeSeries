package com.github.TKnudsen.timeseries.operations;

import java.util.List;

import com.github.TKnudsen.timeseries.operations.preprocessing.IPreprocessingRoutineUnivariate;

/**
 * <p>
 * Title: ParameterGuidance
 * </p>
 * 
 * <p>
 * Description: An interface which allows PreprocessingRoutine to suggest
 * alternative parameteritzations.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011-2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.0
 */

public interface IParameterGuidance {
	public List<? extends IPreprocessingRoutineUnivariate> getAlternativeParameterizations(int maxNumber);

}
