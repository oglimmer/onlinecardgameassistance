package de.oglimmer.bcg.logic.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.CardList;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.GameException;
import de.oglimmer.bcg.logic.Player;

public abstract class AbstractCardsFactory {

	private static final Logger log = LoggerFactory
			.getLogger(AbstractCardsFactory.class);

	private static Map<String, String[]> idCardDataCache = new HashMap<>();

	protected Game game;
	protected Player owner;
	protected Map<String, CardList> cards;
	protected String cardBackgroundUrl;
	protected InputStream deckStream;
	protected int playerNo;

	public AbstractCardsFactory(Game game, Player owner,
			InputStream deckStream, String cardBackgroundUrl) {
		this.game = game;
		this.owner = owner;
		this.cards = new HashMap<>();
		this.deckStream = deckStream;
		this.playerNo = owner.getNo();
		this.cardBackgroundUrl = cardBackgroundUrl;
		initIdCardDataCache();
	}

	protected abstract String[] getRefFiles();

	protected void addCards(CardList cardList, List<String[]> cardData,
			Class<? extends Card> clazz) {
		try {
			for (String[] data : cardData) {
				String imageUrl = data[0];
				String cardName = data[1];
				Card card = clazz.getConstructor(Player.class, CardList.class,
						String.class, String.class, String.class).newInstance(
						owner, cardList, cardName, imageUrl, cardBackgroundUrl);
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

	private String getCardnameFromFilename(String cardSetFileName,
			String cardFileName) {
		StringBuilder tmp = new StringBuilder();
		String cardSetName = cardSetFileName.substring(1).toLowerCase();
		cardSetName = cardSetName.substring(0, cardSetName.indexOf("."));
		// remove the set name, card number and file extension from the card
		// file name
		for (int j = cardFileName.length() - 1; j > 0; j--) {
			if (cardFileName.substring(j).startsWith(cardSetName)) {
				tmp.append(cardFileName.substring(0, j - 1));
			}
		}
		// make it nicely readable (remove hyphens and make Camelcase
		StringBuilder cardName = new StringBuilder();
		for (String s : tmp.toString().split("-")) {
			if (cardName.length() > 0) {
				cardName.append(' ');
			}
			cardName.append(s.substring(0, 1).toUpperCase() + s.substring(1));
		}
		return cardName.toString();
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
	protected List<String[]> getImageUrls(String section)
			throws ParserConfigurationException, SAXException, IOException {

		List<String[]> ret = new ArrayList<>();

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
				String[] data = idCardDataCache.get(idAsInRef);
				for (int j = 0; j < Integer.parseInt(qty); j++) {
					ret.add(data);
				}
			}
		}

		return ret;
	}

	/**
	 * Reads XML and inits the Id->Filename/Cardname mapping
	 * 
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	private synchronized void initIdCardDataCache() {

		try {
			if (idCardDataCache.isEmpty()) {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

				for (String refFile : getRefFiles()) {
					Document doc = dBuilder.parse(this.getClass()
							.getResourceAsStream(refFile));
					doc.getDocumentElement().normalize();

					NodeList nl = doc.getElementsByTagName("Relationship");
					for (int i = 0; i < nl.getLength(); i++) {
						Node n = nl.item(i);
						String fileName = n.getAttributes()
								.getNamedItem("Target").getNodeValue();
						if (fileName.startsWith("/cards/")) {
							fileName = fileName.substring(7);
						}

						String cardName = getCardnameFromFilename(refFile,
								fileName);

						String cardId = n.getAttributes().getNamedItem("Id")
								.getNodeValue();

						idCardDataCache.put(cardId, new String[] { fileName,
								cardName });
					}
				}
			}
		} catch (DOMException | ParserConfigurationException | SAXException
				| IOException e) {
			throw new GameException("Failed to init idImageCache", e);
		}

	}

}
