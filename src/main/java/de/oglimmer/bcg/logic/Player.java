package de.oglimmer.bcg.logic;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.logic.config.GameConfig;
import de.oglimmer.bcg.util.JSONTransformable;
import de.oglimmer.bcg.util.RandomString;

public class Player implements JSONTransformable {

	// private static final Logger log = LoggerFactory.getLogger(Player.class);

	private int no;
	private String id;
	private String key;
	private Game game;
	private CardsSet cardsSet;
	private Side side;
	private boolean connected;
	private int browserWidth;
	private int browserHeight;
	private Collection<String> message = new ArrayList<>();

	public Player(GameConfig gameConfig, int no, String key, Game game,
			Side side, InputStream deckStream) {
		this.id = RandomString.getRandomStringASCII(8);
		this.no = no;
		this.key = key;
		this.game = game;
		this.side = side;
		cardsSet = gameConfig.getCardsFactory(game, this, deckStream)
				.createCardsSet();
	}

	public Game getGame() {
		return game;
	}

	public String getId() {
		return id;
	}

	public int getNo() {
		return no;
	}

	public String getKey() {
		return key;
	}

	public Side getSide() {
		return side;
	}

	public CardsSet getCardStacks() {
		return cardsSet;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setBrowserWidth(int browserWidth) {
		this.browserWidth = browserWidth;
	}

	public void setBrowserHeight(int browserHeight) {
		this.browserHeight = browserHeight;
	}

	public int getBrowserWidth() {
		return browserWidth;
	}

	public int getBrowserHeight() {
		return browserHeight;
	}

	public Collection<String> getMessages() {
		return message;
	}

	public void processMessage(JSONObject cardJSON, String text) {
		cardJSON.element("messageItem", text);
		message.add(text);
	}

	/**
	 * Get card by id (visible for this player, may be on table though)
	 * 
	 * @param cardId
	 * @return
	 */
	public Card getCard(String cardId) {
		for (CardList cs : getCardStacks().getCardLists()) {
			for (Card c : cs.getCards()) {
				if (c.getId().equals(cardId)) {
					return c;
				}
			}
		}
		throw new GameException("Player has no card with id=" + cardId);
	}

	/**
	 * Returns an UIElement with id. This can be an own card, an own cardDeck or
	 * an open cardDeck (like opponent's discard pile).
	 * 
	 * @param id
	 * @return
	 */
	public UIElement getUIElement(String id) {
		for (CardList cs : getCardStacks().getCardLists()) {
			if (cs.getId().equals(id)) {
				return (CardDeck) cs;
			}
			for (Card c : cs.getCards()) {
				if (c.getId().equals(id)) {
					return c;
				}
			}
		}

		CardList cs = game.getBoard().getCardList(id);
		return (CardDeck) cs;
	}

	/**
	 * Get cardList by id (visible for this player, may be on table though)
	 * 
	 * @param cardsId
	 * @return
	 */
	public CardList getCardListById(String cardsId) {
		for (CardList cs : cardsSet.getCardLists()) {
			if (cs.getId().equals(cardsId)) {
				return cs;
			}
		}
		throw new GameException("Player " + no + " has no cardstack with id="
				+ cardsId);
	}

	/**
	 * Get a cardList by its name
	 * 
	 * @param name
	 * @return
	 */
	public CardList getCardListByName(String name) {
		for (CardList cs : cardsSet.getCardLists()) {
			if (cs.getName().equals(name)) {
				return cs;
			}
		}
		throw new GameException("Player " + no + " has no cardstack with name="
				+ name);
	}

	/**
	 * Get cardList by a card id (for this player)
	 * 
	 * @param cardsId
	 * @return
	 */
	public CardList getCardListByCardId(String cardId) {
		for (CardList cs : getCardStacks().getCardLists()) {
			for (Card c : cs.getCards()) {
				if (c.getId().equals(cardId)) {
					return cs;
				}
			}
		}
		throw new GameException("Player " + no
				+ " has no cardstack with a card id=" + cardId);
	}

	@Override
	public String toJSON(Player player, JSONPayload... payload) {
		return Integer.toString(no);
	}

}
