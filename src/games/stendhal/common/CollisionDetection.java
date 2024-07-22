/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2024 - Marauroa                    *
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

import org.apache.log4j.Logger;

import games.stendhal.common.constants.CollisionType;
import games.stendhal.common.tiled.LayerDefinition;

/**
 * This class loads the map and allow you to determine if a player collides or
 * not with any of the non trespasable areas of the world.
 */
public class CollisionDetection {

	private static final Logger logger = Logger.getLogger(CollisionDetection.class);

	private CollisionMap map;

	private int width;
	private int height;

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
	 * @param width
	 *   Width of the map.
	 * @param height
	 *   Height of the map.
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
	 *   X coordinate.
	 * @param y
	 *   Y coordinate.
	 * @param t
	 *   Collision type.
	 */
	public void setCollide(final int x, final int y, final CollisionType t) {
		if ((x < 0) || (x >= width) || (y < 0) || (y >= height)) {
			return;
		}
		//map.set(x, y);
		map.set(x, y, t);
	}

	/**
	 * Set a position in the collision map to static collision.
	 *
	 * @param x
	 *   X coordinate.
	 * @param y
	 *   Y coordinate.
	 */
	public void setCollide(final int x, final int y) {
		/*
		if ((x < 0) || (x >= width) || (y < 0) || (y >= height)) {
			return;
		}
		map.set(x, y);
		*/
		setCollide(x, y, CollisionType.NORMAL);
	}

	/**
	 * Fill the collision map from layer data.
	 *
	 * @param collisionLayer
	 *   Static collision information.
	 */
	public void setCollisionData(final LayerDefinition collisionLayer) {
		// First we build the int array.
		collisionLayer.build();
		init(collisionLayer.getWidth(), collisionLayer.getHeight());

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				/*
				 * NOTE: Right now our collision detection system is binary, so
				 * something is blocked or is not.
				 */
				/*
				if (collisionLayer.getTileAt(x, y) != 0) {
					map.set(x, y);
				*/
				final int tileId = collisionLayer.getTileAt(x, y);
				if (tileId != 0) {
					setCollide(x, y, CollisionType.fromValue((byte) tileId));
				}
			}
		}
	}

	/**
	 * Fill the collision map from layer data.
	 *
	 * @param collisionLayer
	 *   Static collision information.
	 * @param gid
	 *   Tileset GID offset.
	 */
	public void setCollisionData(final LayerDefinition collisionLayer, final Integer gid) {
		// First we build the int array.
		collisionLayer.build();
		init(collisionLayer.getWidth(), collisionLayer.getHeight());

		if (gid == null) {
			logger.debug("collision tileset not found, no collision data available");
			return;
		}

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				final int tileId = collisionLayer.getTileAt(x, y);

				byte collType = (byte) (tileId - gid + 1);
				if (collType < 0x00) {
					logger.debug("non-standard collision type: " + collType);

					// no collision
					collType = 0x00;
				} else if (collType > CollisionType.count()) {
					logger.debug("non-standard collision type: " + collType);

					// default to normal collision
					collType = 0x01;
				}

				if (collType > 0x00) {
					map.set(x, y, collType);
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
	 * @param shape
	 *   Area to be checked.
	 * @return
	 *   {@code true} if shape is at least partially outside the map, {@code false} otherwise.
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
	 * @return
	 *   Map width.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the height of the collision map.
	 *
	 * @return
	 *   Map height.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Check if a rectangle overlaps colliding areas.
	 *
	 * @param shape
	 *   Checked Area
	 * @return
	 *   {@codetrue} if the shape enters in any of the non-trespassable areas of the map,
	 *   {@code false} otherwise.
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
	 * @param x
	 *   Rectangle X position.
	 * @param y
	 *   Rectangle Y position.
	 * @param w
	 *   Rectangle width.
	 * @param h
	 *   Rectangle height.
	 * @return
	 *   {@code true} if the shape enters in any of the non-trespassable areas of the map,
	 *   {@code false} otherwise.
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
	 * @param x
	 *   X coordinate.
	 * @param y
	 *   Y coordinate.
	 * @return
	 *   {@code true} if the map position is a collision tile, otherwise {@code false}.
	 */
	public boolean collides(final int x, final int y) {
		if ((x < 0) || (x >= width)) {
			return true;
		}
		if ((y < 0) || (y >= height)) {
			return true;
		}
		//return map.get(x, y);
		return map.getCollision(x, y) > 0;
	}

	/**
	 * Retrieves collision type for a node.
	 */
	public CollisionType getCollisionType(final int x, final int y) {
		return map.getCollisionType(x, y);
	}

	/**
	 * Checks if items can be placed on this node.
	 *
	 * @param x
	 *   Node X coordinate.
	 * @param y
	 *   Node Y coordinate.
	 * @return
	 *   {@code true} if hard collision is not detected at position.
	 */
	public boolean canSetItemOn(final int x, final int y) {
		return !map.getCollisionType(x, y).equals(CollisionType.NORMAL);
	}

	/**
	 * Checks if projectiles can traverse a node.
	 *
	 * @param x
	 *   Node X coordinate.
	 * @param y
	 *   Node Y coordinate.
	 * @return
	 *   {@code true} if node collision does not interfere with projectiles.
	 */
	public boolean canShootOver(final int x, final int y) {
		//return map.get(x, y);
		return canSetItemOn(x, y);
	}

	/**
	 * Checks if projectiles can traverse an area of nodes.
	 *
	 * @param x
	 *   First node X coordinate.
	 * @param y
	 *   First node Y coordinate.
	 * @param w
	 *   Horizontal number of nodes in area.
	 * @param h
	 *   Vertical number of nodes in area.
	 * @return
	 *   {@code true} if nodes collision does not interfere with projectiles.
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
	 *   Node X coordinate.
	 * @param y
	 *   Node Y coordinate.
	 * @return
	 *   {@code true} if node collision does not interfere with flying entities.
	 */
	public boolean canFlyOver(final int x, final int y) {
		return canShootOver(x, y);
	}

	/**
	 * Checks if a flying entity can traverse an area of nodes.
	 *
	 * @param x
	 *   Node X coordinate.
	 * @param y
	 *   Node Y coordinate.
	 * @param w
	 *   Horizontal number of nodes in area.
	 * @param h
	 *   Vertical number of nodes in area.
	 * @return
	 *   {@code true} if nodes collision does not interfere with flying entities.
	 */
	public boolean canFlyOver(final int x, final int y, final int w, final int h) {
		return canShootOver(x, y, w, h);
	}

	/**
	 * Checks if a flying entity can traverse an area of nodes.
	 *
	 * @param shape
	 *   Entity position and dimensions.
	 * @return
	 *   {@code true} if nodes collision does not interfere with flying entities.
	 */
	public boolean canFlyOver(Rectangle2D shape) {
		return canFlyOver((int) shape.getX(), (int) shape.getY(), (int) shape.getWidth(),
				(int) shape.getHeight());
	}
}
