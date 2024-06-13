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

import { MapMusicObject } from "./MapMusicObject";
import { MusicObject } from "./MusicObject";
import { SoundLayer } from "./SoundLayer";
import { SoundLayerGroup } from "./SoundLayerGroup";
import { SoundLoopObject } from "./SoundLoopObject";
import { SoundObject } from "./SoundObject";
import { SoundType } from "./SoundType";
import { SoundUtil } from "./SoundUtil";

import { ui } from "../../ui/UI";

import { ComponentBase } from "../../ui/toolkit/ComponentBase";

import { Point } from "../../util/Point";

declare var stendhal: any;


/**
 * Manages playing sounds & music.
 *
 * TODO:
 * - use fade in/out for music layer
 * FIXME:
 * - sounds not playing
 */
export class SoundManager extends ComponentBase {

	/** Main HTML element. */
	readonly componentElement: HTMLAudioElement;
	private readonly ctx: AudioContext;

	/** Active layers on which sounds can be played. */
	private readonly layers: Record<string, SoundLayerGroup>;
	/** Music playing globally on map. */
	private zoneMusic?: SoundObject;

	private readonly cache: Record<string, SoundObject>;

	/** Singleton instance. */
	private static instance: SoundManager;


	/**
	 * Retrieves singleton instance.
	 *
	 * @returns {SoundManager}
	 *   Sound manager singleton instance.
	 */
	static get(): SoundManager {
		if (!SoundManager.instance) {
			SoundManager.instance = new SoundManager();
		}
		return SoundManager.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		super();
		this.componentElement = document.getElementById("sound-manager")! as HTMLAudioElement;
		this.ctx = new AudioContext();
		const source = this.ctx.createMediaElementSource(this.componentElement);
		source.connect(this.ctx.destination);

		this.cache = {};

		// initialize channels
		this.layers = {};
		for (const layer of SoundLayer.getLayers()) {
			this.layers[layer.value] = new SoundLayerGroup(layer);
		}
	}

	/**
	 * Loads a sound.
	 *
	 * @param {string} id
	 *   Sound name/identifier.
	 * @param {number} volume
	 *   Base volume level.
	 * @param {SoundLayer} layer
	 *   Sound layer on which to play.
	 * @param {SoundType} type
	 *   Sound type.
	 * @returns {SoundObject}
	 *   Cached or new sound.
	 */
	private load(id: string, volume: number, layer: SoundLayer, type: SoundType): SoundObject {
		if (!this.cache[id]) {
			let prefix = stendhal.paths.sounds;
			if (type === SoundType.MUSIC || type === SoundType.ZONE_MUSIC) {
				prefix = stendhal.paths.music;
			}
			const source = prefix + "/" + id;
			switch(type) {
				case SoundType.MUSIC:
					this.cache[id] = new MusicObject(layer, id, source, volume);
					break;
				case SoundType.ZONE_MUSIC:
					this.cache[id] = new MapMusicObject(layer, id, source, volume);
					break;
				case SoundType.LOOP:
					this.cache[id] = new SoundLoopObject(layer, id, source, volume);
					break;
				default:
					this.cache[id] = new SoundObject(layer, id, source, volume);
			}
		}
		let sound = this.cache[id];
		if (sound.isPlaying()) {
			// make a copy so multiple instances can play simultaneously
			sound = sound.makeCopy();
		}
		return sound;
	}

	playLocal(id: string, volume: number, radius: number, x: number, y: number, layer: SoundLayer,
			type=SoundType.EFFECT): SoundObject {
		const sound = this.load(id, volume, layer, type);
		sound.setRadius(radius);
		sound.setPosition(x, y);
		this.getLayerGroup(layer).play(sound);
		return sound;
	}

	playLocalLoop(id: string, volume: number, radius: number, x: number, y: number, layer: SoundLayer)
			: SoundLoopObject {
		/*
		const sound = this.load(layer, name, SoundType.PERCEIVED_LOOP);
		sound.setRadius(radius);
		sound.setPosition(x, y);
		return sound;
		*/
		return this.playLocal(id, volume, radius, x, y, layer, SoundType.LOOP);
	}

	playLocalMusic(id: string, volume: number, radius: number, x: number, y: number): MusicObject {
		return this.playLocal(id, volume, radius, x, y, SoundLayer.MUSIC, SoundType.MUSIC);
	}

	playGlobal(id: string, volume: number, layer: SoundLayer, type=SoundType.EFFECT): SoundObject {
		const sound = this.load(id, volume, layer, type);
		this.getLayerGroup(layer).play(sound);
		return sound;
	}

	playGlobalLoop(id: string, volume: number, layer: SoundLayer): SoundLoopObject {
		return this.playGlobal(id, volume, layer, SoundType.LOOP);
	}

	playGlobalMusic(id: string, volume: number): MusicObject {
		return this.playGlobal(id, volume, SoundLayer.MUSIC, SoundType.MUSIC);
	}

	playZoneMusic(id?: string, volume=100): MapMusicObject|undefined {
		if (!id) {
			this.stopZoneMusic();
			return this.zoneMusic;
		}
		if (!this.zoneMusic || id !== this.zoneMusic.getId()) {
			this.stopZoneMusic();
			this.zoneMusic = this.playGlobal(id, volume, SoundLayer.MUSIC, SoundType.ZONE_MUSIC);
		}
		return this.zoneMusic;
	}

	stopZoneMusic() {
		if (this.zoneMusic) {
			this.zoneMusic.stop();
			this.zoneMusic = undefined;
		}
	}

	/**
	 * Adjust volume level depending on distance from sound source.
	 *
	 * @param {SoundLoopObject} sound
	 *   Sound source.
	 * @param {number} radius
	 *   Distance at which sound can be heard.
	 * @param {Point} lpos
	 *   Listener's position.
	 */
	adjustForDistance(sound: SoundLoopObject, radius: number, lpos: Point) {
		// DEBUG:
		console.warn("adjustForDistance not implemented");

		// TODO:
	}

	/**
	 * Stops & removes all active sounds.
	 */
	stopAll() {
		for (const layer of this.getLayerGroups()) {
			layer.stop();
		}
	}

	/**
	 * Pauses all active sounds.
	 */
	pauseAll() {
		for (const layer of this.getLayerGroups()) {
			layer.pause();
		}
	}

	/**
	 * Resumes all active sounds from paused state.
	 */
	resumeAll() {
		for (const layer of this.getLayerGroups()) {
			layer.resume();
		}
	}

	/**
	 * Mutes all active sounds.
	 */
	muteAll() {
		for (const layer of this.getLayerGroups()) {
			layer.mute();
		}
	}

	/**
	 * Unmutes all active sounds.
	 */
	unmuteAll() {
		for (const layer of this.getLayerGroups()) {
			layer.unmute();
		}
	}

	/**
	 * Retrieves a sound layer.
	 *
	 * @param {SoundLayer|string} layer
	 *   Layer type.
	 * @returns {SoundLayerGroup}
	 *   Sound layer group.
	 */
	getLayerGroup(layer: SoundLayer|string): SoundLayerGroup {
		if (layer instanceof SoundLayer) {
			layer = layer.value;
		}
		return this.layers[layer as string] || this.layers[SoundLayer.GUI.value];
	}

	/**
	 * Retrieves all sound layers.
	 *
	 * @returns {SoundLayerGroup[]}
	 *   Sound layer groups.
	 */
	getLayerGroups(): SoundLayerGroup[] {
		const layers: SoundLayerGroup[] = [];
		for (const lname in this.layers) {
			layers.push(this.layers[lname]);
		}
		return layers;
	}

	/**
	 * Retrieves layer volume.
	 *
	 * @param {SoundLayer|string} layer
	 *   Layer type.
	 * @returns {number}
	 *   Volume level between 0-100.
	 */
	getLayerVolume(layer: SoundLayer|string): number {
		if (layer === "master") {
			return this.getMasterVolume();
		}
		return this.getLayerGroup(layer).getVolume();
	}

	/**
	 * Sets layer volume.
	 *
	 * @param {SoundLayer|string} layer
	 *   Layer type.
	 * @param {number}
	 *   Volume level between 0-100.
	 */
	setLayerVolume(layer: SoundLayer|string, volume: number) {
		if (layer === "master") {
			this.setMasterVolume(volume);
			return;
		}
		this.getLayerGroup(layer).setVolume(volume);
		this.onVolumeChanged();
	}

	getMasterVolume(): number {
		return stendhal.config.getInt("sound.master.volume");
	}

	setMasterVolume(volume: number) {
		stendhal.config.set("sound.master.volume", SoundUtil.normVolume(volume));
		this.onVolumeChanged();
	}

	private onVolumeChanged() {
		// DEBUG:
		console.log("onVolumeChanged");

		// TODO:
	}

	/**
	 * Toggles muted state of sound system.
	 */
	toggleSound() {
		stendhal.config.set("sound", !stendhal.config.getBoolean("sound"));
		this.onStateChanged();
	}

	/**
	 * Called when sound enabled/disabled state is changed.
	 */
	onStateChanged() {
		if (stendhal.config.getBoolean("sound")) {
			this.unmuteAll();
		} else {
			this.muteAll();
		}
		// notify client
		ui.onSoundUpdate();
	}

	/**
	 * Can be called when configuration values change.
	 */
	onConfigUpdate() {
		for (const lname of ["master", ...SoundLayer.names()]) {
			let vol = stendhal.config.getInt("sound." + lname + ".volume");
			if (typeof(vol) !== "number") {
				console.warn("Unrecognized volume value for layer \"" + lname + "\":", vol);
				// default to full volume
				vol = 100;
			}
			this.setLayerVolume(lname, vol);
		}
	}

	/**
	 * Called at startup to pre-cache certain sounds.
	 */
	startupCache() {
		// login sound
		this.load("ui/login", 100, SoundLayer.SFX, SoundType.EFFECT);
	}
}
