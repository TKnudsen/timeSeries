package com.github.TKnudsen.timeseries.util;

import java.util.UUID;

/**
 * <p>
 * Title: RandomTools
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * @author Juergen Bernard
 * @version 1.0
 */

public class RandomTools {
	/**
	 * Little helper for the generation of a unique identifier.
	 * 
	 * @return unique ID
	 */
	public static long getRandomLong() {
		return UUID.randomUUID().getMostSignificantBits();
	}
}
