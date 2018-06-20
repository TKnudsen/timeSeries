package com.github.TKnudsen.timeseries.data;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.IUncertainty;

/**
 * <p>
 * Title: IUncertaintyAtTimeStamp
 * </p>
 * 
 * <p>
 * Description: interface for temporal uncertainty.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.04
 */
public interface IUncertaintyAtTimeStamp<U extends IUncertainty<?>> {

	public U getUncertainty(Long timeStamp);

	public U getUncertainty(int index);
}
