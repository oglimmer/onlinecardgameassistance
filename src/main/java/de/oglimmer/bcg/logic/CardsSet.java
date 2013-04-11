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

	public CardList get(String name) {
		for (CardList cl : cards) {
			if (cl.getName().equals(name)) {
				return cl;
			}
		}
		return null;
	}

}
