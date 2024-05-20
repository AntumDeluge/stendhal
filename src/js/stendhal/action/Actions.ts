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


/**
 * Action types.
 *
 * Should correspond with `games.stendhal.common.constants.Actions`.
 */
export enum Actions {

	// *** general *** //


	// *** client specific *** /

	// help query
	ABOUT = "about",
	HELP = "help",
	GMHELP = "gmhelp",
	SETTINGS = "settings",

	/*
	ACTION = "action",

	// social
	BUDDYONLINE = "1",
	BUDDY_OFFLINE = "0",
	*/

	// *** user *** //

	/*
	// chat
	ANSWER = "answer",
	CHAT = "chat",
	EMOTE = "emote",
	GROUP_MESSAGE = "group_message",
	REPORT_ERROR = "report_error",
	SUPPORT = "support",
	TELL = "tell",

	// push
	PUSH = "push",

	// player houses
	KNOCK = "knock",

	// pets
	OWN = "own",
	SPECIES = "species",
	FORSAKE = "forsake",
	PET = "pet",
	SHEEP = "sheep",
	NAME = "name",
	*/

	// travel log
	PROGRESS_STATUS = "progressstatus"

	/*
	// player movement
	MOVETO = "moveto",
	DIR = "dir",
	FACE = "face",

	// player sentence
	SENTENCE = "sentence",

	// players query
	WHERE = "where",
	WHO = "who",

	// social
	AWAY = "away",
	GRUMPY = "grumpy",
	ADDBUDDY = "addbuddy",
	REMOVEBUDDY = "removebuddy",
	IGNORE = "ignore",
	UNIGNORE = "unignore",
	REASON = "reason",
	DURATION = "duration",

	// look action
	LOOK = "look",

	// move action
	MOVE = "move",

	// abilities
	ATTACK = "attack",
	CASTSPELL = "cast_spell",

	VALUE = "value",
	MODE = "mode",
	STAT = "stat",

	// for listing e.g. ignore list
	LIST = "list",

	TARGET = "target",
	TARGET_PATH = "target_path",
	BASESLOT = "baseslot",
	BASEOBJECT = "baseobject",
	BASEITEM = "baseitem",
	USE = "use",
	TYPE = "type",
	X = "x",
	Y = "y",
	MINUTES = "minutes",
	MESSAGE = "message",
	TEXT = "text",

	READ = "read",
	LOOK_CLOSELY = "look_closely",
	LANGUAGE = "language",

	// player movement/control
	WALK = "walk",
	STOPWALK = "stopwalk",
	// indicates player is using auto-walk
	AUTOWALK = "autowalk",
	// indicates player is using continuous movement
	// TODO: rename to "ontransfer_cont"
	MOVE_CONTINUOUS = MOVE_CONTINUOUS_PROPERTY,
	COND_STOP = "conditional_stop",

	BESTIARY = "bestiary",

	// PvP
	ACCEPT = "accept",
	CHALLENGE = "challenge",

	// commerce
	SHOP_INVENTORY = "shop_inventory",


	// *** admin/GM *** //

	// chat
	TELLALL = "tellall",
	SUPPORTANSWER = "supportanswer",

	// server query
	LISTQUESTS = "listquests",
	LISTPRODUCERS = "listproducers",
	CSTATUS = "cstatus",
	CID = "cid",
	ID = "id",
	CIDLIST = "cidlist",

	// entity attributes query
	INSPECT = "inspect",
	INSPECTQUEST = "inspectquest",
	INSPECTKILL = "inspectkill",

	// entity attributes manipulation
	ALTER = "alter",
	UNSET = "unset",
	OUTFIT = "outfit_ext",
	REMOVEDETAIL = "removedetail",
	ALTERCREATURE = "altercreature",
	ATTR_HP = "hp",
	SUB = "sub",
	ADD = "add",
	SET = "set",
	TITLE = "title",
	ADMINLEVEL = "adminlevel",
	NEWLEVEL = "newlevel",

	// player specific attributes manipulation
	ALTERKILL = "alterkill",
	ALTERQUEST = "alterquest",

	// ghostmode
	INVISIBLE = "invisible",
	GHOSTMODE = "ghostmode",

	// teleport
	ZONE = "zone",
	TELEPORT = "teleport",
	TELEPORTTO = "teleportto",
	TELECLICKMODE = "teleclickmode",

	// item & creature summoning
	CREATURE = "creature",
	SUMMON = "summon",
	SUMMONAT = "summonat",
	AMOUNT = "amount",
	ITEM = "item",
	SLOT = "slot",

	// player discipline
	JAIL = "jail",
	GAG = "gag",
	BAN = "ban"
	*/
}
