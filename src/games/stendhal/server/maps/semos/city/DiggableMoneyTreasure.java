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
package games.stendhal.server.maps.semos.city;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.spawner.ToolActivatedSpawner;
import games.stendhal.server.util.TimeUtil;


/**
 * Small amount of money that new players can hunt for using puppy.
 */
public class DiggableMoneyTreasure implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		zone.add(buildDiggableTreasure());
	}

	private Entity buildDiggableTreasure() {
		ToolActivatedSpawner entity = new DiggableMoneyEntity();
		SingletonRepository.getCachedActionManager().register(new Runnable() {
			@Override
			public void run() {
				entity.startAvailableTimer();
				// DEBUG:
				//entity.setAvailable();
			}
		});
		return entity;
	}


	private static final class DiggableMoneyEntity extends ToolActivatedSpawner {

		public DiggableMoneyEntity() {
			super("money", 10, 100, TimeUtil.SECONDS_IN_DAY / 2);
			setLevels(0, 50);
		}
	}
}
