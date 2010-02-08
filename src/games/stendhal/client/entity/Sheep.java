/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.entity;

import games.stendhal.client.sound.SoundSystemFacade;
import games.stendhal.common.Rand;
import games.stendhal.common.constants.SoundLayer;

/** A Sheep entity. */
public class Sheep extends DomesticAnimal {
	//
	// DomesticAnimal
	//

	@Override
	protected void probableChat(final int chance) {

		final String[][] soundnames = { { "sheep-1.wav", "sheep-3.wav" },
				{ "sheep-2.wav", "sheep-4.wav" } };
		final int which = Rand.rand(2);
		if (Rand.rand(100) < chance) {
			final String token;
			if (getWeight() > 50) {
				token = soundnames[0][which];
			} else {
				token = soundnames[1][which];
			}
			SoundSystemFacade.get().play(token, x, y, SoundLayer.CREATURE_NOISE, 100); 
		}
	}
}
