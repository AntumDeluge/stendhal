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

import { Component } from "../toolkit/Component";

import { Outfit } from "../../data/Outfit";

import { Direction } from "../../util/Direction";
import { StringUtil } from "../../util/StringUtil";

declare var stendhal: any;


/**
 * Component to preview an entity's outfit sprite.
 */
export class OutfitPreviewComponent extends Component {

	private dir: Direction;
	private index: number;
	private image?: HTMLImageElement;
	private bgColor?: string;

	/** Determines if current attempt to load image should be cancelled. */
	private cancelRetry: boolean;
	/** Number of times loading image has been attempted. */
	private loadAttempts;


	constructor() {
		super("outfit-preview-template");
		this.dir = Direction.DOWN;
		this.index = 1;
		this.cancelRetry = false;
		this.loadAttempts = 0;
	}

	/**
	 * Sets current outfit for this preview & updates drawing.
	 *
	 * @param {string} outfit
	 *   Outfit formatted string ("body=0,head=2,eyes=1,...).
	 * @param {string=} coloring
	 *   Outfit coloring.
	 * @param {number} [retryDelay=500]
	 *   Milliseconds delay between retries.
	 */
	setOutfit(outfit: string, coloring?: string, retryDelay=500) {
		this.loadAttempts++;
		const otemp = Outfit.build(outfit, coloring);
		otemp.toImage((image?: HTMLImageElement) => {
			if (this.cancelRetry) {
				// cancelled: don't re-attempt to load outfit
				return;
			}
			if (typeof(image) === "undefined") {
				// maximum attempts to load image
				if (this.loadAttempts < 10) {
					console.debug("Re-attempting to load outfit image (tries: " + (this.loadAttempts+1) + ")");
					window.setTimeout(() => {
						if (this.cancelRetry) {
							// cancelled: don't re-attempt to load outfit
							return;
						}
						this.setOutfit(outfit, coloring);
					}, retryDelay);
				} else {
					console.warn("Gave up loading outfit image after " + this.loadAttempts + " attempts");
				}
				return;
			}
			this.image = image;
			this.update();
		});
	}

	/**
	 * Sets facing direction to draw.
	 *
	 * Default is down.
	 *
	 * @param {Direction} dir
	 *   Facing direction.
	 */
	setDirection(dir: Direction) {
		this.dir = dir.val < 1 ? Direction.VALUES[1] : dir.val > 4 ? Direction.VALUES[4] : dir;
		this.update();
	}

	/**
	 * Increments facing direction to draw clockwise.
	 */
	nextDirection() {
		let dir = Direction.VALUES[this.dir.val + 1];
		if (typeof(dir) === "undefined") {
			dir = Direction.VALUES[1];
		}
		this.setDirection(dir);
	}

	/**
	 * Increments facing direction to draw counter-clockwise.
	 */
	prevDirection() {
		let dir = Direction.VALUES[this.dir.val - 1];
		if (typeof(dir) === "undefined" || dir.val === 0) {
			dir = Direction.VALUES[4];
		}
		this.setDirection(dir);
	}

	/**
	 * Sets frame index to draw.
	 *
	 * Default is 1 (center frame).
	 *
	 * @param {number} index
	 *   Frame index.
	 */
	setFrame(index: number) {
		this.index = index < 0 ? 0 : index > 2 ? 2 : index;
		this.update();
	}

	/**
	 * Sets or unsets background color to fill before drawing outfit.
	 *
	 * @param {string|undefined|null} color
	 *   Background color or `undefined|null` or empty string to unset.
	 */
	setBGColor(color: string|undefined|null) {
		if (!color || StringUtil.isEmpty(color)) {
			color = undefined;
		}
		this.bgColor = color;
	}

	/**
	 * Draws outfit in preview area.
	 */
	update() {
		if (!this.image || !this.image.height) {
			console.warn("outfit preview not ready");
			return;
		}
		const w = this.image.width / 3;
		const h = this.image.height / 4;
		const ctx = (this.componentElement as HTMLCanvasElement).getContext("2d")!;
		if (w !== ctx.canvas.width || h !== ctx.canvas.height) {
			console.warn("Image dimensions do not match preview canvas: " + w + "x" + h + " != "
					+ ctx.canvas.width + "x" + ctx.canvas.height);
		}
		if (this.bgColor) {
			ctx.fillStyle = this.bgColor;
			ctx.fillRect(0, 0, ctx.canvas.width, ctx.canvas.height);
		} else {
			ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
		}
		ctx.drawImage(this.image, this.index * w, (this.dir.val - 1) * h, w, h, 0, 0, w, h);
	}

	/**
	 * Marks to abort at next attempt to load outfit image.
	 */
	cancel() {
		this.cancelRetry = true;
	}
}
