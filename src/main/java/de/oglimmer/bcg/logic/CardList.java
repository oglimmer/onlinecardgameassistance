package de.oglimmer.bcg.logic;

import java.util.ArrayList;
import java.util.List;

import de.oglimmer.bcg.util.RandomString;

public class CardList {

	// private static final Logger log = LoggerFactory.getLogger(Cards.class);

	private String id;
	private String name;
	private List<Card> cards = new ArrayList<>();

	public CardList(String name) {
		this.id = RandomString.getRandomStringASCII(8);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<Card> getCards() {
		return cards;
	}

	public String getId() {
		return id;
	}

}
