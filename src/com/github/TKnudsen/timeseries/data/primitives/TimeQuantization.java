package com.github.TKnudsen.timeseries.data.primitives;

import java.io.Serializable;

/**
 * <p>
 * Title: TimeQuantization
 * </p>
 * 
 * <p>
 * Description: enum that represents the different granularities of time.
 * According to the book of Aigner et al.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011-2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.11
 */
public enum TimeQuantization implements Serializable {

	MILLISECONDS("milliseconds"), SECONDS("seconds"), MINUTES("minutes"), HOURS("hours"), DAYS("days"), WEEKS("weeks"), MONTHS("months"), QUARTER("quarter"), YEARS("years"), DECADES("decades");

	private String name;

	private TimeQuantization(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}
}