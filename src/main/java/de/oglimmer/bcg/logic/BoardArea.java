package de.oglimmer.bcg.logic;

import java.util.List;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.util.JSONArrayList;
import de.oglimmer.bcg.util.JSONTransformable;

public class BoardArea implements JSONTransformable {

	// private static final Logger log =
	// LoggerFactory.getLogger(BoardArea.class);

	private String name;
	private JSONArrayList<Player> visibleFor = new JSONArrayList<>();
	private String css;
	// internal uses only, contains CardDecks only (no CardLists)
	private JSONArrayList<CardDeck> cardDeckList = new JSONArrayList<>();

	public BoardArea(String name) {
		this.name = name;
	}

	public List<Player> getVisibleFor() {
		return visibleFor;
	}

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	public void addCardDeck(CardDeck cl) {
		cardDeckList.add(cl);
	}

	public String getName() {
		return name;
	}

	public boolean isCardDeckVisibleForPlayer(Player player, CardDeck cardDeck) {
		return visibleFor.contains(player) && cardDeckList.contains(cardDeck);
	}

	@Override
	public JSONObject toJSON(Player player, JSONPayload... payload) {
		JSONObject json = new JSONObject();
		json.element("css", css);
		json.element("id", name);
		// add associated cardDecks
		json.element("cardDecks", cardDeckList.toJsonArray(player));
		return json;
	}
}
