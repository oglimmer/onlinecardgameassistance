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
import de.oglimmer.bcg.logic.GameManager;
import de.oglimmer.bcg.logic.Player;

@SuppressWarnings("serial")
public class ControlServlet extends HttpServlet {

	private static final Logger log = LoggerFactory
			.getLogger(ControlServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String jspPage;
		if (req.getSession().getAttribute("email") == null) {
			jspPage = "index.html";
		} else {
			String path = req.getRequestURI().substring(
					req.getContextPath().length() + 1);
			path = path.substring(0, path.lastIndexOf('.'));
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

	private void prepareCommand(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, MalformedURLException {
		String deckId = req.getParameter("deckId");
		JSONArray decks = (JSONArray) req.getSession().getAttribute("deckList");
		String side = null;
		for (Object o : decks) {
			JSONObject oo = (JSONObject) o;
			if (oo.getString("id").equals(deckId)) {
				side = oo.getString("side");
			}
		}

		Session s = new Session("localhost", 5984);
		Database db = s.getDatabase("swlcg");

		Document doc = db.getDocument(deckId);
		if (doc == null) {
			log.debug("no deck with id=" + deckId);
			return;
		}
		String affi = doc.getString("affiliation");
		String cards = doc.getString("blocks");

		String urlString = "http://swlcg.oglimmer.de/gen.groovy?affi="
				+ affi.replace(" ", "%20") + "&cards="
				+ cards.replace('-', ',');
		log.debug("url=" + urlString);
		URL url = new URL(urlString);
		URLConnection con = url.openConnection();
		con.setUseCaches(false);
		InputStream is = con.getInputStream();
		byte[] buff = new byte[2048];
		int len = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
		while ((len = is.read(buff)) > -1) {
			baos.write(buff, 0, len);
		}
		is.close();

		String gameId = req.getParameter("gameId");
		Game game;
		if (gameId == null) {
			game = GameManager.INSTANCE.createGame();
		} else {
			game = GameManager.INSTANCE.getGame(gameId);
		}
		Player p = game.getPlayers().createPlayer(
				side,
				side.equals("Dark") ? "Star Wars LCG Dark - 0001.jpg"
						: "Star Wars LCG Light - 0001.jpg",
				side.equals("Dark") ? "Star Wars LCG Dark - 0000b.jpg"
						: "Star Wars LCG Light - 0000b.jpg",
				new ByteArrayInputStream(baos.toByteArray()));

		if (game.getPlayers().isPlayersReady()) {
			game.createBoard();
		}

		resp.sendRedirect("game.htm?gameId=" + game.getId() + "&playerId="
				+ p.getId());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Session s = new Session("localhost", 5984);
		Database db = s.getDatabase("swlcg");

		String email = req.getParameter("email").toLowerCase();
		String password = req.getParameter("password");

		Document doc = db.getDocument(email);
		boolean passGood = false;
		if (doc != null) {
			JSONArray passwordJSON = doc.getJSONArray("password");

			MessageDigest messageDigest;
			try {
				messageDigest = MessageDigest.getInstance("SHA1");
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
				e.printStackTrace();
			}
		}
		if (passGood) {
			req.getSession().setAttribute("email", email);
			req.getSession().setAttribute("deckList", doc.get("deckList"));
			req.getRequestDispatcher("/WEB-INF/html/portal.jsp").forward(req,
					resp);
		} else {
			req.getRequestDispatcher("/WEB-INF/html/index.html").forward(req,
					resp);
		}

	}

	protected void reloadDeckList(HttpServletRequest req) throws IOException {

		Session s = new Session("localhost", 5984);
		Database db = s.getDatabase("swlcg");

		String email = (String) req.getSession().getAttribute("email");

		Document doc = db.getDocument(email);
		if (doc != null) {
			req.getSession().setAttribute("deckList", doc.get("deckList"));
		}

	}
}
