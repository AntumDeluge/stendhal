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

import { Runnable } from "./Runnable";


/**
 * Class for executing a type of  `Runnable`.
 */
export class Runner<Runnable> {

	/** Instructions to execute when `Runner.run` is called. */
	readonly run: Runnable;


	/**
	 * Creates a new runnable instance.
	 *
	 * @param run {T}
	 *   Instructions to execute when `Runner.run` is called.
	 */
	constructor(run: Runnable) {
		if (!Runnable.isInstance(run)) {
			throw new Error("`run` must be a function comaptible with `" + Runner + "`");
		}
		this.run = run;
	}
}
