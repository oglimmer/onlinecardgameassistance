package de.oglimmer.bcg.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;
import com.fourspaces.couchdb.Session;

import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.GameException;
import de.oglimmer.bcg.logic.GameManager;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.logic.Side;

@SuppressWarnings("serial")
public class ControlServlet extends HttpServlet {

	private static final Logger log = LoggerFactory
			.getLogger(ControlServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String jspPage;
		if (req.getSession().getAttribute("email") == null) {
			jspPage = "index.jsp";
		} else {
			String path = getRequestPageName(req);
			if ("start".equals(path)) {
				reloadDeckList(req);
				jspPage = "start.jsp";
			} else if ("join".equals(path)) {
				reloadDeckList(req);
				jspPage = "join.jsp";
			} else if ("prepare".equals(path)) {
				prepareCommand(req, resp);
				return;
			} else if ("game".equals(path)) {
				jspPage = "game.html";
			} else {
				jspPage = "portal.jsp";
			}
		}
		req.getRequestDispatcher("/WEB-INF/html/" + jspPage).forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String path = getRequestPageName(req);
		if ("login".equals(path)) {

			Database db = getDatabase();

			String email = req.getParameter("email").toLowerCase();
			String password = req.getParameter("password");

			Document doc = db.getDocument(email);
			if (doc != null && checkPassword(password, doc)) {
				req.getSession().setAttribute("email", email);
				req.getSession().setAttribute("deckList", doc.get("deckList"));
				req.getSession().setAttribute("permissionStartGame",
						checkForAuthorizedUser(doc));
				req.getRequestDispatcher("/WEB-INF/html/portal.jsp").forward(
						req, resp);
			} else {
				req.setAttribute("reason", "No user or wrong password!");
				req.getRequestDispatcher("/WEB-INF/html/index.jsp").forward(
						req, resp);
			}
		} else {
			req.getRequestDispatcher("/WEB-INF/html/index.jsp").forward(req,
					resp);
		}
	}

	private boolean checkForAuthorizedUser(Document doc) {
		return "57363895957".equals(doc.optString("picPath"));
	}

	private void prepareCommand(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, MalformedURLException {

		String deckId = req.getParameter("deckId");

		Side side = determineDeckSide(req, deckId);

		InputStream deckStream = getDeckStream(deckId);

		String gameId = req.getParameter("gameId");
		Game game;
		if (gameId == null) {
			Document doc = getDocFromSession(req);
			if (doc != null && checkForAuthorizedUser(doc)) {
				game = GameManager.INSTANCE.createGame();
			} else {
				throw new GameException("User not authorized to create a game!");
			}
		} else {
			game = GameManager.INSTANCE.getGame(gameId);
		}

		Player player = game.getPlayers().createPlayer(
				(String) req.getSession().getAttribute("email"), side,
				deckStream);

		if (game.getPlayers().isPlayersReady()) {
			game.createBoard();
		}

		resp.sendRedirect("game.htm?gameId=" + game.getId() + "&playerId="
				+ player.getId() + "&name=" + game.getName());
	}

	private Document getDocFromSession(HttpServletRequest req)
			throws IOException {
		String email = (String) req.getSession().getAttribute("email");
		Database db = getDatabase();
		return db.getDocument(email);
	}

	private InputStream getDeckStream(String deckId) throws IOException,
			MalformedURLException {
		Database db = getDatabase();
		Document doc = db.getDocument(deckId);
		if (doc == null) {
			throw new GameException("no deck with id=" + deckId);
		}
		String affi = doc.getString("affiliation").replace(" ", "%20");
		String cards = doc.getString("blocks").replace('-', ',');

		String urlString = "http://swlcg.oglimmer.de/gen.groovy?affi=" + affi
				+ "&cards=" + cards;
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
		return side.equalsIgnoreCase("dark") ? Side.DARK : Side.LIGHT;
	}

	private Database getDatabase() {
		Session s = new Session("localhost", 5984);
		Database db = s.getDatabase("swlcg");
		return db;
	}

	private String getRequestPageName(HttpServletRequest req) {
		String path = req.getRequestURI().substring(
				req.getContextPath().length() + 1);
		path = path.substring(0, path.lastIndexOf('.'));
		return path;
	}

	private boolean checkPassword(String password, Document doc) {
		JSONArray passwordJSON = doc.getJSONArray("password");
		boolean passGood = false;
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
			byte[] data = messageDigest.digest(password.getBytes());
			if (passwordJSON.size() == data.length) {
				passGood = true;
				for (int i = 0; i < passwordJSON.size(); i++) {
					if (data[i] != (byte) passwordJSON.getInt(i)) {
						passGood = false;
					}
				}
			}
		} catch (NoSuchAlgorithmException e) {
			log.error("Failed to checkPassword", e);
		}
		return passGood;
	}

	private void reloadDeckList(HttpServletRequest req) throws IOException {
		Document doc = getDocFromSession(req);
		if (doc != null) {
			req.getSession().setAttribute("deckList", doc.get("deckList"));
		}

	}
}
