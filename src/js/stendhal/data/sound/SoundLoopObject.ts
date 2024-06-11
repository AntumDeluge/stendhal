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

import { SoundLayer } from "./SoundLayer";
import { SoundObject } from "./SoundObject";


/**
 * Wrapper class for an `HTMLAudioElement` that should loop.
 */
export class SoundLoopObject extends SoundObject {

	/**
	 * Creates a new looping sound object.
	 *
	 *
	 * @param {SoundLayer} layer
	 *   Channel this sound plays on.
	 * @param {string} id
	 *   Name/Identifier.
	 * @param {string} source
	 *   Audio source filename.
	 * @param {number} volume
	 *   Base volume.
	 */
	constructor(layer: SoundLayer, id: string, source: string, volume: number);

	/**
	 * Creates a new looping sound object.
	 *
	 * @param {SoundLayer} layer
	 *   Channel this sound plays on.
	 * @param {string} source
	 *   Audio source filename.
	 * @param {number} volume
	 *   Base volume.
	 */
	constructor(layer: SoundLayer, source: string, volume: number);

	// implementation constructor
	constructor(layer: SoundLayer, p1: string, p2: string|number, p3?: number) {
		if (typeof(p2) === "string") {
			super(layer, p1, p2 as string, p3!);
		} else {
			super(layer, p1, p2 as number);
		}
		this.audio.loop = true;

		// event listeners
		this.audio.onended = () => {
			this.onEnded(new Event("loopsound"));
		};
	}

	/*
	override protected onEnded(evt: Event) {
		// NOTE: if there are gaps between loops lets try restarting it manually
		//this.audio.currentTime = 0;
		//this.play();
	}
	*/
}
