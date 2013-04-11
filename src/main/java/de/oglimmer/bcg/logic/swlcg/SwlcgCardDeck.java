package de.oglimmer.bcg.logic.swlcg;

import java.util.ArrayList;
import java.util.Collection;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.logic.CardDeck;
import de.oglimmer.bcg.logic.Player;

class SwlcgCardDeck extends CardDeck {

	static final String DECKNAME_AFFILIATION = "affiliation";
	static final String DECKNAME_DISCARD = "discard";
	static final String DECKNAME_COMMAND = "command";
	static final String DECKNAME_OBJECTIVE = "objective";
	static final String DECKNAME_LOSTOBJECTIVES = "lostobjectives";

	static final String DECKDESC_DISCARD = "discard pile";
	static final String DECKDESC_COMMAND = "command deck";
	static final String DECKDESC_OBJECTIVE = "objective deck";
	static final String DECKDESC_LOSTOBJECTIVES = "lost objectives pile";

	public SwlcgCardDeck(String name, String description, Player owner, int x,
			int y, boolean openCardList) {
		super(name, description, owner, x, y, openCardList,
				SwlcgCardsFactory.EMPTY_CARDS_IMG);
	}

	protected void addMenu(Player player, JSONObject json) {
		if (owner == player && !getCards().isEmpty()) {
			Collection<String> menu = new ArrayList<>();
			menu.add("~" + description);
			menu.add("-");
			switch (getName()) {
			case DECKNAME_DISCARD:
				menu.add("Play card face up on table:deckToTable:up");
				menu.add("Take top card into hand:deckToHand");
				break;
			case DECKNAME_COMMAND:
				menu.add("Take top card into hand:deckToHand");
				menu.add("-");
				menu.add("Play card face up on table:deckToTable:up");
				menu.add("Play card face down on table:deckToTable:down");
				menu.add("-");
				menu.add("Shuffle:shuffle");
				break;
			case DECKNAME_OBJECTIVE:
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
