package de.oglimmer.bcg.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;
import com.fourspaces.couchdb.Session;

public class ServletUtil {

	static Document getDocFromSession(HttpServletRequest req)
			throws IOException {
		String email = (String) req.getSession().getAttribute("email");
		Database db = getDatabase();
		return db.getDocument(email);
	}

	public static Database getDatabase() {
		Session s = new Session("localhost", 5984);
		return s.getDatabase("swlcg");
	}

	public static void loadDeckList(String gametype, HttpServletRequest req)
			throws IOException {
		if (req.getAttribute("deckList") == null) {
			Document doc = ServletUtil.getDocFromSession(req);
			Object decklist = doc.get("deckList_" + gametype);
			req.setAttribute("deckList", decklist);
		}
	}
}
