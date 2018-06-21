package com.github.TKnudsen.timeseries.operations.io.loader;

import java.util.function.Function;

import com.github.TKnudsen.timeseries.data.ITimeSeries;

/**
 * <p>
 * Title: TimeSeriesLoader
 * </p>
 * 
 * <p>
 * Description: loads a ITimeSeries<?> from file.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.02
 */
public interface TimeSeriesLoader<TS extends ITimeSeries<?>> extends Function<String, TS> {

}
