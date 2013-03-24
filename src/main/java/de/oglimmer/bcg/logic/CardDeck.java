package de.oglimmer.bcg.logic;

import java.util.ArrayList;
import java.util.Collection;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.util.JSONTransformable;

public class CardDeck extends CardList implements JSONTransformable {

	// private static final Logger log = LoggerFactory.getLogger(Cards.class);

	private int x;
	private int y;
	private Player owner;
	private boolean openCardList;

	public CardDeck(String name, Player owner, int x, int y,
			boolean openCardList) {
		super(name);
		this.x = x;
		this.y = y;
		this.owner = owner;
		this.openCardList = openCardList;
	}

	@Override
	public JSONObject toJSON(Player player, JSONPayload... payload) {
		JSONObject json = new JSONObject();
		String imageUrl;
		if (getCards().isEmpty()) {
			imageUrl = CardsFactory.EMPTY_CARDS_IMG;
		} else {
			Card c = getCards().get(0);
			if (c.isFaceup()) {
				imageUrl = c.getImageUrl();
			} else {
				imageUrl = c.getBackImageUrl();
			}
		}
		json.element("imageUrl", imageUrl);
		json.element("x", x);
		json.element("y", y);
		json.element("id", getId());
		json.element("type", "deck");
		addMenu(player, json);
		return json;
	}

	private void addMenu(Player player, JSONObject json) {
		if (owner == player && !getCards().isEmpty()) {
			Collection<String> menu = new ArrayList<>();
			switch (getName()) {
			case "discard pile":
				menu.add("~Discard Pile");
				menu.add("-");
				menu.add("Play card face up on table:takeCardPlayOnTable:up");
				menu.add("Take top card into hand:takeCardIntoHand:deck");
				break;
			case "command deck":
				menu.add("~Command Deck");
				menu.add("-");
				menu.add("Take top card into hand:takeCardIntoHand:deck");
				menu.add("-");
				menu.add("Play card face up on table:takeCardPlayOnTable:up");
				menu.add("Play card face down on table:takeCardPlayOnTable:down");
				menu.add("-");
				menu.add("Shuffle:shuffle");
				break;
			case "objective deck":
				menu.add("~Objective Deck");
				menu.add("-");
				menu.add("Take top card into hand:takeCardIntoHand:deck");
				menu.add("-");
				menu.add("Play card face up on table:takeCardPlayOnTable:up");
				menu.add("Play card face down on table:takeCardPlayOnTable:down");
				menu.add("-");
				menu.add("Shuffle:shuffle");
				break;
			}
			json.element("menu", menu);
		}
	}

	public boolean isOpenCardList() {
		return openCardList;
	}
}
