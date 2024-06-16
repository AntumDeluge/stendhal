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

import { AbstractSettingsTab } from "./AbstractSettingsTab";

import { SettingsDialog } from "../SettingsDialog";

import { SettingsComponent } from "../../toolkit/SettingsComponent";
import { SliderComponent } from "../../toolkit/SliderComponent";

import { WidgetFactory } from "../../factory/WidgetFactory";

import { singletons } from "../../../SingletonRepo";

import { SoundLayer } from "../../../data/sound/SoundLayer";


export class SoundTab extends AbstractSettingsTab {

	private readonly sliders: SliderComponent[];


	constructor(parent: SettingsDialog, element: HTMLElement) {
		super(element);
		const config = singletons.getConfigManager();
		const sound = singletons.getSoundManager();
		this.sliders = [];

		const col1 = this.child("#col1")!;

		// state of sound when dialog is created
		let soundEnabled = config.getBoolean("sound");

		WidgetFactory.checkbox(col1, "set-sound", "Enable sound", "sound")
				.addListener((evt: Event) => {
					sound.onStateChanged();
					this.setSlidersEnabled(config.getBoolean("sound"));
				});

		const layers = [
			["master", "Master"],
			["gui", "GUI"],
			["sfx", "Effects"],
			["creature", "Creatures"],
			["ambient", "Ambient"],
			["music", "Music"]
		];

		for (const group of layers) {
			const layer = group[0];
			const label = group[1];
			const slider = new SliderComponent("setting-vol-" + layer, label, 0, 100);
			slider.setValue(sound.getLayerVolume(layer));
			slider.addListener(function(evt: Event) {
				sound.setLayerVolume(layer, slider.getValue());
			});
			slider.addTo(col1);
			this.sliders.push(slider);
		}
		this.setSlidersEnabled(soundEnabled);

		// TODO:
		// - show volume level value
	}

	private setSlidersEnabled(enabled: boolean) {
		for (const slider of this.sliders) {
			slider.setEnabled(enabled);
		}
	}
}
