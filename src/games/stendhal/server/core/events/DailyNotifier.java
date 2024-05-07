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
package games.stendhal.server.core.events;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

import games.stendhal.common.MathHelper;
import games.stendhal.server.util.TimeUtil;


/**
 * Sends a notification once per every real day.
 */
public class DailyNotifier implements TurnListener {

	/** Registered listeners to be notified at new day event. */
	private final Set<DailyListener> listeners;

	/** Seconds buffer to add to notification delay. */
	private static final int TIME_BUFFER = 60;

	/** Singleton instance. */
	private static DailyNotifier instance;


	/**
	 * Retrieves singleton instance.
	 */
	public static DailyNotifier get() {
		if (instance == null) {
			instance = new DailyNotifier();
		}
		return instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private DailyNotifier() {
		listeners = Sets.newHashSet();
		// start notification cycle
		final int notifyAtSeconds = getSecondsToNextNotification();
		TurnNotifier.get().notifyInSeconds(notifyAtSeconds, this);
		debugMessage(notifyAtSeconds - TIME_BUFFER);
	}

	/**
	 * Gets the number of seconds to wait before next notification is sent.
	 *
	 * @return
	 *   Seconds to next day notification.
	 */
	private int getSecondsToNextNotification() {
		// add a minute buffer to guarantee date accuracy
		return TimeUtil.secondsToMidnight() + TIME_BUFFER;
	}

	/**
	 * Adds listener to notification list.
	 *
	 * @param listener
	 *   Listener instance to be added.
	 */
	public void add(final DailyListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes listener from notification list.
	 *
	 * @param listener
	 *   Listener instance to be removed.
	 */
	public void remove(final DailyListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Notify listeners of new day.
	 */
	private void notifyListeners() {
		final LocalDate today = LocalDate.now();
		// NOTE: should this be done asynchronously?
		final Iterator<DailyListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			iter.next().onNewDay(today);
		}
	}

	@Override
	public void onTurnReached(final int currentTurn) {
		notifyListeners();
		// restart for next day
		// use actual time instead of static seconds in one day to adjust for potential drift in case
		// not called exactly in 24 hour increments
		final int notifyAtSeconds = getSecondsToNextNotification();
		TurnNotifier.get().notifyInSeconds(notifyAtSeconds, this);
		debugMessage(notifyAtSeconds - TIME_BUFFER);
	}

	/**
	 * Displays a message for debugging.
	 *
	 * @param secondsToMidnight
	 *   Number of seconds before midnight hour.
	 */
	private void debugMessage(final int secondsToMidnight) {
		final Logger logger = Logger.getLogger(DailyNotifier.class);
		if (logger.isDebugEnabled()) {
			logger.debug("Date: " + LocalDate.now().toString());
			logger.debug("Time: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
			int secs = secondsToMidnight;
			final int days = Math.max(0, secs / MathHelper.SECONDS_IN_ONE_DAY);
			secs %= MathHelper.SECONDS_IN_ONE_DAY;
			final int hours = Math.max(0, secs / MathHelper.SECONDS_IN_ONE_HOUR);
			secs %= MathHelper.SECONDS_IN_ONE_HOUR;
			final int mins = Math.max(0, secs / MathHelper.SECONDS_IN_ONE_MINUTE);
			secs %= MathHelper.SECONDS_IN_ONE_MINUTE;
			final String msg = "Time until midnight: " + days + "d " + hours + "h " + mins + "m "
					+ secs + "s";
			logger.debug(msg);
		}
	}
}
