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

import { Color } from "../data/color/Color";


/**
 * Hidden canvas for manipulating images before they are displayed.
 */
export class DrawingStage {

	/** Hidden canvas element. */
	private canvas: HTMLCanvasElement;
	/** Canvas's drawing context. */
	private ctx: any;
	/** Property denoting WebGL support from browser. */
	private gl: boolean;

	/** Singleton instance. */
	private static instance: DrawingStage;


	/**
	 * Retrieves the singleton instance.
	 *
	 * @returns {DrawingStage}
	 *   Drawing stage instance.
	 */
	static get(): DrawingStage {
		if (!DrawingStage.instance) {
			DrawingStage.instance = new DrawingStage();
		}
		return DrawingStage.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		this.gl = false;
		this.canvas = document.getElementById("drawing-stage")! as HTMLCanvasElement;
		this.ctx = this.canvas.getContext("2d")!;
		/*
		this.ctx = this.canvas.getContext("webgl");
		if (!this.ctx) {
			console.warn("WebGL not supported, falling back to standard 2d context");
			this.gl = false;
			this.ctx = this.canvas.getContext("2d")!;
		} else {
			// DEBUG:
			console.log("Using WebGL");

			this.gl = true;
		}
		*/
		this.reset();
	}

	/**
	 * Clears canvas & resets dimensions to 0x0.
	 */
	reset() {
		if (this.gl) {
			// TODO:
		} else {
			this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
		}
		this.setSize(0, 0);
	}

	/**
	 * Sets dimensions of drawing canvas.
	 *
	 * @param {number} width
	 *   New canvas width.
	 * @param {number} height
	 *   New canvas height.
	 */
	setSize(width: number, height: number) {
		this.canvas.width = width;
		this.canvas.height = height;
	}

	/**
	 * Compares canvas dimensions against image and adjusts if necessary.
	 *
	 * @param {HTMLImageElement} image
	 *   Image to compare against.
	 */
	private checkSize(image: HTMLImageElement) {
		if (this.canvas.width < image.width || this.canvas.height < image.height) {
			this.setSize(image.width, image.height);
		}
	}

	/**
	 * Converts canvas data to PNG data URL.
	 *
	 * @returns {string}
	 *   PNG data encoded string.
	 */
	toDataURL(): string {
		return this.canvas.toDataURL("image/png");
	}

	/**
	 * Converts canvas data to PNG image.
	 *
	 * @returns {HTMLImageElement}
	 *   New image element.
	 */
	toImage(): HTMLImageElement {
		const image = new Image();
		image.src = this.toDataURL();
		return image;
	}

	/**
	 * Draws an image on the canvas.
	 *
	 * @param {HTMLImageElement} image
	 *   Image to be drawn.
	 * @param {boolean} [reset=true]
	 *   If `true` erases current image data before drawing.
	 */
	drawImage(image: HTMLImageElement, reset=true) {
		if (reset) {
			this.reset();
		}
		this.checkSize(image);
		this.ctx.drawImage(image, 0, 0);
	}

	/**
	 * Draws a rotated image on the canvas.
	 *
	 * NOTE: currently only supports accurate rotation on square image in 90 degree increments
	 *
	 * @param {HTMLImageElement} image
	 *   Image to be drawn.
	 * @param {number} angle
	 *   Desired angle of image rotation.
	 * @param {boolean} [reset=true]
	 *   If `true` erases current image data before drawing.
	 */
	drawImageRotated(image: HTMLImageElement, angle: number, reset=true) {
		if (reset) {
			this.reset();
		}
		this.checkSize(image);
		this.ctx.translate(this.canvas.width / 2, this.canvas.height / 2);
		this.ctx.rotate(angle * Math.PI / 180);
		this.ctx.translate(-this.canvas.width / 2, -this.canvas.height / 2);
		// NOTE: do we need to set canvas size again in case of non-square image?
		this.ctx.drawImage(image, 0, 0);
	}

	/**
	 * Creates a speech bubble image.
	 *
	 * @param {number} width
	 *   Image width.
	 * @param {number} height
	 *   Image height.
	 * @returns {HTMLImageElement}
	 *   New image element.
	 */
	createSpeechBubble(width: number, height: number): HTMLImageElement {
		this.reset();
		this.gl ? this.drawWebGLSpeechBubble(width, height) : this.draw2DSpeechBubble(width, height);
		return this.toImage();
	}

	/**
	 * Draws a speech bubble on canvas.
	 *
	 * @param {number} width
	 *   Canvas width.
	 * @param {number} height
	 *   Canvas height.
	 */
	private draw2DSpeechBubble(width: number, height: number) {
		this.ctx.save();

		// DEBUG:
		console.log("width: " + width + "\nheight: " + height);

		const arc = 3;
		const lineWidth = 1; // width of line on single edge
		this.ctx.lineWidth = lineWidth * 2;
		const tail = 8;
		const x = tail + this.ctx.lineWidth, y = lineWidth;
		width += x; // compensate for tail
		// adjust canvas dimensions to compensate for stroke & tail
		const canvasWidth = width + (lineWidth * 2) + tail + lineWidth;
		const canvasHeight = height + (lineWidth * 2);
		this.setSize(canvasWidth, canvasHeight);

		// DEBUG:
		this.ctx.fillStyle = Color.GREEN;
		this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);

		// DEBUG:
		console.log("width: " + width + "\nheight: " + height + "\nline width: " + lineWidth + "\nctx line width: " + this.ctx.lineWidth);

		const fontsize = 14;
		//~ const lheight = fontsize + 6;
		this.ctx.font = fontsize + "px Arial";
		this.ctx.fillStyle = Color.WHITE;
		this.ctx.strokeStyle = Color.BLACK;

		this.ctx.beginPath();
		this.ctx.moveTo(x + arc, y);
		this.ctx.lineTo(x + width - arc, y);
		this.ctx.quadraticCurveTo(x + width, y, x + width, y + arc);
		this.ctx.lineTo(x + width, y + height - arc);
		this.ctx.quadraticCurveTo(x + width, y + height, x + width - arc, y + height);
		this.ctx.lineTo(x + arc, y + height);
		this.ctx.quadraticCurveTo(x, y + height, x, y + height - arc);
		this.ctx.lineTo(x, y + 8);

		// tail
		this.ctx.lineTo(x - 8, y + 11);
		this.ctx.lineTo(x, y + 3);

		this.ctx.lineTo(x, y + arc);
		this.ctx.quadraticCurveTo(x, y, x + arc, y);
		this.ctx.stroke();
		this.ctx.closePath();
		this.ctx.fill();

		this.ctx.restore();
	}

	/**
	 * Draws a speech bubble on GL canvas.
	 *
	 * TODO: doesn't do anything yet
	 *
	 * @param {number} width
	 *   Canvas width.
	 * @param {number} height
	 *   Canvas height.
	 */
	private drawWebGLSpeechBubble(width: number, height: number) {
		// DEBUG:
		console.log("drawing WebGL");

		// TODO:
	}
}
