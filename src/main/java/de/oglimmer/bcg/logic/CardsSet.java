package de.oglimmer.bcg.logic;

import java.util.Collection;
import java.util.Map;

/**
 * A set of cards (cards = set of cards). So this represents a Set of Set of
 * Cards.
 * 
 * @author oli
 * 
 */
public class CardsSet {

	private Map<String, CardList> cards;

	public CardsSet(Game game, Player player, int playerNo,
			Map<String, CardList> cards) {
		this.cards = cards;
	}

	public Collection<CardList> getCardLists() {
		return cards.values();
	}

	public CardList get(String name) {
		return cards.get(name);
	}

}
