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

import static com.google.common.base.Preconditions.checkNotNull;

import games.stendhal.server.entity.npc.behaviour.AttributeServicerCalc;
import games.stendhal.server.entity.player.Player;


/**
 * NPC that can service player attributes, such as healing HP.
 */
public abstract class AttributeServicerNPC extends SpeakerNPC implements AttributeServicerCalc {

	/** Attribute the NPC services. */
	private final String serviceAttribute;
	/** Base attribute used to determine number of units to be restored. */
	private final String serviceBaseAttribute;
	/** Price of a single attribute unit. */
	private int unitBasePrice = 1;
	/** Factor to adjust price per unit based on player level. */
	private double levelFactor = 0.01;
	/** Calculated price for service NPC is offering. */
	private int currentOffer;


	/**
	 * Creates a new attribute servicer NPC.
	 *
	 * @param name
	 *   The NPC's name (please note that names should be unique).
	 * @param attr
	 *   Attribute the NPC services.
	 * @param baseAttr
	 *   Base attribute used to determine number of units to be restored.
	 */
	public AttributeServicerNPC(final String name, final String attr, final String baseAttr) {
		super(name);
		serviceAttribute = checkNotNull(attr);
		serviceBaseAttribute = checkNotNull(baseAttr);
		resetOffer();
		checkNotNull(currentOffer);
	}

	/**
	 * Creates a new attribute servicer NPC.
	 *
	 * @param name
	 *   The NPC's name (please note that names should be unique).
	 * @param attr
	 *   Attribute the NPC services.
	 */
	public AttributeServicerNPC(final String name, final String attr) {
		this(name, attr, "base_" + attr);
	}

	@Override
	public String getServiceAttribute() {
		return serviceAttribute;
	}

	@Override
	public String getServiceBaseAttribute() {
		return serviceBaseAttribute;
	}

	/**
	 * Calculates price for service.
	 *
	 * Formula is: (unitPrice + ((levelFactor * level) / 100)) * (maxHP - HP)
	 *
	 * @param player
	 *   Player being charged.
	 */
	@Override
	public int calculateOffer(final Player player) {
		// apply unit price to number of units to be restored
		// TODO: check if pricing is fair & adjust
		return (int) Math.ceil(getUnitPrice(player) * getUnitsToRestore(player));
	}

	@Override
	public void setOffer(final int offer) {
		currentOffer = offer;
	}

	@Override
	public int getOffer() {
		return currentOffer;
	}

	/**
	 * Sets price per unit.
	 *
	 * @param price
	 *   Price of a single attribute unit.
	 */
	public void setUnitBasePrice(final int price) {
		unitBasePrice = price;
	}

	/**
	 * Sets level adjustment factor.
	 *
	 * @param factor
	 *   Factor to adjust price based on player level.
	 */
	public void setLevelPriceFactor(final double factor) {
		levelFactor = factor / 100;
	}

	/**
	 * Retrieves level adjustment factor.
	 *
	 * @return
	 *   Factor to adjust price based on player level.
	 */
	public double getLevelFactor() {
		return levelFactor * 100;
	}

	/**
	 * Retrieves price per unit.
	 *
	 * @param player
	 *   Player who is being serviced.
	 * @return
	 *   Single unit price factored by player level.
	 */
	protected double getUnitPrice(final Player player) {
		// base unit price must be at least 1
		final int unitPrice = unitBasePrice > 0 ? unitBasePrice : 1;
		// factor in player level
		return unitPrice + ((levelFactor * player.getLevel()) / 100);
	}
}
