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


/**
 * The class that stores the definition of a layer set up to
 * retrieve collision tileset GID.
 */
public class CollisionLayerDefinition extends LayerDefinition {

	public CollisionLayerDefinition(final int layerWidth, final int layerHeight,
			final int gid) {
		super(layerWidth, layerHeight);
	}

	/**
	 * Copy constructor.
	 */
	public CollisionLayerDefinition(final LayerDefinition other) {
		super(other.width, other.height);

		this.map = other.map;
		this.width = other.width;
		this.height = other.height;
		setName(other.getName());
		this.data = other.expose();
		this.raw = other.exposeRaw();
	}

	/**
	 * Retrieves the first GID of the collision tileset.
	 */
	public Integer getTilesetGid() {
		for (final TileSetDefinition def: this.map.getTilesets()) {
			final String filename = def.getSource();
			if (filename != null && (filename.equals("collision.png")
					|| filename.endsWith("collision.png"))) {
				return def.getFirstGid();
			}
		}

		return null;
	}
}
