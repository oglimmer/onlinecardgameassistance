package de.oglimmer.bcg.logic;

import java.util.ArrayList;
import java.util.Collection;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.util.JSONTransformable;

public class CardDeck extends CardList implements JSONTransformable, UIElement {

	// private static final Logger log = LoggerFactory.getLogger(Cards.class);

	public static final String DECKNAME_DISCARD = "discard pile";
	public static final String DECKNAME_COMMAND = "command deck";
	public static final String DECKNAME_OBJECTIVE = "objective deck";

	private int x;
	private int y;
	private Player owner;
	private boolean openCardList;
	private int[] zIndexBorders = new int[] { Card.DEFAULT_ZINDEX,
			Card.DEFAULT_ZINDEX };

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
		json.element("moveable", getName().equals(DECKNAME_DISCARD));
		addMenu(player, json);
		return json;
	}

	private void addMenu(Player player, JSONObject json) {
		if (owner == player && !getCards().isEmpty()) {
			Collection<String> menu = new ArrayList<>();
			switch (getName()) {
			case DECKNAME_DISCARD:
				menu.add("~Discard Pile");
				menu.add("-");
				menu.add("Play card face up on table:takeCardPlayOnTable:up");
				menu.add("Take top card into hand:takeCardIntoHand:deck");
				break;
			case DECKNAME_COMMAND:
				menu.add("~Command Deck");
				menu.add("-");
				menu.add("Take top card into hand:takeCardIntoHand:deck");
				menu.add("-");
				menu.add("Play card face up on table:takeCardPlayOnTable:up");
				menu.add("Play card face down on table:takeCardPlayOnTable:down");
				menu.add("-");
				menu.add("Shuffle:shuffle");
				break;
			case DECKNAME_OBJECTIVE:
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

	public int[] getZIndexBorders() {
		return zIndexBorders;
	}

	@Override
	public void setX(int xPos) {
		this.x = xPos;
	}

	@Override
	public void setY(int yPos) {
		this.y = yPos;		
	}

}
