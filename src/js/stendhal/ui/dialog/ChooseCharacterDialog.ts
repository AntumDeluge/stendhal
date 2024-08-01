/***************************************************************************
 *                (C) Copyright 2015-2024 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { OutfitPreviewComponent } from "../component/OutfitPreviewComponent";

import { DialogContentComponent } from "../toolkit/DialogContentComponent";

import { Client } from "../../Client";
import { SlashActionRepo } from "../../SlashActionRepo";

declare var stendhal: any;


/**
 * A dialog to select your character from.
 *
 * FIXME:
 *   - can't select characters if dialog extends past right edge
 */
export class ChooseCharacterDialog extends DialogContentComponent {

	constructor(characters: any) {
		super("choose-character-template");

		const loaders: any[] = [];
		const characterList = this.child("#characters")!;
		for (var i in characters) {
			if (characters.hasOwnProperty(i)) {
				let name = characters[i]["a"]["name"];
				let button = document.createElement("button");
				button.classList.add("menubutton");
				button.innerText = name;
				button.addEventListener("click", () => {
					this.componentElement.dispatchEvent(new Event("close"));
					Client.get().chooseCharacter(name);
				});

				const preview = new OutfitPreviewComponent();
				// listen for dialog close
				preview.componentElement.addEventListener("cancel-preview", (evt: Event) => {
					for (const loader of loaders) {
						loader.cancel = true;
					}
					preview.cancel();
				});
				loaders.push(this.loadOutfit(preview, characters[i]));

				const col = document.createElement("div");
				col.classList.add("verticalgroup", "choose-character");
				col.append(preview.componentElement, button);
				characterList.append(col);
			}
		}

		this.child("#logout")!.addEventListener("click", (e: Event) => {
			this.componentElement.dispatchEvent(new Event("close"));
			this.onLogout();
		});
	}

	/**
	 * Retrieves the canvas elements used to draw outfit previews.
	 *
	 * @returns {HTMLElement[]}
	 *   List of preview elements for each character.
	 */
	private getPreviewElements(): HTMLElement[] {
		const elements: HTMLElement[] = [];
		const characters = this.child("#characters")!.children;
		for (let idx = 0; idx < characters.length; idx++ ) {
			const previewElement = characters[idx].querySelector("#outfit-preview");
			if (previewElement) {
				elements.push(previewElement as HTMLElement);
			}
		}
		return elements;
	}

	override onParentClose() {
		const evt = new Event("cancel-preview");
		for (const previewElement of this.getPreviewElements()) {
			// cancel re-attempts to load outfit image
			previewElement.dispatchEvent(evt);
		}
	}

	onLogout() {
		queueMicrotask(() => {
			// TODO: detect if not logged in via website & simply re-display login dialog
			SlashActionRepo.get().execute("/logout");
		});
	}

	/**
	 * Creates & executes an object for loading preview image.
	 *
	 * FIXME:
	 *   - sometimes not all layers are drawn on inital login after clearing browser cache
	 *     (Firefox/Windows)
	 *   - layer coloring doesn't work on initial login after clearing browser cache (Firefox/Windows)
	 *
	 * @param {OutfitPreviewComponent} preview
	 *   Component for displaying outfit preview.
	 * @param {any} obj
	 *   Object containing entity outfit attributes.
	 * @returns {any}
	 *   Loader object.
	 */
	private loadOutfit(preview: OutfitPreviewComponent, obj: any): any {
		const loader: any = {
			preview: preview,
			obj: obj,
			attempts: 0,
			cancel: false,

			load: function() {
				if (this.attempts >= 10 || this.cancel) {
					// layer coloring may not be available or loading was cancelled
					return;
				}
				this.attempts++;
				const outfitString = this.obj["a"]["outfit_ext"];
				let colorsMap: any = {};
				if (obj["m"] && obj["m"]["outfit_colors"]) {
					colorsMap = obj["m"]["outfit_colors"]["a"] || colorsMap;
				}
				const coloring: string[] = [];
				const layerNames = ["skin", ...stendhal.data.outfit.getLayerNames()];
				for (const part in colorsMap) {
					// TODO: move this check to `Outfit.build`
					if (layerNames.indexOf(part) > -1) {
						coloring.push(part + "=" + colorsMap[part]);
					}
				}
				if (outfitString) {
					preview.setOutfit(outfitString, coloring.join(","));
					if (coloring.length === 0) {
						// coloring info may not have been ready
						window.setTimeout(() => {
							this.load();
						}, 500);
					}
				}
			}
		};
		loader.load();
		return loader;
	}
}
