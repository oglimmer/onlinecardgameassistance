package de.oglimmer.bcg.logic;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CardsFactory {

	private static final String COMMITED_FORCE_LIGHT = "Star Wars LCG Light - 0000b.jpg";
	private static final String COMMITED_FORCE_DARK = "Star Wars LCG Dark - 0000b.jpg";
	private static final String DEFAULT_BACKGROUND_LIGHT = "Star Wars LCG Light - 0001.jpg";
	private static final String DEFAULT_BACKGROUND_DARK = "Star Wars LCG Dark - 0001.jpg";
	public static final String EMPTY_CARDS_IMG = "Star Wars LCG - 0000.jpg";
	private static final Logger log = LoggerFactory
			.getLogger(CardsFactory.class);

	private Game game;
	private Player owner;
	private Map<String, CardList> cards;
	private String cardBackgroundUrl;
	private String forceImageUrl;
	private InputStream deckStream;
	private int playerNo;

	public CardsFactory(Game game, Player owner, Map<String, CardList> cards,
			InputStream deckStream) {

		this.game = game;
		this.owner = owner;
		this.cards = cards;
		this.deckStream = deckStream;
		this.cardBackgroundUrl = (owner.getSide() == Side.DARK ? DEFAULT_BACKGROUND_DARK
				: DEFAULT_BACKGROUND_LIGHT);
		this.forceImageUrl = (owner.getSide() == Side.DARK ? COMMITED_FORCE_DARK
				: COMMITED_FORCE_LIGHT);
		this.playerNo = owner.getNo();
	}

	public void createDecks() {
		try {
			createUniqueTable();
			cards.put("hand", new CardList("hand"));
			createObjectiveDeck();
			createCommandDeck();
			handleAffiliationDeck();
			createDiscardDeck();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			log.error("Failed to set JSON data", e);
		}
	}

	private synchronized void createUniqueTable() {
		CardList table = null;
		for (Player play : game.getPlayers().getPlayers()) {
			CardList tableList = play.getCardStacks().get("table");
			if (tableList != null) {
				table = tableList;
			}
		}
		if (table != null) {
			cards.put("table", table);
		} else {
			cards.put("table", new CardList("table"));
			Card balanceCard = new BalanceOfTheForceCard();
			balanceCard.setFaceup(true);
			balanceCard.setX(10);
			balanceCard.setY(298);
			cards.get("table").getCards().add(balanceCard);

			Card deathStarDialCard = new DeathStarDialCard();
			deathStarDialCard.setFaceup(true);
			deathStarDialCard.setX(5);
			deathStarDialCard.setY(364);
			cards.get("table").getCards().add(deathStarDialCard);
		}
	}

	private void createDiscardDeck() {
		int discardDeckPos = 100 + 100 * playerNo;
		CardList discardDeck = new CardDeck(CardDeck.DECKNAME_DISCARD, owner,
				10, discardDeckPos, true);
		cards.put("discard", discardDeck);
	}

	private void handleAffiliationDeck() throws ParserConfigurationException,
			SAXException, IOException {
		CardList affiliationDeck = new CardList("affiliation");
		addCards(affiliationDeck, getImageUrls("Affiliation"),
				AffiliationCard.class);
		// cards.put("affiliation", affiliationDeck);
		// special case: affiliationDeck has only one card and that one goes
		// directly to the table
		Card affiCard = affiliationDeck.getCards().get(0);
		affiCard.setFaceup(true);
		affiCard.setX(affiCard.getX() + 200 * playerNo);
		cards.get("table").getCards().add(affiCard);

		for (int i = 0; i < 3; i++) {
			Card force = new CommitForceCard(owner, forceImageUrl);
			force.setFaceup(true);
			force.setY(200);
			force.setX(force.getX() + 200 * playerNo + i * 20);
			cards.get("table").getCards().add(force);
		}
	}

	private void createCommandDeck() throws ParserConfigurationException,
			SAXException, IOException {
		CardList commandDeck = new CardDeck(CardDeck.DECKNAME_COMMAND, owner,
				10, 100, false);
		addCards(commandDeck, getImageUrls("Command Deck"), Card.class);
		Collections.shuffle(commandDeck.getCards());
		cards.put("command", commandDeck);
	}

	private void createObjectiveDeck() throws ParserConfigurationException,
			SAXException, IOException {
		CardList objectiveDeck = new CardDeck(CardDeck.DECKNAME_OBJECTIVE,
				owner, 10, 10, false);
		addCards(objectiveDeck, getImageUrls("Objective Deck"), Card.class);
		Collections.shuffle(objectiveDeck.getCards());
		cards.put("objective", objectiveDeck);
	}

	private void addCards(CardList cardList, List<String> cardImageUrls,
			Class<? extends Card> clazz) {
		try {
			for (String cardImage : cardImageUrls) {
				Card card = clazz.getConstructor(Player.class, CardList.class,
						String.class, String.class).newInstance(owner,
						cardList, cardImage, cardBackgroundUrl);
				cardList.getCards().add(card);
			}
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			log.error("Failed to add a card", e);
		}
	}

	/**
	 * XML helper
	 * 
	 * @param sectionName
	 * @param doc
	 * @return
	 */
	private Node getSection(String sectionName, Document doc) {
		NodeList nl = doc.getElementsByTagName("section");
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n.getAttributes().getNamedItem("name").getNodeValue()
					.equals(sectionName)) {
				return n;
			}
		}
		throw new GameException("couldn't find " + sectionName + " section");
	}

	/**
	 * Reads XML and returns Id->Filename mapping
	 * 
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	private Map<String, String> getIdFilenameMapping() throws SAXException,
			IOException, ParserConfigurationException {
		Map<String, String> ret = new HashMap<>();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(this.getClass().getResourceAsStream(
				"/Core.xml"));
		doc.getDocumentElement().normalize();

		NodeList nl = doc.getElementsByTagName("Relationship");
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			String longFileName = n.getAttributes().getNamedItem("Target")
					.getNodeValue();
			if (longFileName.startsWith("/cards/")) {
				longFileName = longFileName.substring(7);
			}
			ret.put(n.getAttributes().getNamedItem("Id").getNodeValue(),
					longFileName);
		}

		return ret;
	}

	/**
	 * Returns a list of imageUrls for a given deck aka section
	 * 
	 * @param section
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private List<String> getImageUrls(String section)
			throws ParserConfigurationException, SAXException, IOException {

		Map<String, String> idMap = getIdFilenameMapping();

		List<String> ret = new ArrayList<>();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		deckStream.reset();// we read more than once
		Document deckDoc = dBuilder.parse(deckStream);
		deckDoc.getDocumentElement().normalize();

		Node node = getSection(section, deckDoc);
		NodeList nl = node.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n.getNodeName().equals("card")) {
				String qty = n.getAttributes().getNamedItem("qty")
						.getNodeValue();
				String idAsInDef = n.getAttributes().getNamedItem("id")
						.getNodeValue();
				String idAsInRef = "C" + idAsInDef.replace("-", "");
				String filename = idMap.get(idAsInRef);
				for (int j = 0; j < Integer.parseInt(qty); j++) {
					ret.add(filename);
				}
			}
		}

		return ret;
	}

}
