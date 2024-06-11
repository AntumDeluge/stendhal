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

import { AbstractEnum } from "../enum/AbstractEnum";


/**
 * Available layers of sound.
 *
 * NOTE: string names should match `games.stendhal.common.constants.SoundLayer`
 */
export class SoundLayer extends AbstractEnum<string> {

	/** Reference to layers. */
	private static readonly layers: SoundLayer[] = [];

	static readonly MUSIC = new SoundLayer("music");
	static readonly AMBIENT = new SoundLayer("ambient");
	static readonly CREATURE = new SoundLayer("creature");
	static readonly SFX = new SoundLayer("sfx");
	static readonly GUI = new SoundLayer("gui");

	/** Value is required. */
	override readonly value!: string;


	private constructor(value: string) {
		super(value);
		SoundLayer.layers.push(this);
	}

	/**
	 * Retreives all layer types.
	 *
	 * @returns {SoundLayer[]}
	 *   Registered layers.
	 */
	static getLayers(): SoundLayer[] {
		return [...this.layers];
	}

	/**
	 * Retrieves all layer names.
	 *
	 * @return {string[]}
	 *   Names of available layers.
	 */
	static names(): string[] {
		const names: string[] = [];
		for (const layer of SoundLayer.layers) {
			names.push(layer.value);
		}
		return names;
	}

	/**
	 * Retrieves sound layer corresponding to layer name.
	 *
	 * @param {(string|number|SoundLayer)=} l
	 *   Layer, name, or index.
	 * @returns {SoundLayer}
	 *   Sound layer matching name/index or `SoundLayer.GUI` if not recognized.
	 */
	static check(l?: string|number|SoundLayer): SoundLayer {
		if (l instanceof SoundLayer) {
			return l as SoundLayer;
		}
		const ltype = typeof(l);
		if (ltype === "undefined") {
			return SoundLayer.GUI;
		}
		if (ltype === "number") {
			const layer = SoundLayer.layers[l];
			if (layer) {
				return layer;
			}
			console.warn("Unknown layer \"" + l + "\"\n", new Error());
			// default to GUI
			return SoundLayer.GUI;
		}
		for (const layer of SoundLayer.layers) {
			if (layer.value === l) {
				return layer;
			}
		}
		console.warn("Unknown layer \"" + l + "\"\n", new Error());
		// default to GUI
		return SoundLayer.GUI;
	}
}
