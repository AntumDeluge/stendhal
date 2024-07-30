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


/**
 * A dialog to select your character from.
 *
 * FIXME:
 *   - can't select characters if dialog extends past right edge
 *   - doesn't display after refresh
 */
export class ChooseCharacterDialog extends DialogContentComponent {

	constructor(characters: any) {
		super("choose-character-template");

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
				let outfit = characters[i]["a"]["outfit_ext"];
				if (outfit) {
					preview.setOutfit(outfit);
				}

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

	onLogout() {
		queueMicrotask(() => {
			// TODO: detect if not logged in via website & simply re-display login dialog
			SlashActionRepo.get().execute("/logout");
		});
	}
}
