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
package games.stendhal.server.entity.mapstuff;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Tool;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Pair;


/**
 * An entity that is activated by using an item on it.
 *
 * TODO:
 *   - hide from "look" action
 */
public abstract class ToolActivatedEntity extends Entity {

	private static Logger logger = Logger.getLogger(ToolActivatedEntity.class);

	/** Name of tool that can be used to acquire item. */
	protected String toolName;
	/** Level requirement for using. */
	private Pair<Integer, Integer> levelsRange;

	/** Private messages to player on dig attempt. */
	private static Map<String, String> messages = new HashMap<String, String>() {{
		put("approved", null); // use was approved
		put("denied", null); // use was denied
		put("awarded", null); // player received award
		put("awarded_dropped", null); // player received item award but dropped it
		put("unavailable", null); // reward unavailable
		put("error", "Something appears to be broken.");
	}};


	/**
	 * Creates new entity.
	 *
	 * @param toolName
	 *  Name of tool that can be used to acquire item.
	 */
	public ToolActivatedEntity(String toolName) {
		setResistance(0);
		setVisibility(0);
		this.toolName = toolName;
		setLevels(0, -1);
	}

	/**
	 * Creates a new entity with default tool (shovel).
	 */
	public ToolActivatedEntity() {
		this("shovel");
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;
	}

	/**
	 * Retrieves name of tool required to use entity.
	 *
	 * @return
	 *   Tool item name.
	 */
	public String getToolName() {
		return toolName;
	}

	/**
	 * Sets player level requirements for using the entity.
	 *
	 * @param min
	 *   Minimum required level.
	 * @param max
	 *   Maximum allowed level.
	 */
	public void setLevels(int min, int max) {
		assertThat(max < 0 || max >= min, is(true));
		levelsRange = new Pair<>(min, max);
	}

	/**
	 * Sets a private message text sent to player on dig attempt.
	 *
	 * Supported types are: "unavailable"
	 *
	 * @param type
	 *   Message type.
	 * @param msg
	 *   Message text.
	 */
	public void setMessage(String type, String msg) {
		if (!messages.containsKey(type)) {
			logger.warn("Unsupported message type: " + type);
			return;
		}
		messages.put(type, msg);
	}

	/**
	 * Retrieves message text.
	 *
	 * @param type
	 *   Message type.
	 * @return
	 *   Message text or {@code null}.
	 */
	protected String getMessage(String type) {
		if (!messages.containsKey(type)) {
			logger.warn("Unsupported message type: " + type);
			return null;
		}
		return messages.get(type);
	}

	/**
	 * Sends a private message to player.
	 *
	 * @param player
	 *   Player receiving message.
	 * @param type
	 *   Message type.
	 */
	protected void sendUseMessage(Player player, String type) {
		String msg = messages.get(type);
		if (msg == null) {
			return;
		}
		player.sendPrivateText(msg);
	}

	/**
	 * Checks if player meets level requirements to use entity.
	 *
	 * @param player
	 *   Player requesting to use.
	 * @return
	 *   {@code true} if player's level is within min/max range.
	 */
	private boolean canUse(Player player) {
		int level = player.getLevel();
		if (level < levelsRange.first()) {
			return false;
		}
		int max = levelsRange.second();
		if (max < 0) {
			return true;
		}
		return level <= max;
	}

	/**
	 * Attempts to use entity.
	 *
	 * @param player
	 *   Player attempting to use.
	 * @param tool
	 *   Item being used on this.
	 * @return
	 *   {@code true} if used successfully.
	 */
	protected boolean useInternal(Player player, Tool tool) {
		if (!canUse(player)) {
			// player does not meet level requirements
			return false;
		}
		if (!(toolName.equals(tool.getName()))) {
			// item does not work with this
			return false;
		}
		return true;
	}

	/**
	 * Attempts to use entity.
	 *
	 * @param tool
	 *   Item being used on this.
	 */
	public boolean use(Tool tool) {
		// DEBUG:
		System.out.println("Using tool activated entity");

		final Player player = tool.getWielder();
		if (player == null) {
			// must be used by a player
			return false;
		}
		boolean res = useInternal(player, tool);
		if (res) {
			onUsed(player);
		}
		return res;
	}

	/**
	 * Called when player successfully uses entity with required tool.
	 *
	 * @param player
	 *   Player using entity.
	 */
	abstract protected void onUsed(Player player);
}
