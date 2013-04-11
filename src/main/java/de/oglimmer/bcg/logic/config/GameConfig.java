package de.oglimmer.bcg.logic.config;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.logic.Side;
import de.oglimmer.bcg.logic.action.InfoBoxUpdater;

public interface GameConfig {

	CardsFactory getCardsFactory(Game game, Player player,
			InputStream deckStream);

	BoardFactory getBoardFactory();

	String getType();

	Side determineDeckSide(HttpServletRequest req, String deckId)
			throws IOException;

	InputStream getDeckStream(String deckId) throws IOException;

	InfoBoxUpdater getInfoBoxUpdater();
}
