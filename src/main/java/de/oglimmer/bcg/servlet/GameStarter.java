package de.oglimmer.bcg.servlet;

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

import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.GameException;
import de.oglimmer.bcg.logic.GameManager;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.logic.Side;
import de.oglimmer.bcg.logic.swlcg.SwlcgSide;

public class GameStarter {

	private static final String GENERATE_DECK_XML_URL = "http://swlcg.oglimmer.de/gen.groovy?affi=%s&cards=%s";

	private static final Logger log = LoggerFactory
			.getLogger(GameStarter.class);

	private HttpServletRequest req;

	private Game game;

	private Player player;

	public GameStarter(HttpServletRequest req) {
		this.req = req;
	}

	public void startGame() throws MalformedURLException, IOException {
		String deckId = req.getParameter("deckId");

		Side side = determineDeckSide(req, deckId);

		InputStream deckStream = getDeckStream(deckId);

		String gameId = req.getParameter("gameId");
		if (gameId == null) {
			Document doc = ServletUtil.getDocFromSession(req);
			if (doc != null
					&& Authentication.INSTANCE.checkForAuthorizedUser(doc)) {
				game = GameManager.INSTANCE.createGame(deckStream);
			} else {
				throw new GameException("User not authorized to create a game!");
			}
		} else {
			game = GameManager.INSTANCE.getGame(gameId);
		}

		String email = (String) req.getSession().getAttribute("email");
		player = game.getPlayers().createPlayer(email, side, deckStream);

		if (game.getPlayers().isPlayersReady()) {
			game.createBoard();
		}
	}

	private InputStream getDeckStream(String deckId) throws IOException,
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

	@SuppressWarnings("unchecked")
	private Side determineDeckSide(HttpServletRequest req, String deckId) {
		JSONArray decks = (JSONArray) req.getSession().getAttribute("deckList");
		String side = null;
		for (JSONObject deck : (Collection<JSONObject>) decks) {
			if (deck.getString("id").equals(deckId)) {
				side = deck.getString("side");
			}
		}
		return side.equalsIgnoreCase("dark") ? SwlcgSide.DARK : SwlcgSide.LIGHT;
	}

	public String getGameId() {
		return game.getId();
	}

	public String getGameName() {
		return game.getName();
	}

	public String getPlayerId() {
		return player.getId();
	}

}
