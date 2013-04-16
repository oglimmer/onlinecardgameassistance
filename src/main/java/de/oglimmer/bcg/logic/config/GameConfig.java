package de.oglimmer.bcg.logic.config;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import de.oglimmer.bcg.logic.Side;
import de.oglimmer.bcg.logic.action.InfoBoxUpdater;
import de.oglimmer.bcg.util.JSONArrayList;

public interface GameConfig {

	CardsFactory getCardsFactory();

	BoardFactory getBoardFactory();

	String getType();

	Side determineDeckSide(HttpServletRequest req, String deckId)
			throws IOException;

	InputStream getDeckStream(String deckId) throws IOException;

	InfoBoxUpdater getInfoBoxUpdater();

	JSONArrayList<SearchCategory> getSearchCategories();
}
