package games.stendhal.server.maps.kotoch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.ProducerBehaviour;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

public class SmitherNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildKotochSmitherArea(zone);
	}

	private void buildKotochSmitherArea(StendhalRPZone zone) {
		SpeakerNPC smither = new SpeakerNPC("Vulcanus") {

			@Override
			// he doesn't move.
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addGoodbye("Farewell");
				add(
				        ConversationStates.ATTENDING,
				        Arrays.asList("iron", "wood", "gold","giant_heart"),
				        null,
				        ConversationStates.ATTENDING,
				        "Who told you that!?! *cough* Anyway, yes, I can #forge an magical sword for you if you take me those things.",
				        null);
				add(
				        ConversationStates.ATTENDING,
				        Arrays.asList("giant_heart"),
				        null,
				        ConversationStates.ATTENDING,
				        "You can find it at the mountains at the north of Semos, but beware they are really strong.",
				        null);

				Map<String, Integer> requiredResources = new HashMap<String, Integer>();
				requiredResources.put("iron", new Integer(10));
				requiredResources.put("wood", new Integer(20));
				requiredResources.put("gold_bar", new Integer(10));
				requiredResources.put("giant_heart", new Integer(5));
				ProducerBehaviour behaviour = new ProducerBehaviour("vulcanus_forge_sword", "forge", "magic_sword",
				        requiredResources, 5 * 60, true);
				addProducer(
				        behaviour,
				        "Hallo! I am Vulcanus the smither.");

			}
		};

		smither.setDescription("You see vulcanus. You feel a strange sensation near him.");
		zone.assignRPObjectID(smither);
		smither.put("class", "smithernpc");
		smither.set(62, 116);
		smither.setDirection(Direction.DOWN);
		smither.initHP(100);
		zone.add(smither);
		npcs.add(smither);
	}
}
