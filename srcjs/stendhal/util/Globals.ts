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

declare global {
	interface Window { defined: Function; }
}


export class Globals {

	private static initialized = false;

	/**
	 * Static properties & methods only.
	 */
	private constructor() {
		// do nothing
	}

	static init() {
		if (Globals.initialized) {
			console.warn("tried to re-initialize globals");
			return;
		}
		Globals.initialized = true;

		window.defined = window.defined || Globals.defined;
	}

	private static defined(x: any): boolean {
		return typeof(x) !== "undefined";
	}
}
