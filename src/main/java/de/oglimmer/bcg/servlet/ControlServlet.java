package de.oglimmer.bcg.servlet;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;

import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.GameManager;
import de.oglimmer.bcg.util.RandomString;

@SuppressWarnings("serial")
public class ControlServlet extends HttpServlet {

	private static final Logger log = LoggerFactory
			.getLogger(ControlServlet.class);

	private String adminPassword;

	public ControlServlet() {
		adminPassword = RandomString.getRandomStringASCII(8);
		log.error("adminPassword=" + adminPassword);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		restoreSessionFromContext(req);

		String jspPage = null;
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
			} else if ("game".equals(path)) {
				jspPage = "game.html";
			} else if ("admin".equals(path)
					&& adminPassword.equals(req.getParameter("pass"))) {
				jspPage = "admin.jsp";
			} else if ("adminDel".equals(path)
					&& adminPassword.equals(req.getParameter("pass"))) {
				Game game = GameManager.INSTANCE.getGame(req
						.getParameter("gameId"));
				GameManager.INSTANCE.remove(game);
				jspPage = "admin.jsp";
			} else if ("portal".equals(path)) {
				if (req.getParameter("logoff") != null) {
					CrossContextSession.INSTANCE.invalidateAllSessions(req);
					jspPage = "index.jsp";
				} else {
					jspPage = "portal.jsp";
				}
			} else {
				jspPage = "portal.jsp";
			}
		}
		if (jspPage != null) {
			req.getRequestDispatcher("/WEB-INF/html/" + jspPage).forward(req,
					resp);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		restoreSessionFromContext(req);

		if (req.getSession().getAttribute("email") != null) {
			// if already logged in, don't log in again
			req.getRequestDispatcher("/WEB-INF/html/portal.jsp").forward(req,
					resp);
			return;
		}

		String path = getRequestPageName(req);
		if ("login".equals(path)) {

			Database db = ServletUtil.getDatabase();

			String email = req.getParameter("email").toLowerCase();
			String password = req.getParameter("password");

			Document doc = db.getDocument(email);
			if (doc != null
					&& Authentication.INSTANCE.checkPassword(password, doc)) {
				HttpSession session = req.getSession();
				session.setAttribute("email", email);
				setAdditionalSessionData(doc, session);

				CrossContextSession.INSTANCE.saveSessionToServletContext(req);

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

	private void restoreSessionFromContext(HttpServletRequest req)
			throws IOException {
		if (CrossContextSession.INSTANCE.retrieveSessionFromServletContext(req)) {
			HttpSession session = req.getSession();
			String email = (String) session.getAttribute("email");

			Database db = ServletUtil.getDatabase();
			Document doc = db.getDocument((String) email);

			setAdditionalSessionData(doc, session);
		}
	}

	private void setAdditionalSessionData(Document doc, HttpSession session)
			throws IOException {
		session.setAttribute("deckList", doc.get("deckList"));
		session.setAttribute("permissionStartGame",
				Authentication.INSTANCE.checkForAuthorizedUser(doc));
	}

	private void reloadDeckList(HttpServletRequest req) throws IOException {
		Document doc = ServletUtil.getDocFromSession(req);
		if (doc != null) {
			setAdditionalSessionData(doc, req.getSession());
		}
	}

	private void prepareCommand(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, MalformedURLException {

		GameStarter gs = new GameStarter(req);

		gs.startGame();

		String url = String.format("game.htm?gameId=%s&playerId=%s&name=%s",
				gs.getGameId(), gs.getPlayerId(), gs.getGameName());

		resp.sendRedirect(url);
	}

	private String getRequestPageName(HttpServletRequest req) {
		String path = req.getRequestURI().substring(
				req.getContextPath().length() + 1);
		return path.substring(0, path.lastIndexOf('.'));
	}

}
