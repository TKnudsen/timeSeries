package com.github.TKnudsen.timeseries.operations.tools;

/**
 * <p>
 * How to resolve a requested time-series crop boundary that doesn't land
 * exactly on an existing time stamp.
 * </p>
 *
 * @version 1.0
 * @since 2026
 */
public enum TemporalCropStrategy {
	NextOuter, NextInner, ExactInterpolation;
}
