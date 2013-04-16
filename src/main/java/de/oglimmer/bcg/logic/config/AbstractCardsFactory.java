package de.oglimmer.bcg.logic.config;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.CardDeck;
import de.oglimmer.bcg.logic.CardList;
import de.oglimmer.bcg.logic.Player;

public abstract class AbstractCardsFactory {

	private static final Logger log = LoggerFactory
			.getLogger(AbstractCardsFactory.class);

	protected class Data {
		public Data(Player owner, InputStream deckStream,
				String cardBackgroundUrl) {
			this.owner = owner;
			this.deckStream = deckStream;
			this.cardBackgroundUrl = cardBackgroundUrl;
			this.cards = new ArrayList<>();
			this.playerNo = owner.getNo();
		}

		public Player owner;
		public InputStream deckStream;
		public String cardBackgroundUrl;
		public List<CardList> cards;
		public int playerNo;
	}

	public AbstractCardsFactory() {
	}

	protected void addCards(CardList cardList, List<String[]> cardDataList,
			Class<? extends Card> clazz, Data data) {
		try {
			for (String[] cardData : cardDataList) {
				String imageUrl = cardData[0];
				String cardName = cardData[1];
				Card card = clazz.getConstructor(Player.class, CardDeck.class,
						String.class, String.class, String.class, Map.class)
						.newInstance(data.owner, cardList, cardName, imageUrl,
								data.cardBackgroundUrl, null);
				cardList.getCards().add(card);
			}
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			log.error("Failed to add a card", e);
		}
	}

	protected void addCards(int foo, CardList cardList,
			List<Map<String, String>> cardDataList,
			Class<? extends Card> clazz, Data data) {
		try {
			for (Map<String, String> cardData : cardDataList) {
				String imageUrl = cardData.get("ImageFile");
				String cardName = cardData.get("Name");
				Card card = clazz.getConstructor(Player.class, CardDeck.class,
						String.class, String.class, String.class, Map.class)
						.newInstance(data.owner, cardList, cardName, imageUrl,
								data.cardBackgroundUrl, cardData);
				cardList.getCards().add(card);
			}
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			log.error("Failed to add a card", e);
		}
	}
}
