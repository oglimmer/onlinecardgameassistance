package de.oglimmer.bcg.logic;

import java.util.ArrayList;
import java.util.Collection;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.util.JSONTransformable;
import de.oglimmer.bcg.util.RandomString;

public abstract class Card implements JSONTransformable, UIElement {

	public static final int DEFAULT_ZINDEX = 50_000;

	// private static final Logger log = LoggerFactory.getLogger(Card.class);

	protected String id;
	protected String name;
	protected String imageUrl;
	protected String backImageUrl;
	protected boolean faceup;
	protected Player owner;
	protected int x;
	protected int y;
	protected int[] counter = new int[3];
	protected CardList origin;
	protected boolean highlight;
	protected int zIndex = DEFAULT_ZINDEX;

	public Card(Player owner, CardList origin, String name, String imageUrl,
			String backImageUrl) {
		this.id = RandomString.getRandomStringASCII(8);
		this.origin = origin;
		this.name = name;
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

	@Override
	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	@Override
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

	public boolean isHighlight() {
		return highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
	}

	public String getName() {
		return name;
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
		if (JSONPayload.HIGHLIGHT.in(payload)) {
			handleJSONPayloadHighlight(card);
		}
		return card;
	}

	protected void handleJSONPayloadHighlight(JSONObject card) {
		card.element("hl", highlight);
	}

	protected void handleJSONPayloadBase(Player player, JSONObject card) {
		card.element("x", x);
		card.element("y", y);
		if (faceup) {
			card.element("imageUrl", imageUrl);
		} else {
			card.element("imageUrl", backImageUrl);
		}
		card.element("zIndex", zIndex);
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

	abstract protected void addMenu(Player player, JSONObject card,
			Collection<String> menu);

}
