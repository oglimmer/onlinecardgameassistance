package de.oglimmer.bcg.logic;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.util.JSONTransformable;

public abstract class CardDeck extends CardList implements JSONTransformable,
		UIElement {

	// private static final Logger log = LoggerFactory.getLogger(Cards.class);

	protected String description;
	protected int x;
	protected int y;
	protected Player owner;
	protected boolean openCardList;
	protected String emptyImageUrl;
	protected int[] zIndexBorders = new int[] { Card.DEFAULT_ZINDEX,
			Card.DEFAULT_ZINDEX };

	public CardDeck(String name, String description, Player owner, int x,
			int y, boolean openCardList, String emptyImageUrl) {
		super(name);
		this.description = description;
		this.x = x;
		this.y = y;
		this.owner = owner;
		this.openCardList = openCardList;
		this.emptyImageUrl = emptyImageUrl;
	}

	protected abstract boolean isMoveable();

	protected abstract void addMenu(Player player, JSONObject json);

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

	public String getDescription() {
		return description;
	}

	@Override
	public JSONObject toJSON(Player player, JSONPayload... payload) {
		JSONObject json = new JSONObject();
		String imageUrl;
		if (getCards().isEmpty()) {
			imageUrl = emptyImageUrl;
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
		json.element("moveable", isMoveable());
		addMenu(player, json);
		return json;
	}
}
