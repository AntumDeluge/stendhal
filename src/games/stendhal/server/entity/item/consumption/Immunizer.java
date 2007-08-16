package games.stendhal.server.entity.item.consumption;

import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

class Immunizer implements Feeder {

	public boolean feed(ConsumableItem item, Player player) {
		player.setImmune();
		// set a timer to remove the immunity effect after some time
		TurnNotifier notifier = TurnNotifier.get();
		// first remove all effects from previously used immunities to
		// restart the timer
		TurnListener tl = new AntidoteEater(player);
		notifier.dontNotify(tl);
		notifier.notifyInTurns(item.getAmount(), tl);
		item.removeOne();
		return true;
	}

}
