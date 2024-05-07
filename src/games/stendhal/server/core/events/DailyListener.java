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
package games.stendhal.server.core.events;

import java.time.LocalDate;


/**
 * Listener for new day event.
 */
public interface DailyListener {

	/**
	 * Callback for new day event.
	 *
	 * Should not be called more than once per day.
	 *
	 * @param today
	 *   Date event occurred.
	 */
	void onNewDay(LocalDate today);
}
