/***************************************************************************
 *                   (C) Copyright 2003-2022 - Marauroa                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.spawner;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;


public abstract class HarvestableEntityRespawnPoint extends Entity {

	public HarvestableEntityRespawnPoint() {
		super();
	}

	public HarvestableEntityRespawnPoint(final RPObject object) {
		super(object);
	}

	public abstract void onHarvested(final Item harvested);

	public abstract void onItemPickedUp(final Player player);
}
