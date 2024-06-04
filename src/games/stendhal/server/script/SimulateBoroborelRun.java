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
package games.stendhal.server.script;

import java.util.List;
import java.util.concurrent.TimeUnit;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.scripting.AbstractAdminScript;
import games.stendhal.server.entity.pet.boroborel.Boroborel;
import games.stendhal.server.entity.pet.boroborel.BoroborelPet;


public class SimulateBoroborelRun extends AbstractAdminScript {

	@Override
	protected void run(final List<String> args) {
		// TODO Auto-generated method stub
		final BoroborelPet pet = new BoroborelPet("boro");
		pet.put("strength", 50);
		pet.put("vigor", 50);
		pet.put("stamina", 50);
		pet.put("intelligence", 50);
		pet.put("durability", 50);
		pet.put("temperament", 50);
		pet.put("luck", 50);
		pet.put("appearance", 50);
		final Boroborel runner = new Boroborel(pet);
		runner.listenForFinish(new Runnable() {
			@Override
			public void run() {
				final long t = runner.getFinishTime();
				String msg = "\nStart time: " + Boroborel.getStartTime();
				msg += "\nElapsed: " + t + " (" + SimulateBoroborelRun.formatTime(t) + ")";
				msg += "\nTurns: " + runner.getTotalTurns();
				System.out.println(msg);
				admin.sendPrivateText(msg);
			}
		});
		runner.setDebugLogic(new Runnable() {
			@Override
			public void run() {
				String msg = "\nturn: " + runner.getTotalTurns();
				msg += "\nspeed: " + runner.getCurrentSpeed();
				msg += "\naccelerating: " + runner.isAccelerating();
				msg += "\ndistance ran: " + runner.getDistanceRan();
				msg += "\nenergy: " + runner.getEnergy();
				msg += "\nexhausted: " + runner.isExhausted();
				System.out.println(msg);
			}
		});

		// start "run"
		if (args.size() > 0) {
			Boroborel.setRaceDistance(MathHelper.parseIntDefault(args.get(0), Boroborel.getRaceDistance()));
		}
		final String msg = "Starting run at timestamp " + System.currentTimeMillis() + ", distance: " + Boroborel.getRaceDistance();
		System.out.println(msg);
		admin.sendPrivateText(msg);
		//SingletonRepository.getTurnNotifier().notifyInTurns(1, runner);
		runner.onTurnReached(0);
	}

	private static String formatTime(final long millis) {
		final long h = TimeUnit.MILLISECONDS.toHours(millis);
		final long m = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
		final long s = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;

		return String.format("%02dh %02dm %02ds", h, m, s);
	}
}
