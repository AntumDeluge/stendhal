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

declare var marauroa: any;

import { ClientActionImpl } from "./ClientActionImpl";


/**
 * Compatible object for forwarding action info to server.
 */
interface RelayActionData extends Record<string, string> {
	/** Action type name/identifier. Required by server. */
	type: string
}


/**
 * Implementation for forwarding action request to server.
 */
export abstract class RelayActionImpl extends ClientActionImpl {

	/** Action type name/identifier. **/
	abstract type: string;


	/**
	 * Executes the action.
	 *
	 * TODO:
	 * - rename & replace `RelayActionImpl.execute`
	 *
	 * @param {string[]} params
	 *   Action parameters.
	 * @param {string} remainder
	 *   Any remaining data resulting resulting from command parameter parsing.
	 * @returns {boolean}
	 *   `true` to represent successful execution.
	 */
	abstract executeInterim(params: string[], remainder: string): boolean;

	/**
	 * @deprecated
	 */
	execute(_type: string, params: string[], remainder=""): boolean {
		console.debug("Deprecated `RelayActionImpl.execute`");
		// "type" is implied with `RelayActionImpl.type` attrbute
		return this.executeInterim(params, remainder);
	}

	/**
	 * Forwards action information to server.
	 *
	 * @param action {RelayActionData}
	 *   Action object.
	 */
	protected send(action: RelayActionData) {
		marauroa.clientFramework.sendAction(action);
	}
}
