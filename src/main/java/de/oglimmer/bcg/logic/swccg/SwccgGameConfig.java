package de.oglimmer.bcg.logic.swccg;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;

import de.oglimmer.bcg.logic.BoardArea;
import de.oglimmer.bcg.logic.CardDeck;
import de.oglimmer.bcg.logic.CardsSet;
import de.oglimmer.bcg.logic.GameException;
import de.oglimmer.bcg.logic.GameManager;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.logic.Side;
import de.oglimmer.bcg.logic.action.InfoBoxUpdater;
import de.oglimmer.bcg.logic.config.CardsFactory;
import de.oglimmer.bcg.logic.config.DefaultBoardFactory;
import de.oglimmer.bcg.logic.config.GameConfig;
import de.oglimmer.bcg.logic.config.SearchCategory;
import de.oglimmer.bcg.servlet.ServletUtil;
import de.oglimmer.bcg.util.JSONArrayList;

public class SwccgGameConfig implements GameConfig {

	private BoardFactory baordFactory = new BoardFactory();
	private CardsFactory cardsFactory = new SwccgCardsFactory();
	private SwccgInfoBoxUpdater infoBoxUpdater = new SwccgInfoBoxUpdater();

	public SwccgGameConfig() {
	}

	@Override
	public CardsFactory getCardsFactory() {
		return cardsFactory;
	}

	@Override
	public BoardFactory getBoardFactory() {
		return baordFactory;
	}

	@Override
	public String getType() {
		return "swccg";
	}

	public JSONArrayList<SearchCategory> getSearchCategories() {
		JSONArrayList<SearchCategory> searchCategories = new JSONArrayList<>();
		searchCategories.add(new SearchCategory("Name", "TEXT"));
		searchCategories.add(new SearchCategory("Set", "LIST"));
		searchCategories.add(new SearchCategory("Category", "LIST"));
		searchCategories.add(new SearchCategory("Destiny", "TEXT"));
		searchCategories.add(new SearchCategory("Restrictions", "TEXT"));
		searchCategories.add(new SearchCategory("Stats", "TEXT"));
		searchCategories.add(new SearchCategory("Deploy", "TEXT"));
		searchCategories.add(new SearchCategory("Forfeit", "TEXT"));
		searchCategories.add(new SearchCategory("Icons", "TEXT"));
		searchCategories.add(new SearchCategory("Text", "TEXT"));
		return searchCategories;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Side determineDeckSide(HttpServletRequest req, String deckId)
			throws IOException {
		String gametype = req.getParameter("gametype");
		if (gametype == null) {
			gametype = GameManager.INSTANCE.getGame(req.getParameter("gameId"))
					.getType();
		}
		ServletUtil.loadDeckList(gametype, req);
		JSONArray decks = (JSONArray) req.getAttribute("deckList");
		String side = null;
		for (JSONObject deck : (Collection<JSONObject>) decks) {
			if (deck.getString("id").equals(deckId)) {
				side = deck.getString("side");
			}
		}
		return side.equalsIgnoreCase("dark") ? SwccgSide.DARK : SwccgSide.LIGHT;
	}

	@Override
	public InputStream getDeckStream(String deckId) throws IOException,
			MalformedURLException {
		Database db = ServletUtil.getDatabase();
		Document doc = db.getDocument(deckId);
		if (doc == null) {
			throw new GameException("no deck with id=" + deckId);
		}

		return new ByteArrayInputStream(doc.getString("blocks").getBytes());
	}

	@Override
	public InfoBoxUpdater getInfoBoxUpdater() {
		return infoBoxUpdater;
	}

	private static class BoardFactory extends DefaultBoardFactory {
		@Override
		protected void addCardListAssociations(BoardArea ba, Player player0,
				Player player1) {
			if (CardDeck.LISTNAME_TABLE.equals(ba.getName())) {
				associateDecksWithBoard(ba, player0.getCardStacks());
				associateDecksWithBoard(ba, player1.getCardStacks());
			}
		}

		private void associateDecksWithBoard(BoardArea ba,
				CardsSet cardStacksPlayer) {
			ba.addCardDeck((CardDeck) cardStacksPlayer
					.get(SwccgCardDeck.DECKNAME_RESERVEDECK));
			ba.addCardDeck((CardDeck) cardStacksPlayer
					.get(SwccgCardDeck.DECKNAME_LOSTPILE));
			ba.addCardDeck((CardDeck) cardStacksPlayer
					.get(SwccgCardDeck.DECKNAME_USEDPILE));
			ba.addCardDeck((CardDeck) cardStacksPlayer
					.get(SwccgCardDeck.DECKNAME_FORCEPILE));
		}
	}
}
