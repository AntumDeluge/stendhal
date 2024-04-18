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
package games.stendhal.server.entity.npc.behaviour;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.player.Player;


/**
 * Manages calculating price for NPCs that restore attributes, such as healers.
 *
 * TODO: Add support for or replace with new `games.stendhal.server.entity.npc.behaviour.impl.prices.PriceCalculationStrategy` class.
 */
public abstract interface AttributeServicerCalc {

	/**
	 * Retrieves attribute that the NPC services.
	 *
	 * @return
	 *   Attribute the NPC services.
	 */
	String getServiceAttribute();

	/**
	 * Retrieves base attribute that the NPC services.
	 *
	 * @return
	 *   Base attribute used to determine number of units to be restored.
	 */
	String getServiceBaseAttribute();

	/**
	 * Calculates price for service.
	 *
	 * @param player
	 *   Player being charged.
	 */
	int calculateOffer(Player player);

	/**
	 * Sets current offer price.
	 *
	 * @param offer
	 *   Price for service.
	 */
	void setOffer(int offer);

	/**
	 * Retrieves current offer price.
	 *
	 * @return
	 *   Price for service.
	 */
	int getOffer();

	/**
	 * Resets current offer to 0.
	 */
	default void resetOffer() {
		setOffer(0);
	}

	/**
	 * Checks if offer changed from initial calculation.
	 *
	 * @param newOffer
	 *   Most recent price calculation.
	 * @return
	 *   `true` if calculation matches original.
	 */
	default boolean offerChanged(final int newOffer) {
		return newOffer != getOffer();
	}

	/**
	 * Retrieves the number of units that will be restored.
	 *
	 * Double data type is used to support all numeric types.
	 *
	 * @param player
	 *   Player who is being serviced.
	 * @return
	 *   Number of units to be restored.
	 */
	default double getUnitsToRestore(final Player player) {
		return Math.max(player.getDouble(getServiceBaseAttribute()) - player.getDouble(getServiceAttribute()), 0);
	}

	/**
	 * Restores player's attribute to full value.
	 *
	 * @param player
	 *   Player whose attribute is to be updated.
	 * @return
	 *   Number of serviced units.
	 */
	default double fullService(final Player player) {
		final String serviceAttribute = getServiceAttribute();
		final double unitsMissing = getUnitsToRestore(player);
		final String valueTarget = player.get(getServiceBaseAttribute());
		try {
			if (unitsMissing > 0 && Double.parseDouble(valueTarget) == Double.parseDouble(player.get(serviceAttribute)) + unitsMissing) {
				player.put(serviceAttribute, valueTarget);
				resetOffer();
				return unitsMissing;
			}
		} catch (final NumberFormatException e) {
			Logger.getLogger(AttributeServicerCalc.class).error(e);
		}
		resetOffer();
		return 0;
	}
}
