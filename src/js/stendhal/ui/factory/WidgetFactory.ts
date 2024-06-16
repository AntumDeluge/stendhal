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

import { ComponentBase } from "../toolkit/ComponentBase";
import { SettingsComponent } from "../toolkit/SettingsComponent";

import { WidgetType } from "../../data/enum/WidgetType";

import { ConfigManager } from "../../util/ConfigManager";
import { Debug } from "../../util/Debug";


export namespace WidgetFactory {

	const config = ConfigManager.get();

	/**
	 * Creates a check box widget.
	 *
	 * @param {ComponentBase|HTMLElement} parent
	 *   Component or element to which widget is added as a child.
	 * @param {string} id
	 *   DOM element ID.
	 * @param {string} label
	 *   Label text.
	 * @param {string=} cid
	 *   Configuration ID.
	 * @param {string} ttpos
	 * @param {string} ttneg
	 * @param {boolean} experimental
	 *   Marks element to be hidden by default.
	 * @returns {SettingsComponent}
	 *   New widget.
	 */
	export function check(parent: ComponentBase|HTMLElement, id: string, label: string,
			cid?: string, ttpos?: string, ttneg?: string, experimental=false): SettingsComponent {
		// FIXME: don't need to pass experimental to component constructor
		const component = new SettingsComponent(id, label, WidgetType.CHECK, undefined, experimental);
		if (cid) {
			component.setConfigId(cid);
		}
		if (ttpos) {
			component.setTooltip(ttpos, ttneg);
		}
		if (experimental) {
			component.labelElement.innerText + " (experimental)"
			if (!Debug.isActive("settings")) {
				component.setEnabled(false);
				component.componentElement.style.setProperty("display", "none");
				component.labelElement.style.setProperty("display", "none");
			}
		}
		component.addTo(parent);
		return component;
	};
}
