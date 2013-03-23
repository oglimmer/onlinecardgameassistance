package de.oglimmer.bcg.logic;

import java.util.ArrayList;
import java.util.Collection;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.util.JSONTransformable;
import de.oglimmer.bcg.util.RandomString;

public class Card implements JSONTransformable {

	// private static final Logger log = LoggerFactory.getLogger(Card.class);

	private String id;
	private String imageUrl;
	private String backImageUrl;
	private boolean faceup;
	private Player owner;
	private int x;
	private int y;
	private int[] counter = new int[3];
	private CardList origin;

	public Card(Player owner, CardList origin, String imageUrl,
			String backImageUrl) {
		this.id = RandomString.getRandomStringASCII(8);
		this.origin = origin;
		this.imageUrl = imageUrl;
		this.backImageUrl = backImageUrl;
		this.owner = owner;
		this.x = 200;
		this.y = 20;
	}

	public String getId() {
		return id;
	}

	public void setFaceup(boolean faceup) {
		this.faceup = faceup;
	}

	public boolean isFaceup() {
		return faceup;
	}

	public boolean isOwner(Player player) {
		return owner == null || owner == player;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void modCounter(int i, int pos) {
		counter[pos] += i;
	}

	public int getCounter(int i) {
		return counter[i];
	}

	public CardList getOrigin() {
		return origin;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getBackImageUrl() {
		return backImageUrl;
	}

	@Override
	public JSONObject toJSON(Player player, JSONPayload... payload) {
		if (payload.length == 0) {
			throw new GameException("toJson without payload makes no sense");
		}
		JSONObject card = new JSONObject();
		card.element("id", getId());
		if (JSONPayload.BASE.in(payload)) {
			handleJSONPayloadBase(player, card);
		}
		if (JSONPayload.COUNTER.in(payload)) {
			handleJSONPayloadCounter(card);
		}
		return card;
	}

	protected void handleJSONPayloadBase(Player player, JSONObject card) {
		card.element("x", x);
		card.element("y", y);
		if (faceup) {
			card.element("imageUrl", imageUrl);
		} else {
			card.element("imageUrl", "cards/" + backImageUrl);
		}

		Collection<String> menu = new ArrayList<>();
		addMenu(player, card, menu);
		if (!menu.isEmpty()) {
			card.element("menu", menu);
		}
	}

	protected void handleJSONPayloadCounter(JSONObject card) {
		for (int i = 0; i < 3; i++) {
			card.element("counter" + i, getCounter(i));
		}
	}

	protected void addMenu(Player player, JSONObject card,
			Collection<String> menu) {
		if (owner == null || owner == player) {
			if (!faceup) {
				menu.add("Face up:flipCard");
			}
			switch (player.getGame().getBoard().getArea(this)) {
			case "hand":
				menu.add("Play card face up on table:playCardOnTable:up");
				menu.add("Play card face down on table:playCardOnTable:down");
				menu.add("Put card on top of command deck:returnToDeck:top");
				menu.add("Put card under command deck:returnToDeck:bottom");
				break;
			case "table":
				menu.add("Rotate card:rotateCard");
				menu.add("Put card on top of command deck:returnToDeck:top");
				menu.add("Put card under command deck:returnToDeck:bottom");
				menu.add("Take top card into hand:takeCardIntoHand:card");
				menu.add("+1 Resource:modCounter:add-0");
				menu.add("-1 Resource:modCounter:sub-0");
				menu.add("+1 Damage:modCounter:add-1");
				menu.add("-1 Damage:modCounter:sub-1");
				menu.add("+1 Shield:modCounter:add-2");
				menu.add("-1 Shield:modCounter:sub-2");
				break;
			}
			menu.add("Discard card:discardCard");
		}
	}

}
