/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
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

import games.stendhal.common.constants.CollisionType;
import games.stendhal.common.tiled.LayerDefinition;


/**
 * This class loads the map and allow you to determine if a player collides or
 * not with any of the non trespasable areas of the world.
 */
public class CollisionDetection {
	private CollisionMap map;

	private int width;
	private int height;

	private final boolean testserver = System.getProperty("stendhal.testserver") != null;


	/**
	 * Clear the collision map.
	 */
	public void clear() {
		if (map == null) {
			map = new CollisionMap(width, height);
		}
	}

	/**
	 * Initialize the collision map to desired size.
	 *
	 * @param width width of the map
	 * @param height height of the map
	 */
	public void init(final int width, final int height) {
		if (this.width != width || this.height != height) {
			map = null;
		} else if (map != null) {
			map.clear();
		}

		this.width = width;
		this.height = height;

		clear();
	}

	/**
	 * Set a position in the collision map to collision type.
	 *
	 * @param x
	 *     X coordinate.
	 * @param y
	 *     Y coordinate.
	 * @param t
	 *     Collision type.
	 */
	public void setCollide(final int x, final int y, final CollisionType t) {
		if ((x < 0) || (x >= width) || (y < 0) || (y >= height)) {
			return;
		}

		if (!testserver) {
			map.set(x, y);
		} else {
			map.set(x, y, t);
		}
	}

	/**
	 * Set a position in the collision map to static collision.
	 *
	 * @param x
	 *     X coordinate.
	 * @param y
	 *     Y coordinate.
	 */
	public void setCollide(final int x, final int y) {
		setCollide(x, y, CollisionType.NORMAL);
		/*
		if ((x < 0) || (x >= width) || (y < 0) || (y >= height)) {
			return;
		}
		map.set(x, y);
		*/
	}

	/**
	 * Fill the collision map from layer data.
	 *
	 * @param collisionLayer static collision information
	 */
	public void setCollisionData(final LayerDefinition collisionLayer) {
		// First we build the int array.
		collisionLayer.build();
		init(collisionLayer.getWidth(), collisionLayer.getHeight());

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				final int collisionType = collisionLayer.getTileAt(x, y);

				// DEBUG:
				//System.out.println("\nCollsion tile: " + collisionType);

				if (!testserver) {
					/*
					 * NOTE: Right now our collision detection system is binary, so
					 * something is blocked or is not.
					 */
					if (collisionType != 0) {
						map.set(x, y);
					}
				} else {
					// DEBUG:
					//System.out.println("Collision type: " + (byte) collisionType);
					/*
					if (collisionType > 0) {
						System.out.println("\nNon-standard collision: " + (collisionType + 1) + "\n");
					}
					*/

					if (collisionType > 0) {
						// DEBUG:
						/*
						if (collisionType > 1) {
							System.out.println("\nNon-standard collision: " + collisionType + "\n");
						}
						*/

						map.set(x, y, (byte) collisionType);
					}
				}
			}
		}
	}

	/**
	 * Print the area around the (x,y) useful for debugging.
	 *
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param size size of surroundings
	 */
	public void printaround(final int x, final int y, final int size) {
		for (int j = y - size; j < y + size; j++) {
			for (int i = x - size; i < x + size; i++) {
				if ((j >= 0) && (j < height) && (i >= 0) && (i < width)) {
					if ((j == y) && (i == x)) {
						System.out.print("O");
					} else if (map.get(i, j)) {
						System.out.print("X");
					} else {
						System.out.print(".");
					}
				}
			}
			System.out.println();
		}
	}

	/**
	 * Check if a rectangle is at least partially outside the map.
	 *
	 * @param shape area to be checked
	 * @return <code>true</code> if shape is at least partially outside the map,
	 * 	<code>false</code> otherwise
	 */
	public boolean leavesZone(final Rectangle2D shape) {
		final double x = shape.getX();
		final double y = shape.getY();
		final double w = shape.getWidth();
		final double h = shape.getHeight();

		return (x < 0) || (x + w > width) || (y < 0) || (y + h > height);
	}

	/**
	 * Get the width of the collision map.
	 *
	 * @return width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the height of the collision map.
	 *
	 * @return height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Check if a rectangle overlaps colliding areas.
	 *
	 * @param shape checked area
	 * @return <code>true</code> if the shape enters in any of the non
	 *	trespassable areas of the map, <code>false</code> otherwise
	 */
	public boolean collides(final Rectangle2D shape) {
		final double x = shape.getX();
		final double y = shape.getY();
		final double w = shape.getWidth();
		final double h = shape.getHeight();
		return collides(x, y, w, h);
	}

	/**
	 * Check if a rectangle overlaps colliding areas.
	 *
	 * @param x x-position
	 * @param y y-position
	 * @param w width
	 * @param h height
	 * @return <code>true</code> if the shape enters in any of the non
	 *	trespassable areas of the map, <code>false</code> otherwise
	 */
	public boolean collides(final double x, final double y, final double w, final double h) {
		/*
		 * CollisionMap does the same tests, but least tests use zones without
		 * collisions, so it's simplest to do them here too.
		 */
		if ((x < 0) || (x + w > width)) {
			return true;
		}
		if ((y < 0) || (y + h > height)) {
			return true;
		}

		int iHeight = (int) Math.ceil(Math.ceil(y + h) - y);
		int iWidth = (int) Math.ceil(Math.ceil(x + w) - x);
		return map.collides((int) x, (int) y, iWidth, iHeight);
	}

	/**
	 * Check if a location is marked with collision.
	 *
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return <code>true</code> if the map position is a collision tile,
	 * 	otherwise <code>false</code>
	 */
	public boolean collides(final int x, final int y) {
		if ((x < 0) || (x >= width)) {
			return true;
		}

		if ((y < 0) || (y >= height)) {
			return true;
		}

		if (!testserver) {
			return map.get(x, y);
		} else {
			return map.getCollision(x, y) > 0;
		}
	}

	/**
	 * Retrieves collision type for a node.
	 */
	public CollisionType getCollisionType(final int x, final int y) {
		return map.getCollisionType(x, y);
	}

	/**
	 * Checks if items can be placed on this node.
	 */
	public boolean canSetItemOn(final int x, final int y) {
		return !map.getCollisionType(x, y).equals(CollisionType.NORMAL);
	}

	/**
	 * Checks if projectiles can traverse a node.
	 *
	 * @param x
	 *     Node X coordinate.
	 * @param y
	 *     Node Y coordinate.
	 * @return
	 *     <code>true</code> if node collision does not interfere with projectiles.
	 */
	public boolean canShootOver(final int x, final int y) {
		if (!testserver) {
			return map.get(x, y);
		} else {
			return !map.getCollisionType(x, y).equals(CollisionType.NORMAL);
		}
	}

	/**
	 * Checks if projectiles can traverse an area of nodes.
	 *
	 * @param x
	 *     First node X coordinate.
	 * @param y
	 *     First node Y coordinate.
	 * @param w
	 *     Horizontal number of nodes in area.
	 * @param h
	 *     Vertical number of nodes in area.
	 * @return
	 *     <code>true</code> if nodes collision does not interfere with projectiles.
	 */
	public boolean canShootOver(final int x, final int y, final int w, final int h) {
		for (int ix = x; ix < x + w; ix++) {
			for (int iy = y; iy < y + h; iy++) {
				if (!canShootOver(ix, iy)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Checks if a flying entity can traverse a node.
	 *
	 * @param x
	 *     Node X coordinate.
	 * @param y
	 *     Node Y coordinate.
	 * @return
	 *     <code>true</code> if node collision does not interfere with flying entities.
	 */
	public boolean canFlyOver(final int x, final int y) {
		return canShootOver(x, y);
	}

	/**
	 * Checks if a flying entity can traverse an area of nodes.
	 *
	 * @param x
	 *     Node X coordinate.
	 * @param y
	 *     Node Y coordinate.
	 * @param w
	 *     Horizontal number of nodes in area.
	 * @param h
	 *     Vertical number of nodes in area.
	 * @return
	 *     <code>true</code> if nodes collision does not interfere with flying entities.
	 */
	public boolean canFlyOver(final int x, final int y, final int w, final int h) {
		return canShootOver(x, y, w, h);
	}
}
