/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.StringUtils;


public abstract class RepeatableCollectorQuest extends AbstractQuest {

	/**
	 * Checks the player's quest slot for a currently requested item.
	 *
	 * @param player
	 * 		Player to check.
	 * @return
	 * 		Currently requested item if quest is active, otherwise <code>null</code>.
	 */
	public String getRequestedItem(final Player player) {
		final String requested = player.getRequiredItemName(getSlotName(), 0);
		if (requested.equals("")) {
			return null;
		}

		return requested;
	}

	/**
	 * Retrieves item to request from player.
	 *
	 * If player is aborting quest, will blacklist previously requested item.
	 *
	 * @param player
	 * 		Player
	 * @param itemList
	 * 		Map list to select from.
	 * @return
	 * 		Randomly selected item name.
	 */
	protected String getNewItem(final Player player, final Map<String, Integer> itemList) {
		String newItem = Rand.rand(itemList.keySet());

		// don't repeat same item
		if (newItem != null && itemList.size() > 1 && newItem.equals(getRequestedItem(player))) {
			newItem = getNewItem(player, itemList);
		}

		return newItem;
	}

	/**
	 * Creates a ChatAction to set the player's quest slot info.
	 *
	 * @param npc
	 * 		The SpeakerNPC attending to player.
	 * @param itemList
	 * 		List of items to select from.
	 * @param message
	 * 		Message for NPC to say.
	 * @return
	 * 		New ChatAction.
	 */
	protected ChatAction startQuestAction(final SpeakerNPC npc, final Map<String, Integer> itemList, final String message) {
		// common place to get the start quest actions as we can both starts it and abort and start again

		final RPEntity attended = npc.getAttending();
		if (attended instanceof Player) {
			final Player player = (Player) attended;

			final String questSlot = getSlotName();
			final String requested = getNewItem(player, itemList);

			final ChatAction startRecordAction = new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					final int quantity = itemList.get(requested);

					Map<String, String> substitutes = new HashMap<String, String>();
					substitutes.put("item", Grammar.quantityplnoun(quantity, requested, "a"));
					substitutes.put("#item", Grammar.quantityplnounWithHash(quantity, requested));
					substitutes.put("the item", "the " + Grammar.plnoun(quantity, requested));

					raiser.say(StringUtils.substitute(message, substitutes));
					player.setQuest(questSlot, 0, requested + "=" + quantity);
				}
			};

			final List<ChatAction> actions = new LinkedList<ChatAction>();
			actions.add(startRecordAction);
			actions.add(new SetQuestToTimeStampAction(questSlot, 1));

			return new MultipleActions(actions);
		}

		return null;
	}
}
