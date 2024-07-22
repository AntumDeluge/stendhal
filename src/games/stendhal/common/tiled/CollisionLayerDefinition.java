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
package games.stendhal.common.tiled;

import games.stendhal.common.constants.CollisionType;

/**
 * Stores definition of a layer set up to retrieve collision tileset GID.
 */
public class CollisionLayerDefinition extends LayerDefinition {

	/** First GID of collision tileset. */
	private Integer gid = null;


	/**
	 * Copy constructor.
	 *
	 * @param other
	 *   Layer definition to be copied.
	 */
	public CollisionLayerDefinition(final LayerDefinition other) {
		super(other.width, other.height);

		setMap(other.map);
		this.width = other.width;
		this.height = other.height;
		setName(other.getName());
		this.data = other.expose();
		this.raw = other.exposeRaw();
	}

	@Override
	public void setMap(final StendhalMapStructure map) {
		super.setMap(map);
		// set collision tileset first GID
		for (final TileSetDefinition def: this.map.getTilesets()) {
			final String filename = def.getSource();
			if (filename != null && filename.endsWith("collision.png")) {
				gid = def.getFirstGid();
				break;
			}
		}
	}

	/**
	 * Retrieves collision tileset GID.
	 *
	 * @return
	 *   First tileset GID or {@code null}.
	 */
	public Integer getTilesetGid() {
		return gid;
	}

	/**
	 * Determines collision type using a tile GID.
	 *
	 * The collision tileset GID from this layer is used to determine if the supplied GID value is a
	 * collision tile.
	 *
	 * @param gid
	 *   Tile GID.
	 * @return
	 *   Collision type.
	 */
	public CollisionType getCollisionType(int gid) {
		final int offset = gid - this.gid;
		return CollisionType.fromValue(offset);
	}
}
