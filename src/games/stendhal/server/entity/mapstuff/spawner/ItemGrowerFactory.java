/***************************************************************************
 *                      (C) Copyright 2022 - Arianne                       *
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

import org.apache.log4j.Logger;

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;


public class ItemGrowerFactory implements ConfigurableFactory {

	private static Logger logger = Logger.getLogger(ItemGrowerFactory.class);


	@Override
	public Object create(final ConfigurableFactoryContext ctx) {
		final String itemName = ctx.getRequiredString("item");
		final int respawn = ctx.getRequiredInt("respawn");
		final String desc = ctx.getString("description", "");

		// DEBUG:
		System.out.println("\nCreating grower for \"" + itemName + "\"\n");

		if (!desc.equals("")) {
			return new ItemGrower(itemName, respawn, desc);
		} else {
			return new ItemGrower(itemName, respawn);
		}
	}
}
