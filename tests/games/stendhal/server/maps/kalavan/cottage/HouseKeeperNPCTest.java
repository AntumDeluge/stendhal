/* $Id$ */
/***************************************************************************
 *                     (C) Copyright 2011 - Stendhal                       *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.kalavan.cottage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

/**
 * Test buying ice cream.
 *
 * @author Martin Fuchs
 */
public class HouseKeeperNPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "0_kalavan_city_gardens";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		setupZone(ZONE_NAME, new HouseKeeperNPC());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public HouseKeeperNPCTest() {
		super(ZONE_NAME, "Granny Graham");
	}

	/**
	 * Tests for hiAndBye.
	 */
	@Test
	public void testHiAndBye() {
		final SpeakerNPC npc = getNPC("Granny Graham");
		assertNotNull(npc);
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hello"));
		assertEquals("Hello, dear.", getReply(npc));

		assertTrue(en.step(player, "bye"));
		assertEquals("Bye now.", getReply(npc));
	}

	/**
	 * Tests for MakeTea.
	 */
	@Test
	public void testMakeTea() {
		final SpeakerNPC npc = getNPC("Granny Graham");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi"));
		assertEquals("Hello, dear.", getReply(npc));

		assertTrue(en.step(player, "job"));
		assertEquals("I'm the housekeeper here. I can #brew you a nice cup of #tea, if you like.", getReply(npc));

		assertTrue(en.step(player, "offer"));
		assertEquals("I will #brew you a hot cup of #tea, if you like.", getReply(npc));

		assertTrue(en.step(player, "quest"));
		assertEquals("I have such a headache and little Annie shrieking every time she goes down the slide doesn't help. Maybe you could give her something to keep her occupied? ... like a gag ...", getReply(npc));

		assertTrue(en.step(player, "tea"));
		assertEquals("It's the very best drink of all. I sweeten mine with #honey. Just ask if you'd like a #brew.", getReply(npc));

		assertTrue(en.step(player, "brew"));
		assertEquals("I can only brew a cup of tea if you bring me a #'bottle of milk' and a #'jar of honey'.", getReply(npc));

		assertTrue(en.step(player, "milk"));
		assertEquals("Well my dear, I expect you can get milk from a farm.", getReply(npc));

		assertTrue(en.step(player, "bottle of milk"));
		assertEquals("Well my dear, I expect you can get milk from a farm.", getReply(npc));

		assertTrue(en.step(player, "honey"));
		assertEquals("Don't you know the beekeeper of Fado Forest?", getReply(npc));

		assertTrue(en.step(player, "jar of honey"));
		assertEquals("Don't you know the beekeeper of Fado Forest?", getReply(npc));

		assertFalse(player.isEquipped("tea"));

		PlayerTestHelper.equipWithItem(player, "milk");
		PlayerTestHelper.equipWithItem(player, "honey");

		assertTrue(en.step(player, "brew"));
		assertEquals("I need you to fetch me a #'bottle of milk' and a #'jar of honey' for this job. Do you have it?", getReply(npc));

		assertTrue(en.step(player, "no"));
		assertEquals("OK, no problem.", getReply(npc));

		assertTrue(en.step(player, "brew"));
		assertEquals("I need you to fetch me a #'bottle of milk' and a #'jar of honey' for this job. Do you have it?", getReply(npc));

		assertTrue(en.step(player, "yes"));
		assertEquals("OK, I will brew a cup of tea for you, but that will take some time. Please come back in 3 minutes.", getReply(npc));
		assertFalse(player.isEquipped("tea"));

// wait 3 minutes
		
//		assertTrue(player.isEquipped("tea", 1));

		assertTrue(en.step(player, "bye"));
	}

	/**
	 * Tests for buying.
	 */
	@Test
	public void testBuy() {
		final SpeakerNPC npc = getNPC("Granny Graham");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Granny Graham"));
		assertEquals("Hello, dear.", getReply(npc));

		// Currently there are no response to sell sentences for Granny Graham.
		assertFalse(en.step(player, "buy"));
	}

	/**
	 * Tests for selling.
	 */
	@Test
	public void testSell() {
		final SpeakerNPC npc = getNPC("Granny Graham");
		final Engine en = npc.getEngine();

		assertTrue(en.step(player, "hi Granny Graham"));
		assertEquals("Hello, dear.", getReply(npc));

		// Currently there are no response to sell sentences for Granny Graham.
		assertFalse(en.step(player, "sell"));
	}

}
