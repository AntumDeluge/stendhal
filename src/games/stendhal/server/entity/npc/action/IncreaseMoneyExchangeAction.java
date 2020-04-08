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
package games.stendhal.server.entity.npc.action;

import java.util.Objects;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;


/**
 * Action to adjust amount of money earned or spent by player.
 */
public class IncreaseMoneyExchangeAction implements ChatAction {

	private final int quantity;
	private final boolean spent;


	/**
	 * Creates an action to increase money earned or spent by specified amount.
	 *
	 * @param quantity
	 * 		Amount to be increased by.
	 * @param spent
	 * 		If <code>true</code>, increases value of spent money, otherwise increases
	 * 		value of earned money.
	 */
	public IncreaseMoneyExchangeAction(final int quantity, final boolean spent) {
		this.spent = spent;
		this.quantity = quantity;
	}

	/**
	 * Creates an action to increase money earned or spent by 1.
	 *
	 * @param spent
	 * 		If <code>true</code>, increases value of spent money, otherwise increases
	 * 		value of earned money.
	 */
	public IncreaseMoneyExchangeAction(final boolean spent) {
		this(1, spent);
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		if (spent) {
			player.incMoneySpent(quantity);
		} else {
			player.incMoneyEarned(quantity);
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("IncreaseMoneyExchangeAction [");
		if (spent) {
			sb.append("spent");
		} else {
			sb.append("earned");
		}
		sb.append("=" + quantity + "]");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(quantity, spent);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof IncreaseMoneyExchangeAction)) {
			return false;
		}

		final IncreaseMoneyExchangeAction other = (IncreaseMoneyExchangeAction) obj;
		return quantity == other.quantity && spent == other.spent;
	}
}
