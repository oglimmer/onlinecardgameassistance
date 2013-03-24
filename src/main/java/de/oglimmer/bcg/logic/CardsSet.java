package de.oglimmer.bcg.logic;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A set of cards (cards = set of cards). So this represents a Set of Set of
 * Cards.
 * 
 * @author oli
 * 
 */
public class CardsSet {

	private Map<String, CardList> cards = new HashMap<>();

	public CardsSet(Game game, Player player, InputStream deckStream,
			int playerNo) {
		CardsFactory cf = new CardsFactory(game, player, cards, deckStream);
		cf.createDecks();
	}

	public Map<String, CardList> getCardStacks() {
		return cards;
	}

	public CardList get(String name) {
		return cards.get(name);
	}

}
