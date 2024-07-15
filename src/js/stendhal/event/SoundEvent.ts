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

import { RPEvent } from "./RPEvent";

import { SoundID } from "../data/sound/SoundID";
import { SoundLayer } from "../data/sound/SoundLayer";
import { SoundManager } from "../data/sound/SoundManager";

import { MathUtil } from "../util/MathUtil";

declare var marauroa: any;
declare var stendhal: any;


export class SoundEvent extends RPEvent {

	sound?: string;
	sound_id?: string;
	volume!: number;
	radius?: number;
	// FIXME: should this be number?
	layer!: string;


	execute(entity: any) {
		if (!marauroa.me) {
			return;
		}
		let radius = SoundManager.DEFAULT_RADIUS;
		if (typeof(this["radius"]) === "number") {
			radius = this["radius"];
		}
		if (!marauroa.me.isInSoundRange(radius, entity)) {
			// too far away to hear so don't load
			return;
		}

		// adjust by the server specified volume, if any
		const volume = MathUtil.parseIntDefault(this["volume"], 100);

		let sound = this["sound"];
		// get sound from ID
		if (this["sound_id"]) {
			sound = SoundID[this["sound_id"]];
		}

		const lidx = MathUtil.parseIntDefault(this["layer"], -1);
		stendhal.sound.playLocal(sound, volume, SoundLayer.checkLayer(lidx), radius, entity["_x"],
				entity["_y"]);
	}
}
