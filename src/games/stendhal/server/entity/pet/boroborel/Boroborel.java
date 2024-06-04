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
package games.stendhal.server.entity.pet.boroborel;

import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.Entity;


/**
 * A pet class players can raise for entering in the Boroborel races.
 *
 * This version is for creating entity instances to race. {@code BoroborelPet} is the overworld
 * version for players to interact with while raising.
 *
 * TODO: use separate class for managing all racers
 */
public class Boroborel extends Entity implements TurnListener {

	private final String title;
	private int age;
	private final int strength;
	private final int vigor;
	private final int stamina;
	private final int intelligence;
	private final int durability;
	private final int temperament;
	private final int luck;

	/** Number of turns in this race. */
	private int racingTurns;
	/** Number of turns in which age will increment. */
	private int agingTurns;
	/** Represents distance entity has run on track (0 = not started, 100 = finished). */
	private int distanceRan;
	/** Value between 0-100 determining entity's current speed. */
	private int currentSpeed;
	/** Speed at which entity is considered to be in full sprint and no longer accelerating. */
	private final int sprintThreshold;
	/** Entity's absolute top speed. */
	private final int sprintMax;
	/** Determines average increase in acceleration speed. */
	private final int accelerationRate;

	/** Denotes whether this entity has started running. */
	private boolean started;
	/** Determines how tired the entity is. */
	private int energy;
	/** Number of turns for entity to recover from exhaustion. */
	private short turnsToRecover;
	/** Tracks when entity can recover from exhaustion. */
	private int recoverAtTurn;

	/** Timestamp at which entity crossed finish line. */
	private Long finishTime;

	private Runnable finishListener;
	private Runnable debugLogic;

	/** Determines if race has started. */
	//private static boolean started = false;
	/** Timestamp at which race started. */
	private static long startTime = 0;
	/** Determines how long race is. */
	private static int raceDistance = 10000;


	public Boroborel(final BoroborelPet pet) {
		title = pet.getTitle();
		age = MathHelper.parseIntDefault(pet.get("age"), 0);
		strength = MathHelper.parseIntDefault(pet.get("strength"), 0);
		vigor = MathHelper.parseIntDefault(pet.get("vigor"), 0);
		stamina = MathHelper.parseIntDefault(pet.get("stamina"), 0);
		intelligence = MathHelper.parseIntDefault(pet.get("intelligence"), 0);
		durability = MathHelper.parseIntDefault(pet.get("durability"), 0);
		temperament = MathHelper.parseIntDefault(pet.get("temperament"), 0);
		luck = MathHelper.parseIntDefault(pet.get("luck"), 0);

		racingTurns = 0;
		agingTurns = (int) Math.max(5, Math.floor(durability / 2));
		distanceRan = 0;
		currentSpeed = 0;
		// TODO: use vigor as factor to influence sprint threshold & top speed (should not be more than sprintMax)
		sprintThreshold = 5;
		sprintMax = Math.max(100, sprintThreshold + 10);
		// TODO: use strength as factor to influence acceleration rate
		accelerationRate = 1;

		energy = 100;
		turnsToRecover = 20;
		recoverAtTurn = 0;
	}

	/**
	 * Handles behavior during racing.
	 */
	public void logic() {
		// DEBUG:
		//System.out.println("executing logic ...");

		if (hasFinished()) {
			onFinish();
			return;
		}

		/*
		if (!isRacing()) {
			return;
		}
		*/
		racingTurns++;
		if (racingTurns % agingTurns == 0) {
			// ages during races
			age++;
		}

		// TODO: influence energy loss with vigor factor
		if (!isExhausted() && racingTurns % 5 == 0) {
			energy--;
		}

		final int rf = getRandomFactor();

		final boolean exhausted = isExhausted();
		if (exhausted) {
			if (recoverAtTurn < racingTurns) {
				// entity just entered exhaustion
				recoverAtTurn = racingTurns + turnsToRecover;
				currentSpeed = 1;
			} else if (racingTurns >= recoverAtTurn) {
				// recovery
				// TODO: use a factor to influence recovery amount
				energy = 50;
			}
		} else if (isAccelerating()) {
			// FIXME: should speed increase every turn?
			currentSpeed += Math.max(1, accelerationRate + rf);
		} else {
			// FIXME: should speed be influenced every turn?
			currentSpeed = Math.max(sprintThreshold, Math.min(sprintMax, currentSpeed + rf));
		}

		// FIXME: should distanced ran be updated every turn?
		distanceRan += currentSpeed;

		// DEBUG:
		//System.out.println("distance ran: " + distanceRan);

		if (debugLogic != null) {
			debugLogic.run();
		}

		SingletonRepository.getTurnNotifier().notifyInTurns(1, this);
	}

	public boolean isRacing() {
		return startTime > 0 && !hasFinished();
	}

	public boolean isAccelerating() {
		return !isExhausted() && currentSpeed < sprintThreshold;
	}

	public int getCurrentSpeed() {
		return currentSpeed;
	}

	public boolean isTired() {
		return energy < 11;
	}

	public boolean isExhausted() {
		return energy < 1;
	}

	public int getEnergy() {
		return energy;
	}

	public int getDistanceRan() {
		return distanceRan;
	}

	public boolean hasFinished() {
		return distanceRan >= raceDistance;
	}

	private void onStart() {
		// starting speed and acceleration is influenced by strength
		currentSpeed = (int) Math.max(1, Math.floor(strength / 4));
		// apply a bit of randomness
		final int rf = getRandomFactor();
		currentSpeed += Rand.randUniform(-rf, rf);
		started = true;
		if (startTime == 0) {
			// race has started for all racers
			startTime = System.currentTimeMillis();
		}
	}

	private void onFinish() {
		finishTime = System.currentTimeMillis() - startTime;
		SingletonRepository.getTurnNotifier().dontNotify(this); // doesn't work
		if (finishListener != null) {
			finishListener.run();
		}
	}

	public int getTotalTurns() {
		return racingTurns;
	}

	public Long getFinishTime() {
		return finishTime;
	}

	public static Long getStartTime() {
		return startTime;
	}

	public void listenForFinish(final Runnable finishListener) {
		this.finishListener = finishListener;
	}

	public void setDebugLogic(final Runnable listener) {
		debugLogic = listener;
	}

	public boolean outran(final Boroborel other) {
		if (finishTime == null) {
			return false;
		}
		final Long otherFinishTime = other.getFinishTime();
		if (otherFinishTime == null) {
			return true;
		}
		if (finishTime == otherFinishTime) {
			// NOTE: maybe not a good idea?
			return distanceRan > other.getDistanceRan();
		}
		return finishTime < otherFinishTime;
	}

	public static void setRaceDistance(final int distance) {
		raceDistance = distance;
	}

	public static int getRaceDistance() {
		return raceDistance;
	}

	private int getRandomFactor() {
		// TODO: influence with intelligence and/or luck
		return 5;
	}

	@Override
	public void onTurnReached(int currentTurn) {
		if (!started) {
			onStart();
		}
		this.logic();
		//SingletonRepository.getTurnNotifier().notifyInTurns(1, this);
	}
}
