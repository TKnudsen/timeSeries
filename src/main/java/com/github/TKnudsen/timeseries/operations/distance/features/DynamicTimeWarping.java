package com.github.TKnudsen.timeseries.operations.distance.features;

import com.github.TKnudsen.ComplexDataObject.data.features.numericalData.NumericalFeatureVector;
import com.github.TKnudsen.ComplexDataObject.model.distanceMeasure.IDistanceMeasure;

import de.javagl.nd.distance.DistanceFunction;
import de.javagl.nd.distance.tuples.d.DoubleTupleDistanceFunctions;
import de.javagl.nd.tuples.d.DoubleTuple;

/**
 * <p>
 * Title: DynamicTimeWarping
 * </p>
 * 
 * <p>
 * Description: provides the dynamic time warping (DTW) distance measure. Builds
 * up on the javagl DTW implementation.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017-2018
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.03
 */
public class DynamicTimeWarping implements IDistanceMeasure<NumericalFeatureVector> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6065943390224705989L;

	DistanceFunction<DoubleTuple> dtw = DoubleTupleDistanceFunctions.dynamicTimeWarping();

	@Override
	public double getDistance(NumericalFeatureVector o1, NumericalFeatureVector o2) {
		return dtw.distance(o1, o2);
	}

	@Override
	public String getName() {
		return "Dynamic Time Warping";
	}

	@Override
	public String getDescription() {
		return "DTW is a cross-bin comparison method for the calculation of distances. It is more dynamic but slower than ED.";
	}

	@Override
	public double applyAsDouble(NumericalFeatureVector t, NumericalFeatureVector u) {
		return getDistance(t, u);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof DynamicTimeWarping))
			return false;
		return true;
	}
}
