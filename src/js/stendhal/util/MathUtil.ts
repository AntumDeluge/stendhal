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

import { Point } from "./Point";


/**
 * Helper class for mathmatical calculations & conversions.
 */
export class MathUtil {

	/** Epsilon value used for coordinate change checks. */
	public static readonly EPSILON = 0.001;

	/**
	 * Compares to floating point values.
	 *
	 * @param d1
	 *            first value
	 * @param d2
	 *            second value
	 * @param diff
	 *            acceptable diff
	 * @return true if they are within diff
	 */
	public static compareDouble(d1: number, d2: number, diff = MathUtil.EPSILON) {
		return Math.abs(d1 - d2) < diff;
	}

	/**
	 * Converts radians to angle of degrees.
	 *
	 * @param rad {number}
	 *   Radians.
	 * @return {number}
	 *   Angle of degrees.
	 */
	static radToDeg(rad: number): number {
		return rad * 180 / Math.PI;
	}

	/**
	 * Converts angle of degrees to radians.
	 *
	 * @param angle {number}
	 *   Angle of degrees.
	 * @return {number}
	 *   Radians.
	 */
	static degToRad(angle: number): number {
		return angle * Math.PI / 180;
	}

	/**
	 * Converts an X/Y coordinate pair to radians.
	 *
	 * @param point {util.Point.Point}
	 *   Point object containing X/Y coordinates.
	 * @param x {number}
	 *   Coordinate on X axis relative to center 0,0.
	 * @param y {number}
	 *   Coordinate on Y axis relative to center 0,0.
	 * @return {number}
	 *   Radians.
	 */
	static pointToRad(point: Point): number;
	static pointToRad(x: number, y: number): number;
	static pointToRad(x: number|Point, y?: number): number {
		if (x instanceof Point) {
			y = x.y;
			x = x.x;
		}
		return Math.atan2(y!, x);
	}

	/**
	 * Converts an X/Y coordinate pair to angle of degrees.
	 *
	 * @param point {util.Point.Point}
	 *   Point object containing X/Y coordinates.
	 * @param x {number}
	 *   Coordinate on X axis relative to center 0,0.
	 * @param y {number}
	 *   Coordinate on Y axis relative to center 0,0.
	 * @return {number}
	 *   Angle of degrees.
	 */
	static pointToDeg(point: Point): number;
	static pointToDeg(x: number, y: number): number;
	static pointToDeg(x: number|Point, y?: number): number {
		if (x instanceof Point) {
			y = x.y;
			x = x.x;
		}
		return MathUtil.radToDeg(MathUtil.pointToRad(x, y!));
	}

	/**
	 * Normalizes number value to positive angle of degrees.
	 *
	 * @param n {number}
	 *   Value to be normalized.
	 * @return {number}
	 *   Angle of degrees.
	 */
	static normDeg(n: number): number {
		// get angle of degrees represented by value (round to 2nd decimal point)
		n = Number((n % 360).toFixed(2)) || 0;
		// normalize to positive value
		// NOTE: JavaScript has signed 0 value so get absolute just for safety
		return Math.abs(n < 0 ? n + 360 : n);
	}

	/**
	 * Converts a number value to hex string.
	 *
	 * @param n {number}
	 *   Number value to be converted.
	 * @param pad {boolean}
	 *   Left pad with zeros for up to six characters (default: `true`).
	 * @param slice {boolean}
	 *   If `true`, strip any characters preceding last six (default: `false`).
	 * @return {string}
	 *   Hexadecimal string representation (excluding "#" prefix).
	 */
	static toHex(n: number, pad=true, slice=false): string {
		// ensure number is unsigned & convert to hexadecimal string
		let h = (n >>> 0).toString(16).toUpperCase();
		if (h.length < 6 && pad) {
			// pad to make at least 6 characters long
			h = ("000000" + h).slice(-6);
		}
		return slice ? h.slice(-6) : h;
	}

	/**
	 * Parses a float value from string.
	 *
	 * @param {string} st
	 *   String to be parsed.
	 * @param {number} d
	 *   Default value if `st` cannot be parsed.
	 * @returns {number}
	 *   Float value.
	 */
	static parseFloatDefault(st: string, d: number): number {
		let i = parseFloat(st);
		if (Number.isNaN(i)) {
			i = d;
		}
		return i;
	}

	/**
	 * Parses an integer value from string.
	 *
	 * @param {string} st
	 *   String to be parsed.
	 * @param {number} d
	 *   Default value if `st` cannot be parsed.
	 * @returns {number}
	 *   Integer value.
	 */
	static parseIntDefault(st: string, d: number): number {
		return Math.floor(MathUtil.parseFloatDefault(st, d));
	}

	/**
	 * Retrieves Euclidean distance between two points.
	 *
	 * @param {number} x1
	 *   X coordinate of first point on 2d plane.
	 * @param {number} y1
	 *   Y coordinate of first point on 2d plane.
	 * @param {number} x2
	 *   X coordinate of second point on 2d plane.
	 * @param {number} y2
	 *   Y coordinate of second point on 2d plane.
	 * @returns {number}
	 *   Distance of one position relative to the other.
	 */
	static getDistance(x1: number, y1: number, x2: number, y2: number): number;

	/**
	 * Retrieves Euclidean distance between two points.
	 *
	 * @param {Point} pos1
	 *   First point on 2d plane.
	 * @param {Point} pos2
	 *   Second point on 2d plane.
	 * @returns {number}
	 *   Distance of one position relative to the other.
	 */
	static getDistance(pos1: Point, pos2: Point): number;

	// implementation method
	static getDistance(p1: number|Point, p2: number|Point, p3?: number, p4?: number): number {
		let numeric = true;
		for (const p of [p1, p2, p3, p4]) {
			if (typeof(p) !== "number") {
				numeric = false;
				break;
			}
		}

		let x1: number, y1: number, x2: number, y2: number;
		if (numeric) {
			x1 = p1 as number;
			y1 = p2 as number;
			x2 = p3 as number;
			y2 = p4 as number;
		} else if (p1 instanceof Point && p2 instanceof Point) {
			x1 = (p1 as Point).x;
			y1 = (p1 as Point).y;
			x2 = (p2 as Point).x;
			y2 = (p2 as Point).y;
		} else {
			throw new Error("Incompatible parameter types");
		}
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}
}
