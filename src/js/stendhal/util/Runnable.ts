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

import { FunctionImpl } from "./FunctionImpl";


/**
 * A callable class.
 */
export class Runnable extends FunctionImpl<Array<any>> {

	/** Called to execute predefined instructions. */
	private readonly onRun: Function;


	/**
	 * Creates a new runner.
	 *
	 * @param {Array<any>} args
	 *   Parameters passed to the constructor.
	 */
	constructor(...args: Array<any>) {
		super(args);
		// TODO:
		this.onRun = new Function();
	}

	/**
	 * Called to execute predefined instructions.
	 */
	run() {
		// DEBUG:
		console.log("Runnable.run");

		this.onRun(this.args);
	}
}
