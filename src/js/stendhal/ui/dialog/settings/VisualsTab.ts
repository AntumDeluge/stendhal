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

import { WidgetFactory } from "../../factory/WidgetFactory";

import { SettingsComponent } from "../../toolkit/SettingsComponent";

import { singletons } from "../../../SingletonRepo";

import { StandardMessages } from "../../../util/StandardMessages";


export class VisualsTab extends AbstractSettingsTab {

	constructor(parent: SettingsDialog, element: HTMLElement) {
		super(element);
		const config = singletons.getConfigManager();

		const col1 = this.child("#col1")!;

		WidgetFactory.checkbox(col1, "set-light", "Light effects", "effect.lighting",
				"Lighting effects are enabled", "Lighting effects are disabled")
				.addListener(function(evt: Event) {
					StandardMessages.changeNeedsRefresh();
				});

		WidgetFactory.checkbox(col1, "set-weather", "Weather", "effect.weather", "Weather is enabled",
				"Weather is disabled")
				.addListener(function(evt: Event) {
					StandardMessages.changeNeedsRefresh();
				});

		WidgetFactory.checkbox(col1, "set-blood", "Blood", "effect.blood", "Gory images are enabled",
				"Gory images are disabled")
				.addListener(function(evt: Event) {
					StandardMessages.changeNeedsRefresh();
				});

		WidgetFactory.checkbox(col1, "set-nonude", "Cover naked entities", "effect.no-nude",
				"Naked entities have undergarments", "Naked entities are not covered");

		WidgetFactory.checkbox(col1, "set-shadows", "Shadows", "effect.shadows", "Shadows are enabled",
				"Shadows are disabled");

		WidgetFactory.checkbox(col1, "set-click-indicator", "Display clicks/touches", "click-indicator",
				"Displaying clicks", "Not displaying clicks");

		let chkAnimate: SettingsComponent;
		WidgetFactory.checkbox(col1, "set-activity-indicator", "Object activity indicator",
				"activity-indicator",
				"Display an indictor over certain interactive objects and corpses that aren't empty")
			.addListener(function(evt: Event) {
				chkAnimate.setEnabled(config.getBoolean("activity-indicator"));
				StandardMessages.changeNeedsRefresh();
				parent.refresh();
			});

		chkAnimate = WidgetFactory.checkbox(col1, "set-activity-indicator-animate", "Animate",
				"activity-indicator.animate");
		chkAnimate.addListener(function(evt: Event) {
			StandardMessages.changeNeedsRefresh();
			parent.refresh();
		});
		chkAnimate.componentElement.classList.add("indented");
		chkAnimate.setEnabled(config.getBoolean("activity-indicator"));

		WidgetFactory.checkbox(col1, "set-parallax", "Parallax scrolling backgrounds",
				"effect.parallax", "Parallax scrolling enabled", "Parallax scrolling disabled")
			.addListener(function(evt: Event) {
				StandardMessages.changeNeedsRefresh();
				parent.refresh();
			});

		WidgetFactory.checkbox(col1, "set-entity-overlay", "Entity overlay effects",
				"effect.entity-overlay")
			.addListener((evt: Event) => {
				StandardMessages.changeNeedsRefresh();
				parent.refresh();
			});
	}
}
