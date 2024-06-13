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
import { SoundManager } from "./SoundManager";
import { SoundUtil } from "./SoundUtil";

import { MathUtil } from "../../util/MathUtil";

declare var stendhal: any;


/**
 * Extended `HTMLAudioElement` implementation.
 */
interface AudioElementImpl extends HTMLAudioElement {
	/** Name/Identifier. */
	readonly id: string;
	/** Base volume level unique to this sound. */
	readonly basevolume: number;
	/** Channel layer sound plays on. */
	readonly layer: SoundLayer;
	/** Distance at which sound can be heard. */
	radius?: number;
	/** Coordinate of sound entity on X axis. */
	x?: number;
	/** Coordinate of sound entity on Y axis. */
	y?: number;
}


/**
 * Wrapper class for an `HTMLAudioElement`.
 */
export class SoundObject {

	/** The HTML audio element. */
	protected readonly audio: AudioElementImpl;

	onEnded?: Function;


	/**
	 * Creates a new sound object.
	 *
	 * @param {SoundLayer} layer
	 *   Layer this sound plays on.
	 * @param {string} id
	 *   Name/Identifier.
	 * @param {string} source
	 *   Audio source filename.
	 * @param {number} volume
	 *   Base volume.
	 */
	constructor(layer: SoundLayer, id: string, source: string, volume: number);

	/**
	 * Creates a new sound object.
	 *
	 * @param {SoundLayer} layer
	 *   Layer this sound plays on.
	 * @param {string} source
	 *   Audio source filename.
	 * @param {number} volume
	 *   Base volume.
	 */
	constructor(layer: SoundLayer, source: string, volume: number);

	// implementation constructor
	constructor(layer: SoundLayer, p1: string, p2: string|number, p3?: number) {
		// use `any` temporary object to set readonly properties
		const audio = new Audio() as any;
		audio.layer = layer;
		audio.autoplay = false;
		audio.loop = false;
		if (typeof(p2) === "number") {
			audio.id = p1;
			audio.src = p1 + ".ogg";
			audio.basevolume = this.checkVolume(p2 as number);
		} else {
			audio.id = p1;
			audio.src = p2 as string + ".ogg";
			audio.basevolume = this.checkVolume(typeof(p3) !== "undefined" ? p3 : 100);
		}

		if (Number.isNaN(parseFloat(audio.basevolume))) {
			console.error("Attempted to assign non-number to volume: " + audio.basevolume + "\n",
					new Error());
		}

		audio.volume = audio.basevolume;
		this.audio = audio as AudioElementImpl;

		// event listeners
		this.audio.onended = () => {
			if (this.onEnded) {
				this.onEnded(new Event("endsound"));
			}
		};
	}

	/**
	 * Makes a copy of a sound object.
	 *
	 * @param {SoundObject} obj
	 *   Object to be copied.
	 * @returns {SoundObject}
	 *   New sound object instance.
	 */
	/*
	static copy(obj: SoundObject): SoundObject {
		// use extending class constructor
		return new this(obj.audio.layer, obj.audio.id, obj.audio.src.replace(/\.ogg$/, ""),
				obj.audio.basevolume);
	}
	*/

	/**
	 * Makes a copy of this sound object.
	 *
	 * @returns {SoundObject}
	 *   New sound object instance.
	 */
	makeCopy(): SoundObject {
		const clazz = Object.getPrototypeOf(this).constructor;
		return new clazz(this.audio.layer, this.audio.id, this.audio.src.replace(/\.ogg$/, ""),
				this.audio.basevolume);
	}

	/**
	 * Checks if an object is considered the same as this one.
	 *
	 * @param {any} obj
	 *   Object to be compared.
	 * @returns {boolean}
	 *   `true` if `obj` is `SoundObject` instance & attributes match.
	 */
	equals(obj: any): boolean {
		if (!(obj instanceof SoundObject)) {
			return false;
		}
		return this.audio.id === obj.audio.id && this.audio.src === obj.audio.src
				&& this.audio.layer === obj.audio.layer && this.audio.loop === obj.audio.loop
				&& this.audio.basevolume === obj.audio.basevolume;
	}

	isPlaying(): boolean {
		if (this.audio.loop) {
			return true;
		}
		return !this.audio.ended;
	}

	/**
	 * Retrieves audio identifier.
	 *
	 * @returns {string}
	 *   Name/Identifier.
	 */
	getId(): string {
		return this.audio.id;
	}

	/**
	 * Retrieves audio source.
	 *
	 * @returns {string}
	 *   Path to audio source file.
	 */
	getSource(): string {
		return this.audio.src;
	}

	/**
	 * Normalizes & formats volume.
	 *
	 * @param {number} volume
	 *   Volume level value between 0-100.
	 * @returns {number}
	 *   Volume level converted to float value between 0-1 for compatibility with `HTMLAudioElement`.
	 */
	private checkVolume(volume: number): number {
		return SoundUtil.normVolume(volume) / 100;
	}

	/**
	 * Sets actual volume.
	 *
	 * @param {number} volume
	 *   New volume level value between 0-100.
	 */
	public setVolume(volume: number) {
		this.audio.volume = this.checkVolume(volume);
	}

	/**
	 * Sets distance at which sound can be heard.
	 *
	 * @param {number} radius
	 *   New radius.
	 */
	public setRadius(radius: number) {
		this.audio.radius = radius;
	}

	/**
	 * Sets perceived position of sound.
	 *
	 * @param {number} x
	 *   X coordinate of sound origin.
	 * @param {number} y
	 *   Y coordinate of sound origin.
	 */
	public setPosition(x: number, y: number) {
		this.audio.x = x;
		this.audio.y = y;
	}

	/**
	 * Checks sound attributes if volume should be affected by distance.
	 *
	 * @returns {boolean}
	 *   `true` if sound has `x`, `y`, & `radius` attributes.
	 */
	private isPerceived(): boolean {
		return typeof(this.audio.radius) !== "undefined" && typeof(this.audio.x) === "undefined"
				&& typeof(this.audio.y) === "undefined";
	}

	/**
	 * Adjusts for perceived distance from volume source.
	 *
	 * @param {number} x
	 *   Listener's coordinate position on horizontal axis.
	 * @param {number} y
	 *   Listener's coordinate position on vertical axis.
	 */
	public adjustForDistance(x: number, y: number) {
		const distance = this.getDistanceTo(x, y);
		if (distance < 0) {
			// this sound is not affected by distance
			return;
		}
		if (distance > this.audio.radius!) {
			// outside perceived radius
			this.audio.volume = 0;
			return;
		}
		//const euclidean = Math.sqrt(Math.pow(x - this.audio.x, 2) + Math.pow(y - this.audio.y, 2));
		const rad2 = this.audio.radius! * this.audio.radius!;
		const max = this.getAdjustedVolume();
		// The sound api does not guarantee anything about how the volume
		// works, so it does not matter much how we scale it.
		//~ this.audio.volume = SoundUtil.normVolume(Math.min(rad2 / (dist2 * 20), max));
		this.audio.volume = SoundUtil.normVolume(Math.min(rad2 / distance, max));
	}

	/**
	 * Applies layer volume levels.
	 *
	 * @returns {number}
	 *   Volume level adjusted using "master" & associated layer.
	 */
	//~ private getAdjustedVolume(layerName: string, volBase: number): number {
	private getAdjustedVolume(): number {
		const volMaster = stendhal.config.config.getInt("sound.master.volume");
		const volLayer = stendhal.config.getInt("sound." + this.audio.layer.value + ".volume");
		let volActual = (this.audio.basevolume * 100) * volMaster;
		if (typeof(volLayer) !== "number") {
			console.warn("Cannot adjust volume for layer \"" + this.audio.layer.value + "\"");
			return volActual;
		}
		return volActual * volLayer;
	}

	/**
	 * Retrieves distance from sound origin to another point.
	 *
	 * @param {number} x
	 *   X coordinate of other point.
	 * @param {number} y
	 *   Y coordinate of other point.
	 * @returns {number}
	 *   Radial distance or -1 if not affected by distance.
	 */
	private getDistanceTo(x: number, y: number): number {
		if (!this.isPerceived()) {
			return -1;
		}
		return MathUtil.getDistance(this.audio.x!, this.audio.y!, x, y);
	}

	/**
	 * Begins playback.
	 */
	play() {
		this.audio.play();
	}

	/**
	 * Stops playback & removes from channel.
	 */
	stop() {
		this.audio.pause();
		this.audio.currentTime = 0;
		/*
		if (sSound.onended) {
			sSound.onended(new Event("stopsound"));
		}
		*/
		if (this.onEnded) {
			this.onEnded(new Event("endsound"));
		}
	}

	/**
	 * Pauses playback.
	 */
	pause() {
		this.audio.pause();
	}

	/**
	 * Mutes volume but continues playing.
	 */
	mute() {
		this.audio.muted = true;
	}

	/**
	 * Unmutes volume.
	 */
	unmute() {
		this.audio.muted = false;
	}
}
