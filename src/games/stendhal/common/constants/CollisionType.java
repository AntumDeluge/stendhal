/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common.constants;

import org.apache.log4j.Logger;

/**
 * Enumeration that defines different types of collision.
 */
public enum CollisionType {
	NONE(),
	HARD(),
	WALKBLOCK();

	/**
	 * Tracks number of collision types created.
	 */
	private static final class Counter {
		private static int count = 0;
	}

	/** Numeric value represented by this type. */
	private final byte byteValue;


	/**
	 * Creates a new collision type.
	 */
	private CollisionType() {
		byteValue = (byte) Counter.count;
		Counter.count++;
		if (Counter.count > Byte.MAX_VALUE + 1) {
			Logger.getLogger(CollisionType.class).warn("Value overflow (" + (Counter.count - 1)
					+ "(, more types than supported by byte. Results may be inaccurate.");
		}
	}

	/**
	 * Retrieves byte value representation.
	 *
	 * @return
	 *   Value corresponding to this type.
	 */
	public byte getValue() {
		return byteValue;
	}

	/**
	 * Retrieves CollisionType corresponding to byte value or CollisionType.NONE if no corresponding
	 * value found.
	 *
	 * @param value
	 *   Value to be checked.
	 * @return
	 *   Corresponding collision type or {@code CollisionType.NONE}.
	 */
	public static CollisionType fromValue(final int value) {
		if (Counter.count > Byte.MAX_VALUE + 1) {
			Logger.getLogger(CollisionType.class).warn("Value overflow (" + (Counter.count - 1)
					+ "(, more types than supported by byte. Results may be inaccurate.");
		}
		if (value < 0 || value >= Counter.count) {
			return CollisionType.NONE;
		}
		for (final CollisionType t: CollisionType.values()) {
			if (value == t.getValue()) {
				return t;
			}
		}
		// default to no collision
		return CollisionType.NONE;
	}

	/**
	 * Retrieves collision types.
	 *
	 * @return
	 *   Number of registered collision types.
	 */
	public static int count() {
		return Counter.count;
	}
}
