package com.github.TKnudsen.timeseries.data;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.IUncertainty;

/**
 * <p>
 * Title: ITemporalUncertainty
 * </p>
 * 
 * <p>
 * Description: interface for temporal uncertainty.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public interface ITemporalUncertainty<U extends IUncertainty<?>> {

	public U getUncertainty(Long timeStamp);

	public U getUncertainty(int index);
}
