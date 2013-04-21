package de.oglimmer.bcg.logic.swccg;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.CardList;
import de.oglimmer.bcg.logic.CardsSet;
import de.oglimmer.bcg.logic.GameException;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.logic.config.AbstractCardsFactory;
import de.oglimmer.bcg.logic.config.CardsFactory;
import de.oglimmer.bcg.logic.config.SearchCategory;
import de.oglimmer.bcg.util.JSONArrayList;

class SwccgCardsFactory extends AbstractCardsFactory implements CardsFactory {

	private static final String CARDDATA_TXT = "/carddata.txt";

	private static final Logger log = LoggerFactory
			.getLogger(SwccgCardsFactory.class);

	private static final String DEFAULT_BACKGROUND_LIGHT = "misc/light-back.jpg";
	private static final String DEFAULT_BACKGROUND_DARK = "misc/dark-back.jpg";
	static final String EMPTY_CARDS_IMG = "misc/Star Wars LCG - 0000.jpg";

	private Map<String, Map<String, String>> cache = new HashMap<>();

	public SwccgCardsFactory() {
		super();
	}

	public CardsSet createCardsSet(Player player, InputStream deckStream) {
		try {
			Data data = new Data(
					player,
					deckStream,
					(player.getSide() == SwccgSide.DARK ? DEFAULT_BACKGROUND_DARK
							: DEFAULT_BACKGROUND_LIGHT));

			createUniqueTable(data);
			data.cards.add(new CardList(SwccgCardDeck.LISTNAME_HAND));
			createReserveDeck(data);
			createLostPile(data);
			createForcePile(data);
			createUsedPile(data);
			createSearchCategories(player, data);
			return new CardsSet(data.owner.getGame(), data.owner,
					data.playerNo, data.cards);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new GameException("Failed to set JSON data", e);
		}
	}

	private void createSearchCategories(Player player, Data data) {
		addValuesForSearchCategory(data, "Set");
		addValuesForSearchCategory(data, "Category");
	}

	private void addValuesForSearchCategory(Data data, String searchCategoryName) {
		SearchCategory sc = data.owner.getGame().getSearchCategories()
				.getByName(searchCategoryName);
		// VALUES FOR THIS PLAYER
		Set<String> all = new HashSet<>();
		for (CardList cl : data.cards) {
			if (SwccgCardDeck.DECKNAME_RESERVEDECK.equals(cl.getName())) {
				for (Card c : cl.getCards()) {
					all.add(c.getProps().get(searchCategoryName));
				}
			}
		}
		sc.setValues(new JSONArrayList<>(all), data.owner);

		// ALL VALUES
		all = new HashSet<>();
		for (Map<String, String> en : cache.values()) {
			all.add(en.get(searchCategoryName));
		}
		sc.setValues(new JSONArrayList<>(all), null);
	}

	private synchronized CardList createUniqueTable(Data data) {
		CardList table = null;
		for (Player play : data.owner.getGame().getPlayers().getPlayers()) {
			CardList tableList = play.getCardStacks().getByName(
					CardList.LISTNAME_TABLE);
			if (tableList != null) {
				table = tableList;
			}
		}
		if (table != null) {
			data.cards.add(table);
		} else {
			table = new CardList(CardList.LISTNAME_TABLE);
			data.cards.add(table);
		}
		return table;
	}

	private void createReserveDeck(Data data)
			throws ParserConfigurationException, SAXException, IOException {
		CardList commandDeck = new SwccgCardDeck(
				SwccgCardDeck.DECKNAME_RESERVEDECK,
				SwccgCardDeck.DECKDESC_RESERVEDECK, data.owner, 72,
				95 + data.playerNo * 300, false);
		addCards(1, commandDeck, getImageUrls("ReserveDeck", data),
				SwccgCard.class, data);
		Collections.shuffle(commandDeck.getCards());
		data.cards.add(commandDeck);
	}

	private void createLostPile(Data data) throws ParserConfigurationException,
			SAXException, IOException {
		CardList commandDeck = new SwccgCardDeck(
				SwccgCardDeck.DECKNAME_LOSTPILE,
				SwccgCardDeck.DECKDESC_LOSTPILE, data.owner, 172,
				95 + data.playerNo * 300, true);
		data.cards.add(commandDeck);
	}

	private void createForcePile(Data data)
			throws ParserConfigurationException, SAXException, IOException {
		CardList commandDeck = new SwccgCardDeck(
				SwccgCardDeck.DECKNAME_FORCEPILE,
				SwccgCardDeck.DECKDESC_FORCEPILE, data.owner, 7,
				95 + data.playerNo * 300, false);
		data.cards.add(commandDeck);
	}

	private void createUsedPile(Data data) throws ParserConfigurationException,
			SAXException, IOException {
		CardList commandDeck = new SwccgCardDeck(
				SwccgCardDeck.DECKNAME_USEDPILE,
				SwccgCardDeck.DECKDESC_USEDPILE, data.owner, 71,
				5 + data.playerNo * 300, false);
		data.cards.add(commandDeck);
	}

	private List<Map<String, String>> getImageUrls(String section, Data data) {
		initCache();
		List<Map<String, String>> ret = new ArrayList<>();
		String cardList = streamToString(data.deckStream);
		for (String cardData : cardList.split(",")) {
			String[] kv = cardData.split("=");
			for (int i = 0; i < Integer.parseInt(kv[1]); i++) {
				Map<String, String> props = cache.get(kv[0]);
				ret.add(props);
			}
		}

		return ret;
	}

	private static String streamToString(InputStream deckStream) {
		StringBuilder ret = new StringBuilder();
		try {
			deckStream.reset();
			byte[] buff = new byte[1024];
			int len = 0;
			while ((len = deckStream.read(buff)) > -1) {
				ret.append(new String(buff, 0, len));
			}
		} catch (IOException e) {
			log.error("Failed to load deck from deckStream", e);
		}
		return ret.toString();
	}

	private String removeNonAscii(String str) {
		return str.replaceAll("[^a-zA-Z0-9]", "");
	}

	private synchronized void initCache() {
		if (!cache.isEmpty()) {
			return;
		}

		final String[] SEARCH_CATEGORIES = new String[] { "Name", "Set",
				"ImageFile", "Side", "Category", "Destiny", "Rarity",
				"Restrictions", "Stats", "Deploy", "Forfeit", "Icons", "Text" };

		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new DataInputStream(this.getClass().getResourceAsStream(
						CARDDATA_TXT))))) {
			String strLine;
			while ((strLine = br.readLine()) != null) {
				String[] tmp = strLine.split("\t");

				String Set = tmp[1].trim();
				if (!Set.startsWith("Virtual")) {
					String Name = tmp[0].trim();
					String Side = tmp[3].trim();
					String[] CategoryPrep = tmp[4].split(" -- ");
					String ImageFile = tmp[2].trim();
					ImageFile = ImageFile.substring(ImageFile.indexOf("-") + 1);
					ImageFile = Set + "-" + Side + "/" + ImageFile + ".gif";

					Map<String, String> props = new HashMap<>();
					for (int i = 0; i < SEARCH_CATEGORIES.length; i++) {
						props.put(SEARCH_CATEGORIES[i], tmp[i].trim());
					}
					props.put("ImageFile", ImageFile);

					props.put("Category",
							removeNonAscii(CategoryPrep[0].trim()));
					if (CategoryPrep.length > 1) {
						props.put("Subcategory",
								removeNonAscii(CategoryPrep[1].trim()));
					}

					String id = removeNonAscii(Name + Set + Side);
					cache.put(id, props);
				}
			}

		} catch (IOException e) {
			log.error("Failed to init cache", e);
		}
	}
}
