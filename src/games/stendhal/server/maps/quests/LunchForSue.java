/***************************************************************************
 *                       Copyright © 2023 - Stendhal                       *
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

import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.MathHelper;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;


/**
 * This simply reads the quest slot so it can be shown in the travel log. The actual quest code is
 * in `games.stendhal.server.maps.kalavan.citygardens.GardenerNPC`.
 */
public class LunchForSue extends AbstractQuest {

	private static final String QUEST_SLOT = "sue_swap_kalavan_city_scroll";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getName() {
		return "LunchForSue";
	}

	@Override
	public String getNPCName() {
		return "Sue";
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> history = new LinkedList<>();

		final String state1 = player.getQuest(QUEST_SLOT, 0);
		final String state2 = player.getQuest(QUEST_SLOT, 1);
		if (state1 != null) {
			final String npcName = getNPCName();
			history.add(npcName + " will <em>swap</em> a Kalavan city scroll with me for every cup of tea"
					+ " and sandwich I bring her.");
			final int lunch_count = getLunchCount(player);
			String msg;
			if ("done".equals(state1)) {
				msg = "I gave " + npcName + " some lunch and got ";
				if (lunch_count > 1) {
					msg += lunch_count + " scrolls ";
				} else {
					msg += "a scroll ";
				}
				msg += "in return.";
			} else {
				msg = npcName + " told me to return in less than " + lunch_count + " "
						+ Grammar.plnoun(lunch_count, "minute") + " to get my "
						+ Grammar.plnoun(lunch_count, "scroll") + ".";
			}
			history.add(msg);
		}

		return history;
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
			"Lunch for Sue",
			getNPCName() + ", the hungry gardener in Kalavan city, swaps magic scrolls for lunch.",
			true);
	}

	/**
	 * Retrieves number of lunches being swapped.
	 */
	private int getLunchCount(final Player player) {
		final String state1 = player.getQuest(QUEST_SLOT, 0);
		if ("done".equals(state1)) {
			return MathHelper.parseInt(player.getQuest(QUEST_SLOT, 1));
		}
		return MathHelper.parseInt(state1);
	}

	@Override
	public boolean isRepeatable(final Player player) {
		if (!isCompleted(player)) {
			return false;
		}
		return new TimePassedCondition(QUEST_SLOT, 2, MathHelper.MINUTES_IN_ONE_DAY
				* getLunchCount(player)).fire(player, null, null);
	}
}
