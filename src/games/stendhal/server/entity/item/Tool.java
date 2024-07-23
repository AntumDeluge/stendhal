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
package games.stendhal.server.entity.item;

import java.util.Map;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.mapstuff.ToolActivatedEntity;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;

/**
 * Item class representing a tool.
 *
 * TODO:
 *   - maybe merge functions into AreaUseItem
 */
public class Tool extends AreaUseItem {

	/**
	 * Creates a new tool.
	 *
	 * @param name
	 *   Item name.
	 * @param clazz
	 *   Class (or type) of item.
	 * @param subclass
	 *   Subclass of item.
	 * @param attributes
	 *   Attributes, such as attack. May be empty or {@code null}.
	 */
	public Tool(String name, String clazz, String subclass, Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
		//setRPClass("tool");
	}

	/**
	 * Copy constructor.
	 *
	 * @param item
	 *   Item to be copied.
	 */
	public Tool(Tool item) {
		super(item);
		//setRPClass("tool");
	}

	/**
	 * Generates {@code RPClass} and initializes attributes.
	 */
	/*
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass("tool");
		rpclass.isA("item");
	}
	*/

	/**
	 * Gets player using this item.
	 *
	 * @return
	 *   Player who has this equipped.
	 */
	public Player getWielder() {
		final RPObject container = getBaseContainer();
		if (!(container instanceof Player)) {
			return null;
		}
		return (Player) container;
	}

	public Entity findTarget() {
		return null;
	}

	@Override
	protected boolean onUsedInArea(final RPEntity user, final StendhalRPZone zone, final int x,
			final int y) {
		for (Entity e: zone.getEntitiesAt(x, y)) {
			if (e instanceof ToolActivatedEntity) {
				((ToolActivatedEntity) e).use(this);
				// ignore other entities
				return true;
			}
		}

		if (user instanceof Player) {
			user.sendPrivateText("You didn't find anything.");
		}
		return true;
	}
}
