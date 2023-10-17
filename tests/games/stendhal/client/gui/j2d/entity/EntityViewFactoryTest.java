/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.j2d.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import games.stendhal.client.Triple;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.core.rule.defaultruleset.DefaultItem;
import marauroa.common.Log4J;

public class EntityViewFactoryTest {

	private static final Logger logger = Logger.getLogger(EntityViewFactoryTest.class);

	private static final EntityManager em = SingletonRepository.getEntityManager();

	/**
	 * Entity representation.
	 */
	private class EntityRep {
		private final String classname;
		private final String subclass;
		private final String name;
		private final Class implementation;


		private EntityRep(final String classname, final String subclass, final String name,
				final Class implementation) {
			this.classname = classname;
			this.subclass = subclass;
			this.name = name;
			this.implementation = implementation;
		}

		@SuppressWarnings("unused")
		private String getName() {
			return name != null ? name : subclass != null ? subclass : classname;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + EntityViewFactoryTest.this.hashCode();
			result = prime * result + Objects.hash(classname, implementation, name, subclass);
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (!(obj instanceof EntityRep)) {
				return false;
			}
			final EntityRep other = (EntityRep) obj;
			boolean res = classname.equals(other.classname);
			res = res && (subclass == null ? other.subclass == null : subclass.equals(other.subclass));
			res = res && (name == null ? other.name == null : name.equals(other.name));
			//~ res = res && implementation.equals(other.implementation);
			return res;
		}

		@Override
		public String toString() {
			return classname + ", " + subclass + ", " + name + " (" + implementation.getSimpleName() + ")";
		}
	}

	// registered entity views
	private static Map<Triple<String, String, String>, Class<? extends EntityView>> entityViews;

	// registered entities representations to be tested
	private static List<EntityRep> entities;


	@Before
	public void setUp() throws Exception {
		Log4J.init();

		//GameScreen.setDefaultScreen(new MockScreen());

		// populate list of entities to test
		entityViews = EntityViewFactory.getViewMap();
		entities = new ArrayList<>();

		// entities configured in view factory
		for (final Map.Entry<Triple<String, String, String>, Class<? extends EntityView>> entry: entityViews.entrySet()) {
			final Triple<String, String, String> key = entry.getKey();
			entities.add(new EntityRep(key.getFirst(), key.getSecond(), key.getThird(), entry.getValue()));
		}

		// items configured in xml
		for (final DefaultItem item: em.getDefaultItems()) {
			final String class_name = item.getItemClass();
			final String item_name = item.getItemName();
			final String subclass = item.getItemSubclass();
			Class base_implementation = EntityViewFactory.getViewClass("item", class_name, null);
			if (base_implementation == null) {
				base_implementation = getImplementation("item", class_name, null, Item2DView.class);
			}
			Class implementation = getImplementation("item", class_name, item_name, null);
			if (implementation == null) {
				// fallback to lookup via subclass instead of name & default to item class type
				implementation = getImplementation("item", class_name, subclass, base_implementation);
			}

			// item base class
			EntityRep erep = new EntityRep("item", class_name, null, base_implementation);
			if (!entities.contains(erep)) {
				entities.add(erep);
			}

			erep = new EntityRep("item", class_name, item_name, implementation);
			if (!entities.contains(erep) && !entities.contains(new EntityRep("item", class_name, subclass, implementation))) {
				entities.add(erep);
			}
		}
	}

	private Class getImplementation(final String type_name, final String class_name, final String name, final Class def) {
		Class implementation = def;
		final Triple<String, String, String> item_info = new Triple<>(type_name, class_name, name);
		// attempt to retrieve from factory
		if (entityViews.containsKey(item_info)) {
			implementation = entityViews.get(item_info);
		}
		return implementation;
	}

	/**
	 * Tests for all entity views.
	 */
	@Test
	public void testAll() {

		/* *** ITEMS LOADED FROM XML *** */

		checkImplementation("item", "armor", null, Item2DView.class);
		checkImplementation("item", "armor", "blue armor", Item2DView.class);
		checkImplementation("item", "armor", "dress", Item2DView.class);
		checkImplementation("item", "armor", "dwarvish armor", Item2DView.class);
		checkImplementation("item", "armor", "elvish armor", Item2DView.class);
		checkImplementation("item", "armor", "golden armor", Item2DView.class);
		checkImplementation("item", "armor", "leather armor", Item2DView.class);
		checkImplementation("item", "armor", "mainio armor", Item2DView.class);
		checkImplementation("item", "armor", "mithril armor", Item2DView.class);
		checkImplementation("item", "armor", "pauldroned iron cuirass", Item2DView.class);
		checkImplementation("item", "armor", "pauldroned leather cuirass", Item2DView.class);
		checkImplementation("item", "armor", "red armor", Item2DView.class);
		checkImplementation("item", "armor", "xeno armor", Item2DView.class);

		checkImplementation("item", "axe", null, Item2DView.class);
		checkImplementation("item", "axe", "durin axe", Item2DView.class);
		checkImplementation("item", "axe", "halberd", Item2DView.class);

		checkImplementation("item", "boots", null, Item2DView.class);
		checkImplementation("item", "boots", "blue boots", Item2DView.class);
		checkImplementation("item", "boots", "elvish boots", Item2DView.class);
		checkImplementation("item", "boots", "golden boots", Item2DView.class);
		checkImplementation("item", "boots", "leather boots", Item2DView.class);
		checkImplementation("item", "boots", "mainio boots", Item2DView.class);
		checkImplementation("item", "boots", "mithril boots", Item2DView.class);
		checkImplementation("item", "boots", "red boots", Item2DView.class);
		checkImplementation("item", "boots", "xeno boots", Item2DView.class);

		checkImplementation("item", "book", null, Item2DView.class);
		checkImplementation("item", "book", "black book", Item2DView.class);
		checkImplementation("item", "book", "blue book", Item2DView.class);

		checkImplementation("item", "box", "basket", Box2DView.class);
		checkImplementation("item", "box", "present", Box2DView.class);
		checkImplementation("item", "box", "stocking", Box2DView.class);

		checkImplementation("item", "cloak", null, Item2DView.class);
		checkImplementation("item", "cloak", "blue elf cloak", Item2DView.class);
		checkImplementation("item", "cloak", "blue striped cloak", Item2DView.class);
		checkImplementation("item", "cloak", "bone dragon cloak", Item2DView.class);
		checkImplementation("item", "cloak", "dwarf cloak", Item2DView.class);
		checkImplementation("item", "cloak", "elf cloak", Item2DView.class);
		checkImplementation("item", "cloak", "elvish cloak", Item2DView.class);
		checkImplementation("item", "cloak", "golden cloak", Item2DView.class);
		checkImplementation("item", "cloak", "lich cloak", Item2DView.class);
		checkImplementation("item", "cloak", "mainio cloak", Item2DView.class);
		checkImplementation("item", "cloak", "mithril cloak", Item2DView.class);
		checkImplementation("item", "cloak", "red cloak", Item2DView.class);
		checkImplementation("item", "cloak", "stone cloak", Item2DView.class);
		checkImplementation("item", "cloak", "xeno cloak", Item2DView.class);

		checkImplementation("item", "club", null, Item2DView.class);
		checkImplementation("item", "club", "flail", Item2DView.class);
		checkImplementation("item", "club", "golden hammer", Item2DView.class);
		checkImplementation("item", "club", "hammer", Item2DView.class);
		checkImplementation("item", "club", "ice war hammer", Item2DView.class);
		checkImplementation("item", "club", "rod of the gm", Item2DView.class);
		checkImplementation("item", "club", "ugmash", Item2DView.class);

		checkImplementation("item", "container", "empty sack", StackableItem2DView.class);

		checkImplementation("item", "crystal", null, Item2DView.class);
		checkImplementation("item", "crystal", "blue emotion crystal", Item2DView.class);
		checkImplementation("item", "crystal", "pink emotion crystal", Item2DView.class);
		checkImplementation("item", "crystal", "purple emotion crystal", Item2DView.class);
		checkImplementation("item", "crystal", "red emotion crystal", Item2DView.class);
		checkImplementation("item", "crystal", "yellow emotion crystal", Item2DView.class);

		checkImplementation("item", "documents", null, Item2DView.class);
		checkImplementation("item", "documents", "assassins id", Item2DView.class);
		checkImplementation("item", "documents", "closed envelope", Item2DView.class);
		checkImplementation("item", "documents", "coupon", Item2DView.class);
		checkImplementation("item", "documents", "map", Item2DView.class);
		checkImplementation("item", "documents", "note", Item2DView.class);
		checkImplementation("item", "documents", "opened envelope", Item2DView.class);
		checkImplementation("item", "documents", "sealed envelope", Item2DView.class);
		checkImplementation("item", "documents", "unsealed envelope", Item2DView.class);

		checkImplementation("item", "drink", "antidote", UseableItem2DView.class);
		checkImplementation("item", "drink", "cobra venom", UseableItem2DView.class);
		checkImplementation("item", "drink", "deadly poison", UseableItem2DView.class);
		checkImplementation("item", "drink", "disease poison", UseableItem2DView.class);
		checkImplementation("item", "drink", "fish soup", UseableItem2DView.class);
		checkImplementation("item", "drink", "greater antidote", UseableItem2DView.class);
		checkImplementation("item", "drink", "greater potion", UseableItem2DView.class);
		checkImplementation("item", "drink", "love potion", UseableItem2DView.class);
		checkImplementation("item", "drink", "mega potion", UseableItem2DView.class);
		checkImplementation("item", "drink", "minor potion", UseableItem2DView.class);
		checkImplementation("item", "drink", "pina colada", UseableItem2DView.class);
		checkImplementation("item", "drink", "potion", UseableItem2DView.class);
		checkImplementation("item", "drink", "sedative", UseableItem2DView.class);
		checkImplementation("item", "drink", "soup", UseableItem2DView.class);
		checkImplementation("item", "drink", "tea", UseableItem2DView.class);
		checkImplementation("item", "drink", "twilight elixir", UseableItem2DView.class);
		checkImplementation("item", "drink", "water", UseableItem2DView.class);

		checkImplementation("item", "flower", "daisies", StackableItem2DView.class);
		checkImplementation("item", "flower", "lilia", StackableItem2DView.class);
		checkImplementation("item", "flower", "pansy", StackableItem2DView.class);
		checkImplementation("item", "flower", "rhosyd", StackableItem2DView.class);
		checkImplementation("item", "flower", "rose", StackableItem2DView.class);
		checkImplementation("item", "flower", "zantedeschia", StackableItem2DView.class);

		checkImplementation("item", "food", "apple", UseableItem2DView.class);
		checkImplementation("item", "food", "apple pie", UseableItem2DView.class);
		//~ checkImplementation("item", "food", "avocado", UseableItem2DView.class);
		checkImplementation("item", "food", "banana", UseableItem2DView.class);
		checkImplementation("item", "food", "black apple", UseableItem2DView.class);
		checkImplementation("item", "food", "butter", UseableItem2DView.class);
		checkImplementation("item", "food", "carrot", UseableItem2DView.class);
		checkImplementation("item", "food", "cauliflower", UseableItem2DView.class);
		checkImplementation("item", "food", "char", UseableItem2DView.class);
		checkImplementation("item", "food", "cheeseydog", UseableItem2DView.class);
		checkImplementation("item", "food", "chocolate bar", UseableItem2DView.class);
		checkImplementation("item", "food", "clownfish", UseableItem2DView.class);
		checkImplementation("item", "food", "cod", UseableItem2DView.class);
		checkImplementation("item", "food", "collard", UseableItem2DView.class);
		checkImplementation("item", "food", "corn", UseableItem2DView.class);
		checkImplementation("item", "food", "courgette", UseableItem2DView.class);
		checkImplementation("item", "food", "crepes suzette", UseableItem2DView.class);
		checkImplementation("item", "food", "fish pie", UseableItem2DView.class);
		checkImplementation("item", "food", "gumdrops", UseableItem2DView.class);
		checkImplementation("item", "food", "ham", UseableItem2DView.class);
		checkImplementation("item", "food", "leek", UseableItem2DView.class);
		checkImplementation("item", "food", "licorice", UseableItem2DView.class);
		checkImplementation("item", "food", "mackerel", UseableItem2DView.class);
		checkImplementation("item", "food", "mauve apple", UseableItem2DView.class);
		checkImplementation("item", "food", "meat", UseableItem2DView.class);
		checkImplementation("item", "food", "olive", UseableItem2DView.class);
		checkImplementation("item", "food", "onion", UseableItem2DView.class);
		checkImplementation("item", "food", "pear", UseableItem2DView.class);
		checkImplementation("item", "food", "potato", UseableItem2DView.class);
		checkImplementation("item", "food", "purple apple", UseableItem2DView.class);
		checkImplementation("item", "food", "red lionfish", UseableItem2DView.class);
		checkImplementation("item", "food", "salad", UseableItem2DView.class);
		checkImplementation("item", "food", "sandwich", UseableItem2DView.class);
		checkImplementation("item", "food", "sausage", UseableItem2DView.class);
		checkImplementation("item", "food", "small easter egg", UseableItem2DView.class);
		checkImplementation("item", "food", "smoked cod", UseableItem2DView.class);
		checkImplementation("item", "food", "smoked ham", UseableItem2DView.class);
		checkImplementation("item", "food", "smoked meat", UseableItem2DView.class);
		checkImplementation("item", "food", "spinach", UseableItem2DView.class);
		checkImplementation("item", "food", "spotted egg", UseableItem2DView.class);
		checkImplementation("item", "food", "sugar", UseableItem2DView.class);
		checkImplementation("item", "food", "surgeonfish", UseableItem2DView.class);
		checkImplementation("item", "food", "toadstool", UseableItem2DView.class);

		checkImplementation("item", "furniture", null, Item2DView.class);

		checkImplementation("item", "helmet", null, Item2DView.class);
		checkImplementation("item", "helmet", "blue helmet", Item2DView.class);
		checkImplementation("item", "helmet", "chain helmet", Item2DView.class);
		checkImplementation("item", "helmet", "chaos helmet", Item2DView.class);
		checkImplementation("item", "helmet", "golden helmet", Item2DView.class);
		checkImplementation("item", "helmet", "magic chain helmet", Item2DView.class);
		checkImplementation("item", "helmet", "mainio helmet", Item2DView.class);
		checkImplementation("item", "helmet", "mithril helmet", Item2DView.class);
		checkImplementation("item", "helmet", "red helmet", Item2DView.class);
		checkImplementation("item", "helmet", "viking helmet", Item2DView.class);
		checkImplementation("item", "helmet", "xeno helmet", Item2DView.class);

		checkImplementation("item", "herb", "arandula", StackableItem2DView.class);
		checkImplementation("item", "herb", "kekik", StackableItem2DView.class);
		checkImplementation("item", "herb", "kokuda", StackableItem2DView.class);
		checkImplementation("item", "herb", "mandragora", StackableItem2DView.class);
		//~ checkImplementation("item", "herb", "reindeer moss", Item2DView.class);
		checkImplementation("item", "herb", "sclaria", StackableItem2DView.class);

		checkImplementation("item", "jewellery", "amethyst", StackableItem2DView.class);
		checkImplementation("item", "jewellery", "black pearl", StackableItem2DView.class);
		checkImplementation("item", "jewellery", "carbuncle", StackableItem2DView.class);
		checkImplementation("item", "jewellery", "diamond", StackableItem2DView.class);
		checkImplementation("item", "jewellery", "emerald", StackableItem2DView.class);
		checkImplementation("item", "jewellery", "obsidian", StackableItem2DView.class);
		checkImplementation("item", "jewellery", "sapphire", StackableItem2DView.class);

		checkImplementation("item", "key", null, Item2DView.class);
		checkImplementation("item", "key", "dungeon silver key", Item2DView.class);
		checkImplementation("item", "key", "gate key", Item2DView.class);
		checkImplementation("item", "key", "house key", Item2DView.class);
		checkImplementation("item", "key", "kanmararn prison key", Item2DView.class);
		checkImplementation("item", "key", "kotoch prison key", Item2DView.class);
		checkImplementation("item", "key", "lich gold key", Item2DView.class);
		checkImplementation("item", "key", "master key", Item2DView.class);
		checkImplementation("item", "key", "minotaur key", Item2DView.class);
		checkImplementation("item", "key", "nalwor bank key", Item2DView.class);
		checkImplementation("item", "key", "pet sanctuary key", Item2DView.class);
		checkImplementation("item", "key", "sedah gate key", Item2DView.class);
		checkImplementation("item", "key", "small key", Item2DView.class);

		checkImplementation("item", "legs", null, Item2DView.class);
		checkImplementation("item", "legs", "blue legs", Item2DView.class);
		checkImplementation("item", "legs", "dwarvish legs", Item2DView.class);
		checkImplementation("item", "legs", "elvish legs", Item2DView.class);
		checkImplementation("item", "legs", "golden legs", Item2DView.class);
		checkImplementation("item", "legs", "jewelled legs", Item2DView.class);
		checkImplementation("item", "legs", "leather legs", Item2DView.class);
		checkImplementation("item", "legs", "mainio legs", Item2DView.class);
		checkImplementation("item", "legs", "mithril legs", Item2DView.class);
		checkImplementation("item", "legs", "red legs", Item2DView.class);
		checkImplementation("item", "legs", "xeno legs", Item2DView.class);

		checkImplementation("item", "misc", "canned tuna", StackableItem2DView.class);
		checkImplementation("item", "misc", "dice", StackableItem2DView.class);

		checkImplementation("item", "missile", "fire shuriken", StackableItem2DView.class);

		checkImplementation("item", "money", "money", StackableItem2DView.class);

		checkImplementation("item", "relic", null, Item2DView.class);
		checkImplementation("item", "relic", "skull ring", Item2DView.class);

		checkImplementation("item", "resource", "candle", StackableItem2DView.class);
		checkImplementation("item", "resource", "cloth", StackableItem2DView.class);
		checkImplementation("item", "resource", "flour", StackableItem2DView.class);
		checkImplementation("item", "resource", "horse hair", StackableItem2DView.class);
		checkImplementation("item", "resource", "iron", StackableItem2DView.class);
		checkImplementation("item", "resource", "silk gland", StackableItem2DView.class);
		checkImplementation("item", "resource", "silk thread", StackableItem2DView.class);
		checkImplementation("item", "resource", "yarn", StackableItem2DView.class);

		checkImplementation("item", "ring", "engagement ring", Ring2DView.class);
		checkImplementation("item", "ring", "insulated ring", Ring2DView.class);
		checkImplementation("item", "ring", "turtle shell ring", Ring2DView.class);

		checkImplementation("item", "scroll", "balloon", UseableItem2DView.class);
		checkImplementation("item", "scroll", "ados city scroll", UseableItem2DView.class);
		checkImplementation("item", "scroll", "deniran city scroll", UseableItem2DView.class);
		checkImplementation("item", "scroll", "fado city scroll", UseableItem2DView.class);
		checkImplementation("item", "scroll", "home scroll", UseableItem2DView.class);
		checkImplementation("item", "scroll", "kalavan city scroll", UseableItem2DView.class);
		checkImplementation("item", "scroll", "kirdneh city scroll", UseableItem2DView.class);
		checkImplementation("item", "scroll", "nalwor city scroll", UseableItem2DView.class);
		checkImplementation("item", "scroll", "rainbow beans", UseableItem2DView.class);
		checkImplementation("item", "scroll", "summon pet scroll", UseableItem2DView.class);
		checkImplementation("item", "scroll", "twilight moss", UseableItem2DView.class);

		checkImplementation("item", "shield", null, Item2DView.class);
		checkImplementation("item", "shield", "blue shield", Item2DView.class);
		checkImplementation("item", "shield", "elvish shield", Item2DView.class);
		checkImplementation("item", "shield", "golden shield", Item2DView.class);
		checkImplementation("item", "shield", "mainio shield", Item2DView.class);
		checkImplementation("item", "shield", "mithril shield", Item2DView.class);
		checkImplementation("item", "shield", "red shield", Item2DView.class);
		checkImplementation("item", "shield", "wooden shield", Item2DView.class);
		checkImplementation("item", "shield", "xeno shield", Item2DView.class);

		checkImplementation("item", "sword", null, Item2DView.class);
		checkImplementation("item", "sword", "assassin dagger", Item2DView.class);
		checkImplementation("item", "sword", "black sword", Item2DView.class);
		checkImplementation("item", "sword", "chaos dagger", Item2DView.class);
		checkImplementation("item", "sword", "dagger", Item2DView.class);
		checkImplementation("item", "sword", "elvish sword", Item2DView.class);
		checkImplementation("item", "sword", "fire sword", Item2DView.class);
		checkImplementation("item", "sword", "imperator sword", Item2DView.class);
		checkImplementation("item", "sword", "katana", Item2DView.class);
		checkImplementation("item", "sword", "nihonto", Item2DView.class);
		checkImplementation("item", "sword", "obsidian knife", Item2DView.class);
		checkImplementation("item", "sword", "soul dagger", Item2DView.class);
		checkImplementation("item", "sword", "sword", Item2DView.class);
		checkImplementation("item", "sword", "xeno sword", Item2DView.class);

		checkImplementation("item", "token", null, Item2DView.class);
		checkImplementation("item", "token", "arrow game token", Item2DView.class);
		checkImplementation("item", "token", "o board token", Item2DView.class);
		checkImplementation("item", "token", "x board token", Item2DView.class);

		checkImplementation("item", "tool", null, Item2DView.class);
		checkImplementation("item", "tool", "fishing rod", Item2DView.class);
		checkImplementation("item", "tool", "magical needle", Item2DView.class);
		checkImplementation("item", "tool", "pick", Item2DView.class);

		checkImplementation("item", "whip", null, Item2DView.class);
		checkImplementation("item", "whip", "venom whip", Item2DView.class);


		/* *** ENTITIES LOADED FROM ENTITYVIEW FACTORY *** */

		// item
		checkImplementation("item", null, null, Item2DView.class);
		checkImplementation("item", "ammunition", null, StackableItem2DView.class);
		checkImplementation("item", "book", "bestiary", UseableGenericItem2DView.class);
		checkImplementation("item", "box", null, Box2DView.class);
		checkImplementation("item", "club", "wizard_staff", UseableItem2DView.class);
		checkImplementation("item", "container", null, StackableItem2DView.class);
		checkImplementation("item", "drink", null, UseableItem2DView.class);
		checkImplementation("item", "flower", null, StackableItem2DView.class);
		checkImplementation("item", "food", null, UseableItem2DView.class);
		checkImplementation("item", "herb", null, StackableItem2DView.class);
		checkImplementation("item", "jewellery", null, StackableItem2DView.class);
		checkImplementation("item", "misc", null, StackableItem2DView.class);
		checkImplementation("item", "misc", "bulb", UseableItem2DView.class);
		checkImplementation("item", "misc", "seed", UseableItem2DView.class);
		checkImplementation("item", "misc", "snowglobe", UseableGenericItem2DView.class);
		checkImplementation("item", "misc", "teddy", UseableGenericItem2DView.class);
		checkImplementation("item", "missile", null, StackableItem2DView.class);
		checkImplementation("item", "money", null, StackableItem2DView.class);
		checkImplementation("item", "resource", null, StackableItem2DView.class);
		checkImplementation("item", "ring", null, Ring2DView.class);
		checkImplementation("item", "ring", "emerald ring", BreakableRing2DView.class);
		checkImplementation("item", "ring", "wedding", UseableRing2DView.class);
		checkImplementation("item", "scroll", null, UseableItem2DView.class);
		checkImplementation("item", "special", null, StackableItem2DView.class);
		checkImplementation("item", "special", "mithril clasp", Item2DView.class);
		checkImplementation("item", "tool", "foodmill", UseableItem2DView.class);
		checkImplementation("item", "tool", "metal detector", UseableGenericItem2DView.class);
		checkImplementation("item", "tool", "rope", StackableItem2DView.class);
		checkImplementation("item", "tool", "rotary cutter", UseableGenericItem2DView.class);
		checkImplementation("item", "tool", "scroll eraser", UseableItem2DView.class);
		checkImplementation("item", "tool", "sugarmill", UseableItem2DView.class);

		// grower
		checkImplementation("growing_entity_spawner", null, null, GrainField2DView.class);
		checkImplementation("growing_entity_spawner", "items/grower/carrot_grower", null, CarrotGrower2DView.class);
		checkImplementation("growing_entity_spawner", "items/grower/wood_grower", null, CarrotGrower2DView.class);
		checkImplementation("plant_grower", null, null, PlantGrower2DView.class);

		// sign
		checkImplementation("blackboard", null, null, Sign2DView.class);
		checkImplementation("rented_sign", null, null, Sign2DView.class);
		checkImplementation("shop_sign", null, null, ShopSign2DView.class);
		checkImplementation("sign", null, null, Sign2DView.class);

		// portal & door
		checkImplementation("door", null, null, Door2DView.class);
		checkImplementation("gate", null, null, Gate2DView.class);
		checkImplementation("house_portal", null, null, HousePortal2DView.class);
		checkImplementation("portal", null, null, Portal2DView.class);

		// NPC
		checkImplementation("baby_dragon", null, null, Pet2DView.class);
		checkImplementation("cat", null, null, Pet2DView.class);
		checkImplementation("npc", null, null, NPC2DView.class);
		checkImplementation("pet", null, null, Pet2DView.class);
		checkImplementation("purple_dragon", null, null, Pet2DView.class);
		checkImplementation("sheep", null, null, Sheep2DView.class);
		checkImplementation("training_dummy", null, null, TrainingDummy2DView.class);

		// creature
		checkImplementation("creature", null, null, Creature2DView.class);
		checkImplementation("creature", "ent", null, BossCreature2DView.class);

		// resource sources
		checkImplementation("fish_source", null, null, UseableEntity2DView.class);
		checkImplementation("gold_source", null, null, UseableEntity2DView.class);
		checkImplementation("well_source", null, null, UseableEntity2DView.class);

		// misc
		checkImplementation("area", null, null, InvisibleEntity2DView.class);
		checkImplementation("block", null, null, LookableEntity2DView.class);
		checkImplementation("blood", null, null, Blood2DView.class);
		checkImplementation("chest", null, null, Chest2DView.class);
		checkImplementation("corpse", null, null, Corpse2DView.class);
		checkImplementation("fire", null, null, UseableEntity2DView.class);
		checkImplementation("flyover", null, null, FlyOverArea2DView.class);
		checkImplementation("food", null, null, SheepFood2DView.class);
		checkImplementation("game_board", null, null, GameBoard2DView.class);
		checkImplementation("player", null, null, Player2DView.class);
		checkImplementation("spell", null, null, Spell2DView.class);
		checkImplementation("useable_entity", null, null, UseableEntity2DView.class);
		checkImplementation("walkblocker", null, null, WalkBlocker2DView.class);
		checkImplementation("wall", null, null, Wall2DView.class);


		final int incomplete = entities.size();
		if (incomplete > 0) {
			String msg = "";
			for (final EntityRep erep: entities) {
				if (msg.length() > 0) {
					msg += "; ";
				}
				msg += erep.toString();
			}
			fail("the following " + incomplete + " entities were not tested: " + msg);
		}
	}

	private void checkImplementation(final String type_name, final String class_name, final String name,
			final Class implementation) {
		final EntityRep erep = new EntityRep(type_name, class_name, name, implementation);
		if (!entities.contains(erep)) {
			fail("duplicate or not a registered entity representation: " + erep.toString());
		}
		logger.debug("testing entity: " + erep.toString());
		assertEquals(EntityViewFactory.getViewClass(erep.classname, erep.subclass, erep.name), erep.implementation);
		entities.remove(erep);
	}
}
