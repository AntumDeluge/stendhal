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

import { SoundLoopObject } from "./SoundLoopObject";


/**
 * Wrapper class for an `HTMLAudioElement` that should loop.
 *
 * NOTE: should not fade in/out when transitioning between loops
 */
export class MusicObject extends SoundLoopObject {

	/** Rate at which music should fade in/out (TODO: adjust). */
	private static readonly FADE_RADE = 0.5;


	override play() {
		// TODO: fade-in
		super.play();
	}

	override stop() {
		// TODO: fade-out
		super.stop();
	}
}
