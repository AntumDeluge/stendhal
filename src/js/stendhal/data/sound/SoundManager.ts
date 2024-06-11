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
		this.cache = {};
		this.ctx = new AudioContext();
		const source = this.ctx.createMediaElementSource(this.componentElement);
		source.connect(this.ctx.destination);

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
			if (type === SoundType.MUSIC || type === SoundType.MAP_MUSIC) {
				prefix = stendhal.paths.music;
			}
			const source = prefix + "/" + id;
			switch(type) {
				case SoundType.MUSIC:
					this.cache[id] = new MusicObject(layer, id, source, volume);
					break;
				case SoundType.MAP_MUSIC:
					this.cache[id] = new MapMusicObject(layer, id, source, volume);
					break;
				case SoundType.LOOP:
					this.cache[id] = new SoundLoopObject(layer, id, source, volume);
					break;
				default:
					this.cache[id] = new SoundObject(layer, id, source, volume);
			}
		}
		return this.cache[id];
	}

	/**
	 * Loads a sound & readies for playing on layer.
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
	private loadAndReady(id: string, volume: number, layer: SoundLayer, type: SoundType): SoundObject {
		this.load(id, volume, layer, type);
		// add to layer channel
		this.getLayer(layer).push(this.cache[id]);
		return this.cache[id];
	}

	playLocal(id: string, volume: number, radius: number, x: number, y: number, layer: SoundLayer,
			type=SoundType.EFFECT): SoundObject {
		const sound = this.load(id, volume, layer, type);
		sound.setRadius(radius);
		sound.setPosition(x, y);
		return sound;
	}

	playLocalLoop(id: string, radius: number, x: number, y: number, layer: SoundLayer)
			: SoundLoopObject {
		/*
		const sound = this.load(layer, name, SoundType.PERCEIVED_LOOP);
		sound.setRadius(radius);
		sound.setPosition(x, y);
		return sound;
		*/
		return this.playLocal(id, radius, x, y, layer, SoundType.LOOP);
	}

	playLocalMusic(id: string, radius: number, x: number, y: number): MusicObject {
		return this.playLocal(id, radius, x, y, SoundLayer.MUSIC, SoundType.MUSIC);
	}

	playGlobal(id: string, layer: SoundLayer, type=SoundType.EFFECT): SoundObject {
		return this.load(id, layer, type);
	}

	playGlobalLoop(id: string, layer: SoundLayer): SoundLoopObject {
		return this.playGlobal(id, layer, SoundType.LOOP);
	}

	playGlobalMusic(id: string): MusicObject {
		return this.playGlobal(id, SoundLayer.MUSIC, SoundType.MUSIC);
	}

	playZoneMusic(id?: string): MapMusicObject|undefined {
		if (!id) {
			this.stopZoneMusic();
			return this.zoneMusic;
		}
		if (this.zoneMusic && id !== this.zoneMusic.getId()) {
			// map plays different music than previous
			this.stopZoneMusic();
			this.zoneMusic = this.playGlobal(id, SoundLayer.MUSIC, SoundType.MAP_MUSIC);
		}
		return this.zoneMusic;
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

	stopZoneMusic() {
		if (this.zoneMusic) {
			this.zoneMusic.stop();
			this.zoneMusic = undefined;
		}
	}

	/**
	 * Stops & removes all active sounds.
	 */
	stopAll() {
		for (const layer of this.getLayers()) {
			layer.stop();
		}
	}

	/**
	 * Pauses all active sounds.
	 */
	pauseAll() {
		for (const layer of this.getLayers()) {
			layer.pause();
		}
	}

	/**
	 * Resumes all active sounds from paused state.
	 */
	resumeAll() {
		for (const layer of this.getLayers()) {
			layer.resume();
		}
	}

	/**
	 * Mutes all active sounds.
	 */
	muteAll() {
		for (const layer of this.getLayers()) {
			layer.mute();
		}
	}

	/**
	 * Unmutes all active sounds.
	 */
	unmuteAll() {
		for (const layer of this.getLayers()) {
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
	getLayer(layer: SoundLayer|string): SoundLayerGroup {
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
	getLayers(): SoundLayerGroup[] {
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
		return this.getLayer(layer).getVolume();
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
		this.getLayer(layer).setVolume(volume);
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
		this.load(SoundLayer.SFX, "ui/login", SoundType.EFFECT);
	}
}
