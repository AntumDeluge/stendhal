/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;


/**
 * Class for helping with date-related functions.
 */
public class DateUtil {

	/**
	 * Static class.
	 */
	private DateUtil() {
		// do nothing
	}

	/**
	 * Checks if a date is today.
	 *
	 * @param date
	 *   Date object.
	 * @return
	 *   {@code true} if matches today's date.
	 */
	public static boolean isToday(final LocalDate date) {
		return LocalDate.now().equals(date);
	}

	/**
	 * Checks if a date is today.
	 *
	 * @param date
	 *   Date time object.
	 * @return
	 *   {@code true} if matches today's date.
	 */
	public static boolean isToday(final LocalDateTime date) {
		return isToday(date.toLocalDate());
	}

	/**
	 * Checks if a date is today.
	 *
	 * @param yyyy
	 *   Year of date.
	 * @param mm
	 *   Month of date.
	 * @param dd
	 *   Day of date.
	 * @return
	 *   {@code true} if matches today's date.
	 */
	public static boolean isToday(final int yyyy, final int mm, final int dd) {
		return isToday(LocalDate.of(yyyy, mm, dd));
	}

	/**
	 * Checks if a date is today of this year.
	 *
	 * @param mm
	 *   Month of date.
	 * @param dd
	 *   Day of date.
	 * @return
	 *   {@code true} if matches today's date.
	 */
	public static boolean isToday(final int mm, final int dd) {
		return isToday(LocalDate.now().getYear(), mm, dd);
	}

	/**
	 * Checks if a string represents today's day of the week.
	 *
	 * @param name
	 *   Name of day.
	 * @return
	 *   {@code true} if {@code name} matches today.
	 */
	public static boolean isDayOfWeek(final String name) {
		final DayOfWeek today = LocalDate.now().getDayOfWeek();
		return today.getDisplayName(TextStyle.FULL_STANDALONE, Locale.ENGLISH).equalsIgnoreCase(name);
	}
}
