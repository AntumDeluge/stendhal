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
	interface Window {
		stendhal: any;
	}
}


export class Globals {

	/** Property denoting state of class initialization. */
	private static initialized = false;


	/**
	 * Static class.
	 */
	private constructor() {
		// do nothing
	}

	static init() {
		if (Globals.initialized) {
			console.warn("Tried to re-initialize globals class");
			return;
		}
		Globals.initialized = true;

		//window.marauroa = window.marauroa || {}; // marauroa object should already be intialized
		window.stendhal = window.stendhal || {};
	}
}
