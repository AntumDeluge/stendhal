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
package games.stendhal.server.entity.pet.boroborel;

import games.stendhal.server.entity.ActiveEntity;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;


/**
 * Class for raising a boroborel pet.
 */
public class BoroborelPet extends ActiveEntity {

	public static void generateRPClass() {
		final RPClass rpclass = new RPClass("boborel");
		rpclass.isA("entity");

		rpclass.addAttribute("title", Type.STRING);
		rpclass.addAttribute("strength", Type.INT, Definition.PRIVATE);
		rpclass.addAttribute("vigor", Type.INT, Definition.PRIVATE);
		rpclass.addAttribute("stamina", Type.INT, Definition.PRIVATE);
		rpclass.addAttribute("intelligence", Type.INT, Definition.PRIVATE);
		rpclass.addAttribute("durability", Type.INT, Definition.PRIVATE);
		rpclass.addAttribute("temperament", Type.INT, Definition.PRIVATE);
		rpclass.addAttribute("luck", Type.INT, Definition.PRIVATE);
		rpclass.addAttribute("appearance", Type.STRING, Definition.PRIVATE);
	}

	/**
	 * Creates a new boroborel entity.
	 *
	 * @param title
	 *   Display name.
	 */
	public BoroborelPet(final String title) {
		put("title", title);
	}

	/**
	 * Copy constructor.
	 * @param pet
	 */
	public BoroborelPet(final BoroborelPet pet) {
		// TODO: copy attributes
	}
}
