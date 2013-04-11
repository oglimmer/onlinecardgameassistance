package de.oglimmer.bcg.logic.swccg;

import java.util.ArrayList;
import java.util.Collection;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.logic.CardDeck;
import de.oglimmer.bcg.logic.Player;

class SwccgCardDeck extends CardDeck {

	public static final String DECKNAME_RESERVEDECK = "reserve";
	public static final String DECKDESC_RESERVEDECK = "reserve deck";
	public static final String DECKNAME_LOSTPILE = "lost";
	public static final String DECKDESC_LOSTPILE = "lost pile";
	public static final String DECKNAME_FORCEPILE = "force";
	public static final String DECKDESC_FORCEPILE = "force pile";
	public static final String DECKNAME_USEDPILE = "used";
	public static final String DECKDESC_USEDPILE = "used pile";

	public SwccgCardDeck(String name, String description, Player owner, int x,
			int y, boolean openCardList) {
		super(name, description, owner, x, y, openCardList,
				SwccgCardsFactory.EMPTY_CARDS_IMG);
	}

	@Override
	protected boolean isMoveable() {
		return true;
	}

	@Override
	protected void addMenu(Player player, JSONObject json) {
		Collection<String> menu = new ArrayList<>();
		menu.add("~" + owner.getSide() + "'s " + description);
		if (owner == player && !getCards().isEmpty()) {
			menu.add("-");
			switch (getName()) {
			case DECKNAME_FORCEPILE:
				menu.add("Use one force:deckToDeck:used_top");
				menu.add("-");
				menu.add("Take top card into hand:deckToHand");
				menu.add("-");
				menu.add("Play top card face up on table:deckToTable:up");
				menu.add("Play top card face down on table:deckToTable:down");
				menu.add("-");
				menu.add("Lose top card:deckToDeck:lost_top");
				menu.add("-");
				menu.add("Shuffle:shuffle");
				break;
			case DECKNAME_USEDPILE:
				menu.add("Put all cards back under reserve deck:deckToDeck:reserve_all");
				menu.add("-");
				menu.add("Take top card into hand:deckToHand");
				menu.add("-");
				menu.add("Play top card face up on table:deckToTable:up");
				menu.add("Play top card face down on table:deckToTable:down");
				menu.add("-");
				menu.add("Lose top card:deckToDeck:lost_top");
				menu.add("-");
				menu.add("Shuffle:shuffle");
				break;
			case DECKNAME_LOSTPILE:
				menu.add("Take top card into hand:deckToHand");
				menu.add("-");
				menu.add("Play top card face up on table:deckToTable:up");
				menu.add("Play top card face down on table:deckToTable:down");
				menu.add("-");
				menu.add("Shuffle:shuffle");
				break;
			case DECKNAME_RESERVEDECK:
				menu.add("Activate one force:deckToDeck:force_top");
				menu.add("-");
				menu.add("Take top card into hand:deckToHand");
				menu.add("-");
				menu.add("Play top card face up on table:deckToTable:up");
				menu.add("Play top card face down on table:deckToTable:down");
				menu.add("-");
				menu.add("Lose top card:deckToDeck:lost_top");
				menu.add("-");
				menu.add("Shuffle:shuffle");
				break;
			}
		}
		json.element("menu", menu);
	}
}
