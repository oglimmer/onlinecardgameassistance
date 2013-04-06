package de.oglimmer.bcg.logic.config;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.oglimmer.bcg.logic.GameException;

public enum GameConfigManager {
	INSTANCE;

	public GameConfig getGameConfig(InputStream is) {
		String gameId = readGameIdFromStream(is);
		return gameConfigFromId(gameId);
	}

	private String readGameIdFromStream(InputStream is) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			is.reset();
			Document deckDoc = dBuilder.parse(is);
			deckDoc.getDocumentElement().normalize();

			NodeList nl = deckDoc.getElementsByTagName("deck");
			if (nl.getLength() != 1) {
				throw new GameException("Invalid doc");
			}
			String gameId = null;
			Node n = nl.item(0);
			for (int i = 0; i < n.getAttributes().getLength(); i++) {
				Node attrNode = n.getAttributes().item(i);
				if (attrNode.getNodeName().equals("game")) {
					gameId = attrNode.getNodeValue();
				}
			}
			if (gameId == null) {
				throw new GameException(
						"Unable to get gameId from deck def file");
			}
			return gameId;
		} catch (IOException | SAXException | ParserConfigurationException e) {
			throw new GameException("Failed to init gameconfig", e);
		}
	}

	private GameConfig gameConfigFromId(String gameId) {
		try {
			switch (gameId) {
			case "d5cf89e5-1984-4873-8ae0-f06eea411bb3":
				return (GameConfig) Class.forName(
						"de.oglimmer.bcg.logic.swlcg.SwlcgGameConfig")
						.newInstance();
			}
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			e.printStackTrace();
		}
		throw new GameException("No game with id=" + gameId);
	}

}
