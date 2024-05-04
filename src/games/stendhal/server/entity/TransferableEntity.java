/***************************************************************************
 *                    (C) Copyright 2003-2024 - Arianne                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;


/**
 * An entity that can change zones.
 */
public abstract class TransferableEntity extends DressedEntity {

	/**
	 * Creates a new entity that can change zones.
	 */
	public TransferableEntity() {
		super();
	}

	/**
	 * Creates a new entity that can change zones with attributes copied from {@code object}.
	 */
	public TransferableEntity(final RPObject object) {
		super(object);
	}

	/**
	 * Determine if zone changes are currently allowed via normal means (non-portal teleportation
	 * doesn't count).
	 *
	 * @return
	 *   {@code true} if the entity can change zones.
	 */
	@Override
	public boolean isZoneChangeAllowed() {
		return true;
	}

	/**
	 * Teleports entity to given destination.
	 *
	 * @param zone
	 *   Destination zone.
	 * @param x
	 *   X coordinate of destination zone.
	 * @param y
	 *   Y coordinate of destination zone.
	 * @param dir
	 *   Direction entity should face after teleport or {@code null} for no change.
	 * @param teleporter
	 *   Player who initiated the teleport or {@code null} if no player is responsible. This is only
	 *   to give feedback if something goes wrong. If no feedback is wanted use {@code null}.
	 * @return
	 *   {@code true} if teleport was successful.
	 */
	public boolean teleport(final StendhalRPZone zone, final int x, final int y, final Direction dir,
			final Player teleporter) {
		if (StendhalRPAction.placeat(zone, this, x, y)) {
			if (dir != null) {
				this.setDirection(dir);
			}
			notifyWorldAboutChanges();
			return true;
		} else {
			final String text = "Position [" + x + "," + y + "] is occupied";
			if (teleporter != null) {
				teleporter.sendPrivateText(text);
			} else {
				this.sendPrivateText(text);
			}
			return false;
		}
	}

	/**
	 * Teleports entity to given destination.
	 *
	 * @param id
	 *   Destination zone name/ID.
	 * @param x
	 *   X coordinate of destination zone.
	 * @param y
	 *   Y coordinate of destination zone.
	 * @param dir
	 *   Direction entity should face after teleport or {@code null} for no change.
	 * @param teleporter
	 *   Player who initiated the teleport or {@code null} if no player is responsible. This is only
	 *   to give feedback if something goes wrong. If no feedback is wanted use {@code null}.
	 * @return
	 *   {@code true} if teleport was successful.
	 */
	public boolean teleport(final String id, final int x, final int y, final Direction dir,
			final Player teleporter) {
		return teleport(SingletonRepository.getRPWorld().getZone(id), x, y, dir, teleporter);
	}
}
