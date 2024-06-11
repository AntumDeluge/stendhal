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

import { ComponentBase } from "../../ui/toolkit/ComponentBase";


/**
 * Manages playing sounds & music.
 *
 * TODO:
 * - use fade in/out for music layer
 */
export class SoundManagerNG extends ComponentBase {

	/** Main HTML element. */
	readonly componentElement: HTMLAudioElement;
	private readonly ctx: AudioContext;

	/** Active layers on which sounds can be played. */
	private readonly layers: Record<string, SoundLayerGroup>;
	/** Music playing globally on map. */
	private map?: SoundObject;

	private readonly cache: Record<string, SoundObject>;

	/** Singleton instance. */
	private static instance: SoundManagerNG;


	/**
	 * Retrieves singleton instance.
	 *
	 * @returns {SoundManagerNG}
	 *   Sound manager singleton instance.
	 */
	static get(): SoundManagerNG {
		if (!SoundManagerNG.instance) {
			SoundManagerNG.instance = new SoundManagerNG();
		}
		return SoundManagerNG.instance;
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
	 * @returns {SoundObject}
	 *   Cached or new sound object instance.
	 */
	private load(layer: SoundLayer, id: string, type: SoundType): SoundObject {
		if (!this.cache[id]) {
			// TODO: set basevolume dynamically
			const basevolume = 100;
			switch(type) {
				case SoundType.MUSIC:
					this.cache[id] = new MusicObject(layer, id, basevolume);
					break;
				case SoundType.MAP_MUSIC:
					this.cache[id] = new MapMusicObject(layer, id, basevolume);
					break;
				case SoundType.LOOP:
					this.cache[id] = new SoundLoopObject(layer, id, basevolume);
					break;
				default:
					this.cache[id] = new SoundObject(layer, id, basevolume);
			}
		}
		// add to layer channel
		this.getLayer(layer).push(this.cache[id]);
		return this.cache[id];
	}

	playPerceived(layer: SoundLayer, name: string, radius: number, x: number, y: number,
			type=SoundType.EFFECT) : SoundObject {
		const sound = this.load(layer, name, type);
		sound.setRadius(radius);
		sound.setPosition(x, y);
		return sound;
	}

	playPerceivedLoop(layer: SoundLayer, name: string, radius: number, x: number, y: number)
			: SoundLoopObject {
		/*
		const sound = this.load(layer, name, SoundType.PERCEIVED_LOOP);
		sound.setRadius(radius);
		sound.setPosition(x, y);
		return sound;
		*/
		return this.playPerceived(layer, name, radius, x, y, SoundType.LOOP);
	}

	playPerceivedMusic(name: string, radius: number, x: number, y: number): MusicObject {
		return this.playPerceived(SoundLayer.MUSIC, name, radius, x, y, SoundType.MUSIC);
	}

	playGlobal(layer: SoundLayer, name: string, type=SoundType.EFFECT): SoundObject {
		return this.load(layer, name, type);
	}

	playGlobalLoop(layer: SoundLayer, name: string): SoundLoopObject {
		return this.playGlobal(layer, name, SoundType.LOOP);
	}

	playGlobalMusic(name: string): MusicObject {
		return this.playGlobal(SoundLayer.MUSIC, name, SoundType.MUSIC);
	}

	playMapMusic(name?: string): MapMusicObject|undefined {
		if (!name) {
			this.stopMapMusic();
			return this.map;
		}
		if (this.map && name !== this.map.getId()) {
			// map plays different music than previous
			this.stopMapMusic();
			this.map = this.playGlobal(SoundLayer.MUSIC, name, SoundType.MAP_MUSIC);
		}
		return this.map;
	}

	stopMapMusic() {
		if (this.map) {
			this.map.stop();
			this.map = undefined;
		}
	}

	/**
	 * Retrieves sound channel.
	 *
	 * @param {SoundLayer} layer
	 *   Channel type.
	 */
	getLayer(layer: SoundLayer): SoundLayerGroup {
		return this.layers[layer.value];
	}

	/**
	 * Retrieves channel volume.
	 *
	 * @returns {number}
	 *   Volume level between 0-100.
	 */
	getLayerVolume(layer: SoundLayer): number {
		return this.getLayer(layer).getVolume();
	}
}
