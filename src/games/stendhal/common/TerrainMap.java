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
package games.stendhal.common;

import org.apache.log4j.Logger;


public class TerrainMap {

	private static Logger logger = Logger.getLogger(TerrainMap.class);

	/** Terrain mapping. */
	private TerrainType[][] terrain;


	/**
	 * Initializes terrain map dimensions.
	 *
	 * @param width
	 *   Zone width.
	 * @param height
	 *   Zone height.
	 */
	public void init(int width, int height) {
		terrain = new TerrainType[width][height];
	}

	/**
	 * Sets terrain type at a given position.
	 *
	 * @param type
	 *   Terrain type.
	 * @param x
	 *   X zone position.
	 * @param y
	 *   Y zone position.
	 */
	public void set(TerrainType type, int x, int y) {
		if (terrain == null) {
			logger.warn("Terrain map not initialized");
			return;
		}
		terrain[x][y] = type;
	}

	/**
	 * Retrieves terrain type at a given position.
	 *
	 * @param x
	 *   X zone position.
	 * @param y
	 *   Y zone position.
	 * @return
	 *   Terrain type.
	 */
	public TerrainType get(int x, int y) {
		if (terrain == null) {
			return TerrainType.NONE;
		}
		TerrainType type = terrain[x][y];
		if (type == null) {
			return TerrainType.NONE;
		}
		return type;
	}
}
