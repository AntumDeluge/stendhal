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
import { MapMusicObject } from "./MapMusicObject";

import { Chat } from "../../util/Chat";

declare var stendhal: any;


/**
 * Represents all sounds playing on a single layer.
 */
export class SoundLayerGroup {

	/** Maximum number of sounds that can play simultaneously on a layer group. */
	private static readonly MAX = 25;

	/** Currently active sounds. */
	private readonly sounds: Array<SoundObject>;
	/** Layer volume to apply to sounds. */
	private volume: number;
	private layerName: string;


	/**
	 * Creates a new layer group.
	 *
	 * @param {SoundLayer} layer
	 *   Layer ID.
	 */
	constructor(layer: SoundLayer) {
		this.sounds = [];
		this.layerName = layer.value;
		const volume = stendhal.config.getInt("sound." + this.layerName + ".volume");
		if (typeof(volume) === "undefined") {
			console.error("Unsupported layer: " + this.layerName + "\n", new Error());
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
	 * @param {SoundObject} sound
	 *   Sound to be removed.
	 * @returns {boolean}
	 *   `true` if sound object not found in channel.
	 */
	remove(sound: SoundObject): boolean {
		const idx = this.sounds.indexOf(sound);
		if (idx > -1) {
			this.sounds.splice(this.sounds.indexOf(sound), 1);
		}
		return this.sounds.indexOf(sound) < 0;
	}

	play(sound: SoundObject) {
		if (this.sounds.length >= SoundLayerGroup.MAX) {
			console.warn("Not playing \"" + sound.getSource() + "\", cannot play more than "
					+ SoundLayerGroup.MAX + " simultaneous sounds");
			return;
		}

		// DEBUG:
		Chat.debug("Playing on layer \"" + this.layerName + "\":", sound.getSource());

		if (sound.isActive()) {
			// make a copy so multiple instances can play simultaneously
			sound = sound.makeCopy();
		}
		sound.onEnded = () => {
			if (!this.remove(sound)) {
				console.warn("Failed to remove sound:", sound.getSource());
			}
		};
		this.sounds.push(sound);
		sound.play();
	}

	stop(force=false) {
		for (const sound of [...this.sounds]) {
			if (sound instanceof MapMusicObject && !force) {
				// zone music should continue playing on zone change
				continue;
			}
			sound.stop();
		}
	}

	pause() {
		for (const sound of this.sounds) {
			sound.pause();
		}
	}

	resume() {
		for (const sound of this.sounds) {
			sound.play();
		}
	}

	mute() {
		for (const sound of this.sounds) {
			sound.mute();
		}
	}

	unmute() {
		for (const sound of this.sounds) {
			sound.unmute();
		}
	}
}
