package com.github.TKnudsen.timeseries.operations.preprocessing.univariate;

/**
 * Convenience specialization of {@link ValueRemover} that removes all
 * time-value pairs whose value is {@code Double.NaN} or {@code null}.
 *
 * <p>
 * Equivalent to {@code new ValueRemover(Double.NaN)} but communicates intent
 * more clearly at the call site.
 *
 * <p>
 * Copyright: Copyright (c) 2015-2026
 * </p>
 *
 * @author Juergen Bernard
 * @version 2.1 refactored to extend ValueRemover
 * @see ValueRemover
 */
public class MissingValueRemover extends ValueRemover {

	/**
	 * Creates a remover that treats {@code Double.NaN} as the missing value
	 * indicator.
	 */
	public MissingValueRemover() {
		super(Double.NaN);
	}

	public MissingValueRemover(Double value) {
		super(value);
	}
}