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
import { SliderComponent } from "../toolkit/SliderComponent";
import { WidgetComponent } from "../toolkit/WidgetComponent";

import { OptionsEnum } from "../../data/enum/OptionsEnum";
import { WidgetType } from "../../data/enum/WidgetType";

import { Debug } from "../../util/Debug";


export namespace WidgetFactory {

	/**
	 * Configures common widget attributes.
	 *
	 * @param {WidgetComponent} component
	 *   Widget being configured.
	 * @param {ComponentBase|HTMLElement} parent
	 *   Component or element to which widget is added as a child.
	 * @param {string=} cid
	 *   Configuration ID.
	 * @param {(string|number|boolean)=} value
	 *   Value with which to be intialized.
	 * @param {string=} ttpos
	 *   Displayed tooltip text (when checked for checkbox type).
	 * @param {string=} ttneg
	 *   Displayed tooltip text when not checked (checkbox type only).
	 * @param {boolean} [experimental=false]
	 *   Marks element to be hidden by default.
	 */
	function setup(component: WidgetComponent, parent: ComponentBase|HTMLElement, cid?: string,
			value?: string|number|boolean, ttpos?: string, ttneg?: string, experimental=false) {
		if (cid) {
			component.setConfigId(cid);
		}
		if (typeof(value) !== "undefined") {
			component.setValue(value);
		}
		if (ttpos) {
			component.setTooltip(ttpos, ttneg);
		}
		if (experimental) {
			component.componentElement.classList.add("experimental");
			component.labelElement.innerText + " (experimental)"
			if (!Debug.isActive("settings")) {
				component.setEnabled(false);
				component.componentElement.style.setProperty("display", "none");
				component.labelElement.style.setProperty("display", "none");
			}
		}
		component.addTo(parent);
	};

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
	 * @param {string=} ttpos
	 *   Displayed tooltip text when checked.
	 * @param {string=} ttneg
	 *   Displayed tooltip text not checked.
	 * @param {boolean} [experimental=false]
	 *   Marks element to be hidden by default.
	 * @returns {SettingsComponent}
	 *   New widget.
	 */
	export function check(parent: ComponentBase|HTMLElement, id: string, label: string, cid?: string,
			ttpos?: string, ttneg?: string, experimental=false): SettingsComponent {
		const component = new SettingsComponent(id, label, WidgetType.CHECK);
		setup(component, parent, cid, undefined, ttpos, ttneg, experimental);
		return component;
	};

	/**
	 * Creates a text input widget.
	 *
	 * @param {ComponentBase|HTMLElement} parent
	 *   Component or element to which widget is added as a child.
	 * @param {string} id
	 *   DOM element ID.
	 * @param {string} label
	 *   Label text.
	 * @param {string=} cid
	 *   Configuration ID.
	 * @param {string=} value
	 *   Value with which to be intialized.
	 * @param {string=} tooltip
	 *   Displayed tooltip text.
	 * @param {boolean} [experimental=false]
	 *   Marks element to be hidden by default.
	 * @returns {SettingsComponent}
	 *   New widget.
	 */
	export function text(parent: ComponentBase|HTMLElement, id: string, label: string, cid?: string,
			value?: string, tooltip?: string, experimental=false): SettingsComponent {
		const component = new SettingsComponent(id, label, WidgetType.TEXT);
		setup(component, parent, cid, value, tooltip, undefined, experimental);
		return component;
	};

	/**
	 * Creates a number input widget.
	 *
	 * @param {ComponentBase|HTMLElement} parent
	 *   Component or element to which widget is added as a child.
	 * @param {string} id
	 *   DOM element ID.
	 * @param {string} label
	 *   Label text.
	 * @param {string=} cid
	 *   Configuration ID.
	 * @param {string=} value
	 *   Value with which to be intialized.
	 * @param {string=} tooltip
	 *   Displayed tooltip text.
	 * @param {boolean} [experimental=false]
	 *   Marks element to be hidden by default.
	 * @returns {SettingsComponent}
	 *   New widget.
	 */
	export function number(parent: ComponentBase|HTMLElement, id: string, label: string, cid?: string,
			value?: number|string, tooltip?: string, experimental=false): SettingsComponent {
		const component = new SettingsComponent(id, label, WidgetType.NUMBER);
		setup(component, parent, cid, value, tooltip, undefined, experimental);
		return component;
	};

	/**
	 * Creates a multi-select widget.
	 *
	 * @param {ComponentBase|HTMLElement} parent
	 *   Component or element to which widget is added as a child.
	 * @param {string} id
	 *   DOM element ID.
	 * @param {string} label
	 *   Label text.
	 * @param {string=} cid
	 *   Configuration ID.
	 * @param {OptionsEnum=} options
	 *   Available options.
	 * @param {number} [index=0]
	 *   Value index with which to be intialized.
	 * @param {string=} tooltip
	 *   Displayed tooltip text.
	 * @param {boolean} [experimental=false]
	 *   Marks element to be hidden by default.
	 * @returns {SettingsComponent}
	 *   New widget.
	 */
	export function select(parent: ComponentBase|HTMLElement, id: string, label: string, cid?: string,
			options: OptionsEnum={}, index=0, tooltip?: string, experimental=false): SettingsComponent {
		const component = new SettingsComponent(id, label, WidgetType.SELECT, options);
		setup(component, parent, cid, index, tooltip, undefined, experimental);
		return component;
	};

	/**
	 * Creates a slider widget.
	 *
	 * @param {ComponentBase|HTMLElement} parent
	 *   Component or element to which widget is added as a child.
	 * @param {string} id
	 *   DOM element ID.
	 * @param {string} label
	 *   Label text.
	 * @param {string=} cid
	 *   Configuration ID.
	 * @param {number=} min
	 *   Lowest possible value this component can represent.
	 * @param {number=} max
	 *   Highest possible value this component can represent.
	 * @param {number=} value
	 *   Value with which to be intialized.
	 * @param {string=} tooltip
	 *   Displayed tooltip text.
	 * @param {boolean} [experimental=false]
	 *   Marks element to be hidden by default.
	 * @returns {SliderComponent}
	 *   New widget.
	 */
	export function slider(parent: ComponentBase|HTMLElement, id: string, label: string, cid?: string,
			min?: number, max?: number, value?: number, tooltip?: string, experimental=false)
			: SliderComponent {
		if (typeof(min) === "undefined") {
			min = 0;
		}
		if (typeof(max) === "undefined") {
			max = 100;
		}
		const component = new SliderComponent(id, label, min, max);
		setup(component, parent, cid, value, tooltip, undefined, experimental);
		return component;
	};
}
