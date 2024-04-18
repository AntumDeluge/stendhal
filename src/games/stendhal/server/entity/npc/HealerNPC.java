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
package games.stendhal.server.entity.npc;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.status.PoisonStatus;

/**
 * NPC that services player HP.
 */
public class HealerNPC extends AttributeServicerNPC {

	/* The lowest possible price for a level 0 player. */
	private static Integer MIN_OFFER;


	/**
	 * Creates a new healer NPC.
	 *
	 * @param name
	 *   The NPC's name (please note that names should be unique).
	 */
	public HealerNPC(final String name) {
		super(name, "hp");
	}

	@Override
	public double fullService(final Player player) {
		final double unitsServiced = super.fullService(player);
		// healing also cures poison
		player.getStatusList().removeAll(PoisonStatus.class);
		return unitsServiced;
	}

	/**
	 * Restores player's HP to full value and cures poison.
	 *
	 * @param player
	 *   Player to be healed.
	 * @return
	 *   Amount of HP restored.
	 */
	public int heal(final Player player) {
		return (int) fullService(player);
	}

	/**
	 * This is for registering a `games.stendhal.server.entity.npc.behaviour.impl.HealerBehaviour`
	 * with `games.stendhal.server.entity.npc.behaviour.journal.ServicersRegister`.
	 *
	 * @return
	 *   The lowest possible price for a level 0 player.
	 */
	public int getMinOffer() {
		if (HealerNPC.MIN_OFFER == null) {
			// create a temporary level 0 player
			final Player dummyPlayer = Player.createZeroLevelPlayer("_dummy_", null);
			dummyPlayer.setHP(dummyPlayer.getHP()-1);
			HealerNPC.MIN_OFFER = calculateOffer(dummyPlayer);
		}
		return HealerNPC.MIN_OFFER;
	}
}
