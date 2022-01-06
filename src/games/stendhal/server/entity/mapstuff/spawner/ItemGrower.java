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

import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;


public class ItemGrower extends HarvestableEntityRespawnPoint implements TurnListener {

	private static Logger logger = Logger.getLogger(ItemGrower.class);

	protected int meanTurnsForRegrow;
	private boolean harvestReady;
	private final String growingItemName;


	public ItemGrower(final RPObject object, final String growingItemName,
			final int meanTurnsForRegrow) {
		super(object);

		this.growingItemName = growingItemName;
		this.meanTurnsForRegrow = meanTurnsForRegrow;

		setRPClass("item_grower");
		put("type", "item_grower");

		setResistance(0);
	}

	public ItemGrower(final String growingItemName, final int meanTurnsForRegrow) {
		super();

		this.growingItemName = growingItemName;
		this.meanTurnsForRegrow = meanTurnsForRegrow;

		setRPClass("item_grower");
		put("type", "item_grower");

		setResistance(0);
	}

	public ItemGrower(final String growingItemName, final int meanTurnsForRegrow,
			final String description) {
		this(growingItemName, meanTurnsForRegrow);

		setDescription(description);
	}

	public static void generateRPClass() {
		final RPClass grower = new RPClass("item_grower");
		grower.isA("entity");
		grower.addAttribute("class", Type.STRING);
	}

	@Override
	public void onHarvested(final Item harvested) {
		harvestReady = false;
		if (harvested != null) {
			harvested.setPlantGrower(null);
		}
		SingletonRepository.getTurnNotifier().notifyInTurns(getRandomTurnsForRegrow(), this);
	}

	@Override
	public void onItemPickedUp(final Player player) {
		player.incHarvestedForItem(growingItemName, 1);
	}

	@Override
	public void onTurnReached(final int currentTurn) {
		growNewItem();
	}

	public void setToFullGrowth() {
		if (!harvestReady) {
			growNewItem();
		}
		// don't grow anything new until someone picks a fruit
		SingletonRepository.getTurnNotifier().dontNotify(this);
	}

	public void setStartState() {
		onHarvested(null);
	}

	protected int getRandomTurnsForRegrow() {
		return Rand.randGaussian(meanTurnsForRegrow, (int) (0.1 * meanTurnsForRegrow));
	}

	public void growNewItem() {
		if (!harvestReady) {
			logger.debug("Growing " + growingItemName);

			final StendhalRPWorld world = SingletonRepository.getRPWorld();
			final StendhalRPZone zone = world.getZone(getID().getZoneID());

			// create a new grown item
			final Item grownItem = SingletonRepository.getEntityManager().getItem(
					growingItemName);
			grownItem.setPlantGrower(this);
			grownItem.setPosition(getX(), getY());
			grownItem.setFromCorpse(true);

			// The item should not expire to avoid unnecessary loop of spawning
			// and expiring
			zone.add(grownItem, false);
			harvestReady = true;
		}
	}
}
