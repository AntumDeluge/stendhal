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
 * Base implementation for function type classes.
 */
export abstract class FunctionImpl<T extends Array<any>> extends Function {

	protected readonly args: T;


	/**
	 * Creates a new function.
	 *
	 * @param {...T} args
	 *   Parameters passed to the constructor.
	 */
	constructor(...args: T) {
		super();
		this.args = args;
	}

	// DEBUG:
	public debug() {
		console.log();
		const parent = this.getParentClass();
		if (parent === FunctionImpl<T>) {
			console.log("immediate subclass of", parent.name);
		} else {
			console.log("subclass of", parent.name);
		}

		console.log("parent:", this.getParentClass());
		console.log("class:", this.getClass());
		console.log("name:", this.getName());
	}

	/**
	 * Retrieves class type.
	 *
	 * @returns {typeof FunctionImple<T>}
	 *   Class type.
	 */
	getClass(): typeof FunctionImpl<T> {
		return Object.getPrototypeOf(this).constructor;
	}

	/**
	 * Retreives parent class type.
	 *
	 * @returns {typeof FunctionImpl<T>|Function}
	 *   Class type.
	 */
	getParentClass(): typeof FunctionImpl<T>|Function {
		return Object.getPrototypeOf(this.getClass());
	}

	/**
	 * Retrieves the class name.
	 *
	 * @returns {string}
	 *   Name of class instance.
	 */
	getName(): string {
		return Object.getPrototypeOf(this).constructor.name;
	}

	/**
	 * Helpful if compiler not recognizing `FunctionImpl.length`.
	 *
	 * @returns {number}
	 *   Number of arguments passed to constructor.
	 */
	getLength(): number {
		if (this.length > 0) {
			// DEBUG:
			console.log("using " + this.getName() + ".length:", this.length);

			return this.length;
		}
		// DEBUG:
		console.log(this.getName() + ".args:", typeof(this.args));

		// NOTE: is it possible for `this.args` to be `undefined` if inheriting class implements as `FunctionImpl<never[]>`?
		if (!this.args) {
			// DEBUG:
			console.log(this.getName() + ".args empty");

			return 0;
		}

		// DEBUG:
		console.log("using " + this.getName() + ".args.length:", this.args.length);
		return this.args.length;
	}

	/**
	 * Retrieves the parameter values passed to the constructor.
	 *
	 * @returns {Array<any>}
	 *   List of parameters.
	 */
	getArgs(): Array<any> {
		const params: Array<any> = [];
		for (const p of this.args) {
			params.push(p);
		}
		return params;
	}

	/**
	 * Retrieves the parameter types passed to the constructor.
	 *
	 * @returns {Array<string>}
	 *   List of parameter type names.
	 */
	getArgTypes(): Array<string> {
		const ptypes: Array<string> = [];
		for (const p of this.args) {
			ptypes.push(typeof(p));
		}
		return ptypes;
	}

	/**
	 * Retrieves a string representation of this instance.
	 *
	 * @returns {string}
	 *   Representation with class name & constructor parameters.
	 */
	override toString(): string {
		return this.getName() + "(" + this.args.join(", ") + ")";
	}

	/**
	 * Checks if an object is compatible with this instance.
	 *
	 * @param {any} other
	 *   The object to be compared.
	 * @returns {boolean}
	 *   `true` if `other` signature matches.
	 */
	equals(other: any): boolean {
		if (typeof(other) !== typeof(this)) {
			return false;
		}
		// FIXME: how to get arguments from a function that is not an instance of `FunctionImpl<T>`?
		const args = typeof(other.getParams) !== "undefined" ? other.getParams() : [];
		if (args.length !== this.getLength()) {
			return false;
		}
		const argsThis = this.getArgs();
		// FIXME: this will fail if `other` is a function that is not a direct instance of `FunctionImpl<T>`
		/*
		for (let idx = 0; idx < args.length; idx++) {
			if (args[idx] !== argsThis[idx]) {
				return false;
			}
		}
		*/
		// TODO:
		return true;
	}

	/**
	 * Checks if an object is, or is compatible with, an instance of `FunctionImpl<T>` class.
	 *
	 * @param {any} other
	 *   The object to be compared.
	 * @returns {boolean}
	 *   `true` if `other` signature matches `FunctionImpl<T>` class.
	 */
	static isInstance(other: any): boolean {
		if (typeof(other) !== typeof(FunctionImpl)) {
			return false;
		}
		if (other.length !== FunctionImpl.length) {
			return false;
		}
		// TODO:
		return true;
	}
}
