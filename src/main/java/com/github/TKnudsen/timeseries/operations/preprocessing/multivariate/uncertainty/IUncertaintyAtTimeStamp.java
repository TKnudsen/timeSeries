package com.github.TKnudsen.timeseries.operations.preprocessing.multivariate.uncertainty;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.IUncertainty;
import com.github.TKnudsen.timeseries.data.ITemporalUncertainty;

/**
 * <p>
 * Title: IUncertaintyAtTimeStamp
 * </p>
 * 
 * <p>
 * Description: Behavior for retrieving uncertainty information for given time
 * stamps.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016-2017 VISSECT,
 * http://www.gris.informatik.tu-darmstadt.de/projects/vissect/index.html
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.01
 */
public interface IUncertaintyAtTimeStamp<U extends IUncertainty<?>> extends ITemporalUncertainty<U> {

}
