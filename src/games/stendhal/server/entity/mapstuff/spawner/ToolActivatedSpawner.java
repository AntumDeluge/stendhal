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
package games.stendhal.server.entity.mapstuff.spawner;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.awt.Point;

import org.apache.log4j.Logger;

import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.item.Tool;
import games.stendhal.server.entity.mapstuff.ToolActivatedEntity;
import games.stendhal.server.entity.npc.condition.PlayerCanEquipItemCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;
import marauroa.common.Pair;


/**
 * Spawns an item that can be acquired using a tool item.
 *
 * TODO:
 *   - only spawn on specified tiles
 *   - set up puppy to help find item
 */
public abstract class ToolActivatedSpawner extends ToolActivatedEntity implements TurnListener {

	private static Logger logger = Logger.getLogger(ToolActivatedSpawner.class);

	/** Item acquired from this entity. */
	private Item prototype;
	/** Min/Max possible number of item to receive. */
	private Pair<Integer, Integer> quantityRange;
	/** Average number of seconds for item to become available. */
	private int meanTime;
	/** Intermediary for getting random timer duration. */
	private Integer meanTurns = null;
	/** Denotes whether an item is available for obtaining. */
	private boolean available;


	/**
	 * Creates a new entity.
	 *
	 * Doesn't actually "spawn" an item into zone, but rather sets availability state and awards
	 * according to stipulations when used by a player.
	 *
	 * @param itemName
	 *   Name of item to be awarded.
	 * @param minQuantity
	 *   Minimum number of the item that can be awarded.
	 * @param maxQuantity
	 *   Maximum number of the item that can be awarded.
	 * @param meanTime
	 *   Average number of seconds for item to become available.
	 */
	public ToolActivatedSpawner(String itemName, int minQuantity, int maxQuantity, int meanTime) {
		super();
		setItem(itemName, minQuantity, maxQuantity);
		setMeanTime(meanTime);
		available = false;

		setMessage("awarded", "You found [item].");
		setMessage("awarded_dropped", "You found [item] but dropped [it/them] because you don't have"
				+ " room to carry it.");
		setMessage("unavailable", "You didn't find anything.");
	}

	/**
	 * Sets item obtainable from this entity.
	 *
	 * @param itemName
	 *   Name of item to be awarded.
	 * @param minQuantity
	 *   Minimum number of the item that can be awarded.
	 * @param maxQuantity
	 *   Maximum number of the item that can be awarded.
	 */
	public void setItem(String itemName, int minQuantity, int maxQuantity) {
		prototype = SingletonRepository.getEntityManager().getItem(itemName);
		assertThat(prototype, notNullValue());
		assertThat(minQuantity, greaterThan(0));
		assertThat(maxQuantity, greaterThanOrEqualTo(minQuantity));
		quantityRange = new Pair<>(minQuantity, maxQuantity);
	}

	/**
	 * Retrieves name of item awarded from this entity.
	 *
	 * @return
	 *   Item name.
	 */
	public String getItemName() {
		return prototype.getName();
	}

	/**
	 * Sets average time for availability refresh.
	 *
	 * @param seconds
	 *   Average number of seconds for item to become available.
	 */
	public void setMeanTime(int seconds) {
		meanTime = seconds;
	}

	/**
	 * Checks if a item is available to be acquired.
	 *
	 * @return
	 *   {@code true} if item is available for retrieving.
	 */
	public boolean isAvailable() {
		return available;
	}

	/**
	 * Sets availability for when player attempts to acquire.
	 *
	 * @return
	 *   {@code true} if item is available for retrieving.
	 */
	private boolean setAvailableInternal() {
		if (available) {
			logger.warn("Item is already available");
			return available;
		}
		final StendhalRPZone zone = getZone();
		if (zone == null) {
			logger.warn("Zone not found, cannot set availability");
			return available;
		}
		// find an appropriate position on map
		Point pos = zone.getRandomSpawnPosition(this);
		if (pos == null) {
			logger.warn("Unable to find suitable position in zone (" + zone + ") retrying in 5 minutes");
			startAvailableTimer(TimeUtil.SECONDS_IN_MINUTE * 5);
			return available;
		}
		// update position
		setPosition(pos.x, pos.y);
		// set state to available for retrieving
		available = true;

		// TODO: change to debug
		logger.info("Set tool activated spawner " + getItemName() + " available at " + zone.getName()
				+ " " + getX() + "," + getY());

		return available;
	}

	/**
	 * Sets availability for when player attempts to acquire.
	 */
	public void setAvailable() {
		if (setAvailableInternal()) {
			// ensure timer stopped
			TurnNotifier.get().dontNotify(this);
		}
	}

	/**
	 * Sets an exact time to refresh availability.
	 *
	 * @param seconds
	 *   Seconds to elapse before item made available.
	 */
	public void startAvailableTimer(int seconds) {
		if (available) {
			logger.warn("Item is still available, cannot start timer");
			return;
		}
		TurnNotifier.get().notifyInSeconds(seconds, this);

		// TODO: change to debug
		logger.info("Refresh timer set for " + TimeUtil.timeUntil(seconds));
	}

	/**
	 * Sets time to refresh availability using predefined average time.
	 */
	public void startAvailableTimer() {
		// FIXME: accurate method for getting random time without converting to turns?
		if (meanTurns == null) {
			meanTurns = TimeUtil.secondsToTurns(meanTime);
		}
		int turns = Rand.randGaussian(meanTurns, (int) (0.1 * meanTurns));
		int seconds =  TimeUtil.turnsToSeconds(turns);
		startAvailableTimer(seconds);
	}

	@Override
	public void onTurnReached(int currentTurn) {
		setAvailable();
	}

	@Override
	protected boolean useInternal(Player player, Tool tool) {
		if (!super.useInternal(player, tool)) {
			return false;
		}
		if (!available) {
			//sendUseMessage(player, "unavailable");
			return false;
		}
		return true;
	}

	@Override
	protected void onUsed(Player player) {
		if (awardItem(player) == null) {
			sendUseMessage(player, "error");
		}
	}

	/**
	 * Awards player with item.
	 *
	 * @param player
	 *   Player to receive item.
	 * @return
	 *   Received item.
	 */
	private Item awardItem(Player player) {
		// determine how many should be rewarded
		int quantity = Rand.randUniform(quantityRange.first(), quantityRange.second());
		Item item;
		if (quantity > 1) {
			StackableItem stackable = new StackableItem((StackableItem) prototype);
			stackable.setQuantity(quantity);
			item = stackable;
		} else {
			item = new Item(prototype);
		}

		String playerName = player.getName();
		int itemQuantity = item.getQuantity();
		String itemName = item.getName();
		String itemNameP = Grammar.plnoun(itemQuantity, itemName);
		if (itemQuantity != quantity) {
			logger.warn("Determined quantity " + quantity + " but player " + playerName
					+ " rewarded with only " + itemQuantity + " " + itemNameP);
		}

		// TODO: change to debug
		logger.info("Awarding " + itemQuantity + " " + itemNameP + " to " + playerName);

		String msgType = "awarded";
		if (!new PlayerCanEquipItemCondition(itemName).fire(player, null, null)) {
			msgType = "awarded_dropped";
		}
		player.equipOrPutOnGround(item);
		sendUseMessage(player, msgType, item);
		available = false;
		startAvailableTimer();
		return item;
	}

	/**
	 * Sends a private message to player.
	 *
	 * @param player
	 *   Player receiving message.
	 * @param type
	 *   Message type.
	 * @param item
	 *   Acquired item.
	 */
	private void sendUseMessage(Player player, String type, Item item) {
		String msg = getMessage(type);
		int itemQuantity = 0;
		String itemName = null;
		if (item != null) {
			itemQuantity = item.getQuantity();
			itemName = item.getName();
		}
		if (itemName != null) {
			msg = msg.replace("[item]", Grammar.quantityplnoun(itemQuantity, itemName))
					.replace("[it/them]", Grammar.itthem(itemQuantity));
		}
		player.sendPrivateText(msg);
	}

	@Override
	protected void sendUseMessage(Player player, String type) {
		sendUseMessage(player, type, null);
	}
}
