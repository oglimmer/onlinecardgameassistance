package de.oglimmer.bcg.logic.config;

import java.io.IOException;
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

import de.oglimmer.bcg.logic.GameException;

abstract public class OctgnCardsFactory extends AbstractCardsFactory {

	private static final Logger log = LoggerFactory
			.getLogger(OctgnCardsFactory.class);

	private static Map<String, String[]> idCardDataCache = new HashMap<>();

	public OctgnCardsFactory() {
		super();
		initIdCardDataCache();
	}

	protected abstract String[] getRefFiles();

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

	protected String getCardnameFromFilename(String cardSetFileName,
			String cardFileName) {
		return cardFileName;
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
	protected List<String[]> getImageUrls(String section, Data data)
			throws ParserConfigurationException, SAXException, IOException {

		List<String[]> ret = new ArrayList<>();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		data.deckStream.reset();// we read more than once
		Document deckDoc = dBuilder.parse(data.deckStream);
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
				String[] cardData = idCardDataCache.get(idAsInRef);
				for (int j = 0; j < Integer.parseInt(qty); j++) {
					ret.add(cardData);
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
						log.debug(cardId + " => " + fileName + "~" + cardName);
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
