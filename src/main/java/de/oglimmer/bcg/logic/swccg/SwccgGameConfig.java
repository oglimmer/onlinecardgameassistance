package de.oglimmer.bcg.logic.swccg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import de.oglimmer.bcg.logic.BoardArea;
import de.oglimmer.bcg.logic.CardDeck;
import de.oglimmer.bcg.logic.CardsSet;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.GameManager;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.logic.Side;
import de.oglimmer.bcg.logic.action.InfoBoxUpdater;
import de.oglimmer.bcg.logic.config.CardsFactory;
import de.oglimmer.bcg.logic.config.DefaultBoardFactory;
import de.oglimmer.bcg.logic.config.GameConfig;
import de.oglimmer.bcg.servlet.ServletUtil;

public class SwccgGameConfig implements GameConfig {

	private BoardFactory bf = new BoardFactory();
	private SwccgInfoBoxUpdater infoBoxUpdater = new SwccgInfoBoxUpdater();

	@Override
	public CardsFactory getCardsFactory(Game game, Player player,
			InputStream deckStream) {
		return new SwccgCardsFactory(game, player, deckStream);
	}

	@Override
	public BoardFactory getBoardFactory() {
		return bf;
	}

	@Override
	public String getType() {
		return "swccg";
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
		// Database db = ServletUtil.getDatabase();
		// Document doc = db.getDocument(deckId);
		// if (doc == null) {
		// throw new GameException("no deck with id=" + deckId);
		// }

		URL url = new URL("file:///Users/oli/Desktop/" + deckId);
		URLConnection con = url.openConnection();
		con.setUseCaches(false);
		return copyContentAsBufferedInputStream(con);
	}

	private InputStream copyContentAsBufferedInputStream(URLConnection con)
			throws IOException {
		ByteArrayOutputStream baos;
		try (InputStream is = con.getInputStream()) {
			byte[] buff = new byte[2048];
			int len = 0;
			baos = new ByteArrayOutputStream(2048);
			while ((len = is.read(buff)) > -1) {
				baos.write(buff, 0, len);
			}
		}
		return new ByteArrayInputStream(baos.toByteArray());
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
