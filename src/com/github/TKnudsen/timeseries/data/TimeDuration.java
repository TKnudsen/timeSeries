package com.github.TKnudsen.timeseries.data;

/**
 * <p>
 * Title: TimeDuration
 * </p>
 * 
 * <p>
 * Description: Used to define an abstract time interval. Examples are 3 years,
 * 2 months, 17 days, etc.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2010-2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.21
 */

public class TimeDuration implements Cloneable {

	private TimeQuantization type;
	private long typeFactor;

	public TimeDuration(TimeQuantization type, long typeFactor) {
		this.type = type;
		this.typeFactor = typeFactor;
	}

	public void setType(TimeQuantization type) {
		this.type = type;
	}

	public TimeQuantization getType() {
		return type;
	}

	public void setTypeFactor(long typeFactor) {
		this.typeFactor = typeFactor;
	}

	public long getTypeFactor() {
		return typeFactor;
	}

	public boolean equals(TimeDuration e) {
		if (e.getType().equals(this.type) && e.getTypeFactor() == this.typeFactor)
			return true;
		return false;
	}

	public long getDuration() {
		long duration = getTypeFactor();
		if (getType().equals(TimeQuantization.MILLISECONDS)) {
		} else {
			duration *= 1000;
			if (getType().equals(TimeQuantization.SECONDS)) {
			} else {
				duration *= 60;
				if (getType().equals(TimeQuantization.MINUTES)) {
				} else {
					duration *= 60;
					if (getType().equals(TimeQuantization.HOURS)) {
					} else {
						duration *= 24;
						if (getType().equals(TimeQuantization.DAYS)) {
						} else if (getType().equals(TimeQuantization.WEEKS)) {
							duration *= 7;
						} else if (getType().equals(TimeQuantization.MONTHS)) {
							duration *= 30;
						} else if (getType().equals(TimeQuantization.QUARTER)) {
							duration *= 120;
						} else if (getType().equals(TimeQuantization.YEARS)) {
							duration *= 365;
						} else if (getType().equals(TimeQuantization.DECADES)) {
							duration *= 3650;
						}
					}
				}
			}
		}
		return duration;
	}

	public TimeDuration clone() {
		return new TimeDuration(this.type, this.typeFactor);
	}

	public String toString() {
		if (getType().toString().endsWith("s"))
			return getTypeFactor() + " " + getType();
		else
			return getTypeFactor() + " " + getType() + "s";
	}
}
