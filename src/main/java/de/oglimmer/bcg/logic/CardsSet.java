package de.oglimmer.bcg.logic;

import java.util.Collection;
import java.util.List;

/**
 * A set of cards (cards = set of cards). So this represents a Set of Set of
 * Cards.
 * 
 * @author oli
 * 
 */
public class CardsSet {

	private List<CardList> cards;

	public CardsSet(Game game, Player player, int playerNo, List<CardList> cards) {
		this.cards = cards;
	}

	public Collection<CardList> getCardLists() {
		return cards;
	}

	public CardList getByName(String name) {
		for (CardList cl : cards) {
			if (cl.getName().equals(name)) {
				return cl;
			}
		}
		return null;
	}

	/**
	 * Get cardList where a certain card is located (for this player)
	 * 
	 * @param cardsId
	 * @return
	 */
	public CardList getByCardId(String cardId) {
		for (CardList cs : cards) {
			for (Card c : cs.getCards()) {
				if (c.getId().equals(cardId)) {
					return cs;
				}
			}
		}
		throw new GameException("No cardstack with a card id=" + cardId);
	}

	/**
	 * Get cardList by id (visible for this player, may be on table though)
	 * 
	 * @param cardsId
	 * @return
	 */
	public CardList getById(String cardsId) {
		for (CardList cs : cards) {
			if (cs.getId().equals(cardsId)) {
				return cs;
			}
		}
		return null;
	}

	/**
	 * Get card by id (visible for this player, may be on table though)
	 * 
	 * @param cardId
	 * @return
	 */
	public Card getCard(String cardId) {
		Card c = getCardById(cardId);
		if (c == null) {
			throw new GameException("Player has no card with id=" + cardId);
		}
		return c;
	}

	public Card getCardById(String cardId) {
		for (CardList cs : cards) {
			for (Card c : cs.getCards()) {
				if (c.getId().equals(cardId)) {
					return c;
				}
			}
		}
		return null;
	}
}
