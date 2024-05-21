/***************************************************************************
 *                   (C) Copyright 2021-2024 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { ParamList } from "../ParamList";


/**
 * Base slash action implementation.
 *
 * TODO:
 * - check that `maxParams` is handled correctly if set to -1 (no limit)
 */
export abstract class ActionBaseImpl {
	[key: string|symbol]: any;

	/** Minimum required number of parameters. */
	abstract minParams: number;
	/** Maximum allowed number of parameters. */
	abstract maxParams: number;
	/** Parameter definitions for help information. */
	params?: ParamList|string;
	/** Description for help information. */
	desc?: string;
	/** Alternative aliases. */
	aliases?: string[];
	/** Denotes as admin/GM action. */
	admin?: boolean;


	/**
	 * Instructions to be executed for command.
	 *
	 * TODO:
	 * - remove `_type` parameter
	 *
	 * @param {string} _type
	 *   Command name.
	 * @param {string[]} params
	 *   Parameters passed to command.
	 * @param {string} remainder
	 *   Any remaining data after parameters have been parsed.
	 * @return {boolean}
	 *   `true` to represent successful execution.
	 */
	abstract execute(_type: string, params: string[], remainder: string): boolean;
}
