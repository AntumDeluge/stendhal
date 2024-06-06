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

import games.stendhal.server.entity.Entity;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;


/**
 * A simple generic entity representing an effect for client to draw.
 *
 * Other than name, to identify the entity, and foreground/background info, drawing instructions
 * should be handled client-side.
 */
public class PassiveEffect extends Entity {

	public static void generateGateRPClass() {
		final RPClass rpclass = new RPClass("passive_effect");
		rpclass.isA("entity");

		rpclass.addAttribute("name", Type.STRING);
		rpclass.addAttribute("foreground", Type.FLAG);
	}

	public PassiveEffect(final String name) {
		put("name", name);
	}
}
