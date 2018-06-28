/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
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



import java.awt.geom.Rectangle2D;
import java.util.BitSet;

import games.stendhal.common.tiled.LayerDefinition;

public class CollisionMap {

	private final int width;
	private final int height;
	private final BitSet[] colls;
	private final BitSet[] ranged_colls;
	private final BitSet[] table_colls;

	public CollisionMap(final int width, final int height) {
		this.width = width;
		this.height = height;
		colls = new BitSet[width];
		ranged_colls = new BitSet[width];
		table_colls = new BitSet[width];
		for (int i = 0; i < width; i++) {
			colls[i] = new BitSet();
			ranged_colls[i] = new BitSet();
			table_colls[i] = new BitSet();
		}

	}

	public CollisionMap(final LayerDefinition layer) {
		this(layer.getWidth(), layer.getHeight());
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if (layer.getTileAt(x, y) != 0) {
						set(x, y);
					}
				}
			}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean get(final int i, final int j) {
		return colls[i].get(j);
	}

	public final int getType(final int i, final int j) {
		if (get(i, j)) {
			if (ranged_colls[i].get(j)) {
				return 2;
			} else if (table_colls[i].get(j)) {
				return 3;
			}
		}

		return 1;
	}

	public void set(final int i, final int j) {

		colls[i].set(j);
	}

	/**
	 * Collision types:
	 *   1: standard
	 *   2: ranged allowed
	 *   3: table
	 *
	 * @param i
	 * @param j
	 * @param collisionType
	 */
	public void set(final int i, final int j, final int collisionType) {
		switch (collisionType) {
		case 1:
			set(i, j);
			break;
		case 2:
			ranged_colls[i].set(j);
		default:
			break;
		}
	}

	public boolean collides(final int x, final int y, final int width, final int height) {
		if (x < 0 || x - 1 + width >= this.width) {
			return true;
		}

		if (y < 0 || y - 1 + height >= this.height) {
			return true;
		}

		final BitSet result = new BitSet();
		for (int i = x; i < x + width; i++) {
			result.or(colls[i]);
		}

		return !result.get(y, y + height).isEmpty();
	}

	public void clear() {
		for (int i = 0; i < this.width; i++) {
			colls[i].clear();
			ranged_colls[i].clear();
			table_colls[i].clear();
		}

	}
	public static CollisionMap create(final LayerDefinition layer) {

		CollisionMap collissionMap = new CollisionMap(layer.getWidth(), layer
				.getHeight());
		for (int x = 0; x < layer.getWidth(); x++) {
			for (int y = 0; y < layer.getHeight(); y++) {
				if (layer.getTileAt(x, y) != 0) {
					collissionMap.set(x, y);
				}
			}
		}
		return collissionMap;
	}

	public void unset(final int i, final int k) {
		colls[i].clear(k);

	}

	public void set(final Rectangle2D shape) {
		int y = (int) shape.getY();
		for (int x = (int) shape.getX(); x < shape.getX() + shape.getWidth(); x++) {

			colls[x].set(y, (int) (y + shape.getHeight()));
		}

	}

}
