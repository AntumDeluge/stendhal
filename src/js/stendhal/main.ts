/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Client } from "./Client";

import { Globals } from "./util/Globals";

declare var stendhal: any;


// entry point
(function() {
	// initialize "stendhal" object
	Globals.init();
	stendhal.main = Client.get();
	stendhal.main.init();

	document.addEventListener('DOMContentLoaded', stendhal.main.startup);
	window.addEventListener('error', stendhal.main.onerror);
})();
