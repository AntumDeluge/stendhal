package games.stendhal.server.entity.npc.behaviour.impl;

import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.ExpressionType;
import games.stendhal.server.entity.npc.parser.WordList;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * The behaviour of an NPC who is able to produce something for a player if the
 * player brings the required resources. Production takes time, depending on the
 * amount of ordered products.
 * 
 * @author daniel
 */
public class ProducerBehaviour extends TransactionBehaviour {

	/**
	 * To store the current status of a production order, each ProducerBehaviour
	 * needs to have an exclusive quest slot.
	 * 
	 * This slot can have three states:
	 * <ul>
	 * <li>unset: if the player has never asked the NPC to produce anything.</li>
	 * <li>done: if the player's last order has been processed.</li>
	 * <li>number;product;time: if the player has given an order and has not
	 * yet retrieved the product. number is the amount of products that the
	 * player will get, product is the name of the ordered product, and time is
	 * the time when the order was given, in milliseconds since the epoch.</li>
	 * </ul>
	 * 
	 * Note: The product name is stored although each ProductBehaviour only
	 * allows one type of product at the moment. We store it to make the system
	 * extensible.
	 */
	private final String questSlot;

	/**
	 * The name of the activity, e.g. "build", "forge", "bake"
	 */
	private final String productionActivity;

	/**
	 * The unit in which the product is counted, e.g. "bags", "pieces", "pounds"
	 */
	// private String productUnit;

	private final String productName;

	/**
	 * Whether the produced item should be player bound.
	 */
	private final boolean productBound;

	/**
	 * A mapping which maps the name of each required resource (e.g. "iron ore")
	 * to the amount of this resource that is required for one unit of the
	 * product.
	 */
	private final Map<String, Integer> requiredResourcesPerItem;

	/**
	 * The number of seconds required to produce one unit of the product.
	 */
	private final int productionTimePerItem;

	/**
	 * Creates a new ProducerBehaviour.
	 * 
	 * @param questSlot
	 *            the slot that is used to store the status
	 * @param productionActivity
	 *            the name of the activity, e.g. "build", "forge", "bake"
	 * @param productName
	 *            the name of the product, e.g. "plate armor". It must be a
	 *            valid item name.
	 * @param requiredResourcesPerItem
	 *            a mapping which maps the name of each required resource (e.g.
	 *            "iron ore") to the amount of this resource that is required
	 *            for one unit of the product.
	 * @param productionTimePerItem
	 *            the number of seconds required to produce one unit of the
	 *            product.
	 */
	public ProducerBehaviour(final String questSlot, final String productionActivity,
			final String productName, final Map<String, Integer> requiredResourcesPerItem,
			final int productionTimePerItem) {
		this(questSlot, productionActivity, productName,
				requiredResourcesPerItem, productionTimePerItem, false);
	}

	/**
	 * Creates a new ProducerBehaviour.
	 * 
	 * @param questSlot
	 *            the slot that is used to store the status
	 * @param productionActivity
	 *            the name of the activity, e.g. "build", "forge", "bake"
	 * @param productName
	 *            the name of the product, e.g. "plate armor". It must be a
	 *            valid item name.
	 * @param requiredResourcesPerItem
	 *            a mapping which maps the name of each required resource (e.g.
	 *            "iron ore") to the amount of this resource that is required
	 *            for one unit of the product.
	 * @param productionTimePerItem
	 *            the number of seconds required to produce one unit of the
	 *            product.
	 * @param productBound
	 *            Whether the produced item should be player bound. Use only for
	 *            special one-time items.
	 */
	public ProducerBehaviour(final String questSlot, final String productionActivity,
			final String productName, final Map<String, Integer> requiredResourcesPerItem,
			final int productionTimePerItem, final boolean productBound) {
		super(productName);

		this.questSlot = questSlot;
		this.productionActivity = productionActivity;
		// this.productUnit = productUnit;
		this.productName = productName;
		this.requiredResourcesPerItem = requiredResourcesPerItem;
		this.productionTimePerItem = productionTimePerItem;
		this.productBound = productBound;

		// add the activity word as verb to the word list in case it is still missing there
		WordList.getInstance().registerName(productionActivity, ExpressionType.VERB);

		for (final String itemName : requiredResourcesPerItem.keySet()) {
			WordList.getInstance().registerName(itemName, ExpressionType.OBJECT);
		}
	}

	public String getQuestSlot() {
		return questSlot;
	}

	protected Map<String, Integer> getRequiredResourcesPerItem() {
		return requiredResourcesPerItem;
	}

	public String getProductionActivity() {
		return productionActivity;
	}

//	protected String getProductUnit() {
//	return productUnit;
//	}

	/**
	 * Return item name of the product to produce.
	 *
	 * @return product name
	 */
	public String getProductName() {
		return productName;
	}

	public int getProductionTime(final int amount) {
		return productionTimePerItem * amount;
	}

	/*
	 * Determine whether the produced item should be player bound.
	 * 
	 * @return <code>true</code> if the product should be bound.
	 */
	public boolean isProductBound() {
		return productBound;
	}

	/**
	 * Gets a nicely formulated string that describes the amounts and names of
	 * the resources that are required to produce <i>amount</i> units of the
	 * product, with hashes before the resource names in order to highlight
	 * them, e.g. "4 #wood, 2 #iron, and 6 #leather".
	 * 
	 * @param amount
	 *            The amount of products that were requested
	 * @return A string describing the required resources.
	 */
	protected String getRequiredResourceNamesWithHashes(final int amount) {
		// use sorted TreeSet instead of HashSet
		final Set<String> requiredResourcesWithHashes = new TreeSet<String>();
		for (final Map.Entry<String, Integer> entry : getRequiredResourcesPerItem().entrySet()) {
			requiredResourcesWithHashes.add(Grammar.quantityplnounWithHash(amount
					* entry.getValue(), entry.getKey()));
		}
		return Grammar.enumerateCollection(requiredResourcesWithHashes);
	}

	public String getApproximateRemainingTime(final Player player) {
		final String orderString = player.getQuest(questSlot);
		final String[] order = orderString.split(";");
		final long orderTime = Long.parseLong(order[2]);
		final long timeNow = new Date().getTime();
		final int numberOfProductItems = Integer.parseInt(order[0]);
		// String productName = order[1];

		final long finishTime = orderTime
				+ (getProductionTime(numberOfProductItems) * 1000);
		final int remainingSeconds = (int) ((finishTime - timeNow) / 1000);
		return TimeUtil.approxTimeUntil(remainingSeconds);
	}

	protected int getMaximalAmount(final Player player) {
		int maxAmount = Integer.MAX_VALUE;
		for (final Map.Entry<String, Integer> entry : getRequiredResourcesPerItem().entrySet()) {
			final int limitationByThisResource = player.getNumberOfEquipped(entry.getKey())
					/ entry.getValue();
			maxAmount = Math.min(maxAmount, limitationByThisResource);
		}
		return maxAmount;
	}

	/**
	 * Tries to take all the resources required to produce <i>amount</i> units
	 * of the product from the player. If this is possible, asks the user if the
	 * order should be initiated.
	 * 
	 * @param npc
	 * @param player
	 * @param amount
	 * @return true if all resources can be taken
	 */
	public boolean askForResources(final SpeakerNPC npc, final Player player, final int amount) {
		if (getMaximalAmount(player) < amount) {
			npc.say("I can only " + getProductionActivity() + " "
					+ Grammar.quantityplnoun(amount, getProductName())
					+ " if you bring me "
					+ getRequiredResourceNamesWithHashes(amount) + ".");
			return false;
		} else {
			setAmount(amount);
			npc.say("I need you to fetch me "
					+ getRequiredResourceNamesWithHashes(amount)
					+ " for this job. Do you have it?");
			return true;
		}
	}

	/**
	 * Tries to take all the resources required to produce the agreed amount of
	 * the product from the player. If this is possible, initiates an order.
	 * 
	 * @param npc
	 *            the involved NPC
	 * @param player
	 *            the involved player
	 */
	@Override
	public boolean transactAgreedDeal(final SpeakerNPC npc, final Player player) {
		if (getMaximalAmount(player) < amount) {
			// The player tried to cheat us by placing the resource
			// onto the ground after saying "yes"
			npc.say("Hey! I'm over here! You'd better not be trying to trick me...");
			return false;
		} else {
			for (final Map.Entry<String, Integer> entry : getRequiredResourcesPerItem().entrySet()) {
				final int amountToDrop = amount * entry.getValue();
				player.drop(entry.getKey(), amountToDrop);
			}
			final long timeNow = new Date().getTime();
			player.setQuest(questSlot, amount + ";" + getProductName() + ";"
					+ timeNow);
			npc.say("OK, I will "
					+ getProductionActivity()
					+ " "
					+ Grammar.quantityplnoun(amount, getProductName())
					+ " for you, but that will take some time. Please come back in "
					+ getApproximateRemainingTime(player) + ".");
			return true;
		}
	}

	/**
	 * This method is called when the player returns to pick up the finished
	 * product. It checks if the NPC is already done with the order. If that is
	 * the case, the player is given the product. Otherwise, the NPC asks the
	 * player to come back later.
	 * 
	 * @param npc
	 *            The producing NPC
	 * @param player
	 *            The player who wants to fetch the product
	 */
	public void giveProduct(final SpeakerNPC npc, final Player player) {
		final String orderString = player.getQuest(questSlot);
		final String[] order = orderString.split(";");
		final int numberOfProductItems = Integer.parseInt(order[0]);
		// String productName = order[1];
		final long orderTime = Long.parseLong(order[2]);
		final long timeNow = new Date().getTime();
		if (timeNow - orderTime < getProductionTime(numberOfProductItems) * 1000) {
			npc.say("Welcome back! I'm still busy with your order to "
					+ getProductionActivity() + " " + Grammar.quantityplnoun(numberOfProductItems, getProductName())
					+ " for you. Come back in "
					+ getApproximateRemainingTime(player) + " to get it.");
		} else {
			final StackableItem products = (StackableItem) SingletonRepository.getEntityManager().getItem(
					getProductName());

			products.setQuantity(numberOfProductItems);

			if (isProductBound()) {
				products.setBoundTo(player.getName());
			}

			if (player.equipToInventoryOnly(products)) {					
				npc.say("Welcome back! I'm done with your order. Here you have "
					+ Grammar.quantityplnoun(numberOfProductItems,
							getProductName()) + ".");
				player.setQuest(questSlot, "done");
				// give some XP as a little bonus for industrious workers
				player.addXP(numberOfProductItems);
				player.notifyWorldAboutChanges();
			} else {
				npc.say("Welcome back! I'm done with your order. But right now you cannot take the "
						+ Grammar.plnoun(numberOfProductItems, getProductName()) 
						+ ". Come back when you have space.");
			}
		}
	}

}
