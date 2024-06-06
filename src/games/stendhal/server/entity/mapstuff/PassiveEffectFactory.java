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

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;


public class PassiveEffectFactory implements ConfigurableFactory {

	@Override
	public Object create(final ConfigurableFactoryContext ctx) {
		final PassiveEffect effect = new PassiveEffect(ctx.getRequiredString("name"));
		if (ctx.getBoolean("foreground", false)) {
			effect.put("foreground", "");
		}
		return effect;
	}
}
