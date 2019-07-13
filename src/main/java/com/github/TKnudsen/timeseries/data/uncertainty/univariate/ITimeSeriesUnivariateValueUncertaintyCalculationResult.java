package com.github.TKnudsen.timeseries.data.uncertainty.univariate;

import com.github.TKnudsen.ComplexDataObject.data.uncertainty.Double.IValueUncertainty;
import com.github.TKnudsen.timeseries.data.uncertainty.ITimeSeriesValueUncertaintyCalculationResult;

/**
 * The result of computing the {@link IValueUncertainty} for the elements of a
 * time series.<br>
 * <br>
 * This consists of a time series that stores the uncertainty for each time
 * stamp, as well as a time series that contains the values that these
 * uncertainties refer to. The time stamps of both of these time series may be
 * assumed to be equal.<br>
 * <br>
 * The uncertainties that are summarized with this result may be <i>absolute</i>
 * or <i>relative</i>, as indicated by {@link #isRelative()}. When the
 * uncertainties are relative, then an uncertainty of <code>u</code> for an
 * uncertain value <code>v</code> means that the actual value may be
 * <code>v + u * v</code>. When the uncertainties are absolute, then the actual
 * value may be <code>v + u</code>.
 * 
 * @param <V>
 */
public interface ITimeSeriesUnivariateValueUncertaintyCalculationResult extends ITimeSeriesValueUncertaintyCalculationResult<IValueUncertainty> {
}