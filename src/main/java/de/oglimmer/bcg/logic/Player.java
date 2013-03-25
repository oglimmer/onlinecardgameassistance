package de.oglimmer.bcg.logic;

import java.io.InputStream;

import de.oglimmer.bcg.util.JSONTransformable;
import de.oglimmer.bcg.util.RandomString;

public class Player implements JSONTransformable {

	// private static final Logger log = LoggerFactory.getLogger(Player.class);

	private int no;
	private String id;
	private String key;
	private Game game;
	private CardsSet cardStacks;
	private Side side;
	private boolean connected;
	private int browserWidth;
	private int browserHeight;

	public Player(int no, String key, Game game, Side side,
			InputStream deckStream) {
		this.id = RandomString.getRandomStringASCII(8);
		this.no = no;
		this.key = key;
		this.game = game;
		this.side = side;
		cardStacks = new CardsSet(game, this, deckStream, no);
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
		return cardStacks;
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

	/**
	 * Get card by id (visible for this player, may be on table though)
	 * 
	 * @param cardId
	 * @return
	 */
	public Card getCard(String cardId) {
		for (CardList cs : getCardStacks().getCardStacks().values()) {
			for (Card c : cs.getCards()) {
				if (c.getId().equals(cardId)) {
					return c;
				}
			}
		}
		throw new GameException("Player has no card with id=" + cardId);
	}

	/**
	 * Get cardList by id (visible for this player, may be on table though)
	 * 
	 * @param cardsId
	 * @return
	 */
	public CardList getCardListById(String cardsId) {
		for (CardList cs : cardStacks.getCardStacks().values()) {
			if (cs.getId().equals(cardsId)) {
				return cs;
			}
		}
		throw new GameException("Player " + no + " has no cardstack with id="
				+ cardsId);
	}

	/**
	 * Get cardList by a card id (for this player)
	 * 
	 * @param cardsId
	 * @return
	 */
	public CardList getCardListByCardId(String cardId) {
		for (CardList cs : getCardStacks().getCardStacks().values()) {
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
