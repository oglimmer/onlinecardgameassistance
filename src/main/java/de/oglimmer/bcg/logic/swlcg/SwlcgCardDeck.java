package de.oglimmer.bcg.logic.swlcg;

import java.util.ArrayList;
import java.util.Collection;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.logic.CardDeck;
import de.oglimmer.bcg.logic.Player;

public class SwlcgCardDeck extends CardDeck {

	static final String DECKNAME_DISCARD = "discard pile";
	static final String DECKNAME_COMMAND = "command deck";
	static final String DECKNAME_OBJECTIVE = "objective deck";

	public SwlcgCardDeck(String name, Player owner, int x, int y,
			boolean openCardList) {
		super(name, owner, x, y, openCardList,
				SwlcgCardsFactory.EMPTY_CARDS_IMG);
	}

	protected void addMenu(Player player, JSONObject json) {
		if (owner == player && !getCards().isEmpty()) {
			Collection<String> menu = new ArrayList<>();
			switch (getName()) {
			case DECKNAME_DISCARD:
				menu.add("~Discard Pile");
				menu.add("-");
				menu.add("Play card face up on table:deckToTable:up");
				menu.add("Take top card into hand:deckToHand");
				break;
			case DECKNAME_COMMAND:
				menu.add("~Command Deck");
				menu.add("-");
				menu.add("Take top card into hand:deckToHand");
				menu.add("-");
				menu.add("Play card face up on table:deckToTable:up");
				menu.add("Play card face down on table:deckToTable:down");
				menu.add("-");
				menu.add("Shuffle:shuffle");
				break;
			case DECKNAME_OBJECTIVE:
				menu.add("~Objective Deck");
				menu.add("-");
				menu.add("Take top card into hand:deckToHand");
				menu.add("-");
				menu.add("Play card face up on table:deckToTable:up");
				menu.add("Play card face down on table:deckToTable:down");
				menu.add("-");
				menu.add("Shuffle:shuffle");
				break;
			}
			json.element("menu", menu);
		}
	}

	@Override
	protected boolean isMoveable() {
		return getName().equals(DECKNAME_DISCARD);
	}
}
