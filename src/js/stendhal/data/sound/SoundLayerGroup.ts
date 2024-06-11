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
import { SoundUtil } from "./SoundUtil";

declare var stendhal: any;


/**
 * Represents all sounds playing on a single layer.
 */
export class SoundLayerGroup extends Array<SoundObject> {

	/** Layer volume to apply to sounds. */
	private volume: number;


	/**
	 * Creates a new layer group.
	 *
	 * @param {SoundLayer} layer
	 *   Layer ID.
	 */
	constructor(layer: SoundLayer) {
		super();
		const lname = layer.value;
		const volume = stendhal.config.getInt("sound." + lname + ".volume");
		if (typeof(volume) === "undefined") {
			console.error("Unsupported layer \"" + lname + "\"");
			this.volume = 100;
		} else {
			this.volume = SoundUtil.normVolume(volume);
		}
	}

	/**
	 * Retrieves layer group volume.
	 *
	 * @returns {number}
	 *   Volume level between 0-100.
	 */
	getVolume(): number {
		return this.volume;
	}

	/**
	 * Sets layer group volume.
	 *
	 * @param {number} volume
	 *   Volume level between 0-100.
	 */
	setVolume(volume: number) {
		this.volume = SoundUtil.normVolume(volume);
	}

	/**
	 * Removes a sound from this channel.
	 *
	 * @param {SoundObject} snd
	 *   Sound to be removed.
	 * @returns {boolean}
	 *   `true` if sound object not found in channel.
	 */
	remove(snd: SoundObject): boolean {
		// make sure sound is no longer playing
		snd.stop();
		const idx = this.indexOf(snd);
		if (idx > -1) {
			this.splice(this.indexOf(snd), 1);
		}
		return this.indexOf(snd) < 0;
	}

	stop() {
		for (const sound of [...this]) {
			sound.stop();
		}
	}

	pause() {
		for (const sound of this) {
			sound.pause();
		}
	}

	resume() {
		for (const sound of this) {
			sound.play();
		}
	}

	mute() {
		for (const sound of this) {
			sound.mute();
		}
	}

	unmute() {
		for (const sound of this) {
			sound.unmute();
		}
	}
}
