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
package games.stendhal.common.tiled;

import static games.stendhal.common.constants.CollisionType.COLLISION_TYPES;


/**
 * The class that stores the definition of a layer with formatted
 * collision information.
 */
public CollisionLayerDefinition extends LayerDefinition {

	public CollisionLayerDefinition(final int layerWidth, final int layerHeight,
			final int gid) {
		super(layerWidth, layerHeight);
	}

	/**
	 * Copy constructor.
	 */
	public CollisionLayerDefinition(final LayerDefinition other) {
		this.map = other.map;
		this.width = other.width;
		this.height = other.height;
		this.name = other.name;
		this.data = other.data;
		this.raw = other.raw;
		this.collisionInfo = other.collisionInfo;
	}

	/**
	 * Initializes collision info if <code>null</code> & sets
	 * all collision to <code>0x00</code> (no collision).
	 */
	private void resetCollisionInfo() {
		if (collisionInfo == null) {
			collisionInfo = new Byte[width][height];
		}

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				collisionInfo[x][y] = 0x00;
			}
		}
	}

	/**
	 * Formats collision info using the tileset GID.
	 *
	 * @param offset
	 *     Tileset's first GID.
	 */
	public void setCollisionInfo(final int offset) {
		resetCollisionInfo();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				final Integer tileId = getTileAt(x, y);
				if (tileId != null) {
					byte collisionType = (byte) (tileId - offset + 1);

					// failsafe
					if (collisionType < 0x00) {
						// no collision
						collisionType = 0x00;
					} else if (collisionType > COLLISION_TYPES) {
						// normal collision
						collisionType = 0x01;
					}

					if (collisionType > 0x00) {
						collisionInfo[x][y] = collisionType;
					}
				}
			}
		}
	}

	/**
	 * Retrieves the first GID of the collision tileset.
	 */
	final Integer getTilesetGid() {
		Integer gid = null;

		for (final TileSetDefinition def: this.map) {
			final String filename = def.getSource();
			if (filename != null && (filename.equals("collision.png")
					|| filename.endsWith("collision.png"))) {
				gid = def.getFirstGid();
			}
		}

		return gid;
	}
}
