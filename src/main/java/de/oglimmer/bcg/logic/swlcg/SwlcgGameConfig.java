package de.oglimmer.bcg.logic.swlcg;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import de.oglimmer.bcg.logic.config.BoardFactory;
import de.oglimmer.bcg.logic.config.CardsFactory;
import de.oglimmer.bcg.logic.config.DefaultBoardFactory;
import de.oglimmer.bcg.logic.config.GameConfig;
import de.oglimmer.bcg.logic.config.SearchCategory;
import de.oglimmer.bcg.servlet.ServletUtil;
import de.oglimmer.bcg.util.JSONArrayList;

public class SwlcgGameConfig implements GameConfig {

	private static final String GENERATE_DECK_XML_URL = "http://swlcg.oglimmer.de/gen.groovy?affi=%s&cards=%s";

	private static final Logger log = LoggerFactory
			.getLogger(SwlcgGameConfig.class);

	private InfoBoxUpdater infoBoxUpdater = new SwlcgInfoBoxUpdater();
	private CardsFactory cardsFactory = new SwlcgCardsFactory();

	@Override
	public synchronized CardsFactory getCardsFactory() {
		return cardsFactory;
	}

	@Override
	public BoardFactory getBoardFactory() {
		return new DefaultBoardFactory() {
			@Override
			protected void addCardListAssociations(BoardArea ba,
					Player player0, Player player1) {
				if (ba.getName().equals("table")) {
					CardsSet cardStacksPlayer = player0.getCardStacks();
					ba.addCardDeck((CardDeck) cardStacksPlayer.get("discard"));

					cardStacksPlayer = player1.getCardStacks();
					ba.addCardDeck((CardDeck) cardStacksPlayer.get("discard"));
				} else if (ba.getName().equals("hand")) {
					CardsSet cardStacksPlayer = player0.getCardStacks();
					ba.addCardDeck((CardDeck) cardStacksPlayer.get("command"));
					ba.addCardDeck((CardDeck) cardStacksPlayer.get("objective"));
				}
			}
		};
	}

	@Override
	public String getType() {
		return "swlcg";
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
		return side.equalsIgnoreCase("dark") ? SwlcgSide.DARK : SwlcgSide.LIGHT;
	}

	@Override
	public InputStream getDeckStream(String deckId) throws IOException,
			MalformedURLException {
		Database db = ServletUtil.getDatabase();
		Document doc = db.getDocument(deckId);
		if (doc == null) {
			throw new GameException("no deck with id=" + deckId);
		}
		String affi = doc.getString("affiliation").replace(" ", "%20");
		String cards = doc.getString("blocks").replace('-', ',');

		String urlString = String.format(GENERATE_DECK_XML_URL, affi, cards);
		log.debug("url=" + urlString);
		URL url = new URL(urlString);
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

	@Override
	public JSONArrayList<SearchCategory> getSearchCategories() {
		return null;
	}

}
