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

declare var marauroa: any;

import { ParamList } from "../ParamList";
import { SlashActionImpl } from "../SlashAction";

import { Pair } from "../../util/Pair";


/**
 * Base class for user actions.
 *
 * TODO:
 * - add parameter checking for client-side-only actions
 */
export abstract class ClientActionImpl extends SlashActionImpl {

	/**
	 * Retrieves help information.
	 *
	 * @param {(ParamList|Pair[])=} params
	 *   Optional parameters info.
	 * @return {string[]}
	 *   Help info for this action.
	 */
	getHelp(params?: ParamList|Pair<string, string>[]): string[] {
		const p: any = params ? new ParamList(params) : this.params;
		const result = [];
		const sparams = p ? ClientActionImpl.formatParams(p) : "";
		if (this.desc) {
			result.push(sparams);
			result.push(this.desc);
		} else if (sparams) {
			result.push(sparams);
		}
		return result;
	}

	/**
	 * Formats parameter list information.
	 *
	 * @param {any} params
	 *   Parameters info.
	 * @param {boolean} [namesOnly=true]
	 *   Format result in single line using parameter names only.
	 * @return {string}
	 *   Parameter formatted string.
	 */
	static formatParams(params: any, namesOnly=true): string {
		// NOTE: can this check be changed to `instanceof SlashActionImpl`?
		if (params instanceof ClientActionImpl) {
			params = params.params;
		}
		if (typeof(params) === "string") {
			return params;
		}
		return new ParamList(params).toString(namesOnly);
	}

	/**
	 * Forwards action information to server.
	 *
	 * @param {object} action
	 *   Action object.
	 */
	protected send(action: object) {
		marauroa.clientFramework.sendAction(action);
	}
}
