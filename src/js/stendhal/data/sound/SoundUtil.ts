/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/


/**
 * Misc. functions for audio manipulation.
 */
export namespace SoundUtil {

	/**
	 * Normalizes volume level.
	 *
	 * @param {number} volume
	 *   Input volume level.
	 * @returns {number}
	 *   Normalized volume value between 0-100.
	 */
	export function normVolume(volume: number): number {
		return Math.max(0, Math.min(100, volume));
	}
}
