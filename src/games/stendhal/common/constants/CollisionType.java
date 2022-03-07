/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
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


/**
 * Enumeration that defines different types of collision.
 */
public enum CollisionType {
	NONE((byte) 0x00),
	NORMAL((byte) 0x01),
	WALKBLOCK((byte) 0x02),
	FLYOVER((byte) 0x03);

	private final byte byteValue;

	public static final int COLLISION_TYPES = 3;


	private CollisionType(final byte bv) {
		byteValue = bv;
	}

	/**
	 * Retrieves byte value of this type.
	 */
	public byte getValue() {
		return byteValue;
	}

	/**
	 * Retrieves CollisionType corresponding to byte value
	 * or CollisionType.NONE if no corresponding value found.
	 *
	 * @param b
	 *     Byte value to check for.
	 * @return
	 *     Corresponding CollsionType or NONE.
	 */
	public static CollisionType fromByte(final byte b) {
		for (final CollisionType t: CollisionType.values()) {
			if (b == t.getValue()) {
				return t;
			}
		}

		// default to no collision
		return NONE;
	}
}
