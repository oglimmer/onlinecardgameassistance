package de.oglimmer.bcg.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum GameManager {
	INSTANCE;

	private static final Logger log = LoggerFactory
			.getLogger(GameManager.class);

	private Map<String, Game> games = new HashMap<>();

	private GameManager() {
	}

	public Game getGame(String id) {
		Game game = games.get(id);
		if (game == null) {
			throw new GameException("There is no game with id='" + id + "'");
		}
		return game;
	}

	public Game createGame() {
		Game game = new Game();
		games.put(game.getId(), game);
		return game;
	}

	public Collection<Game> getAllGames() {
		Collection<Game> ret = new ArrayList<>();
		for (Game g : games.values()) {
			if (!g.getPlayers().isPlayersReady()) {
				ret.add(g);
			}
		}
		return ret;
	}

	public Game getGame(Player ply) {
		for (Game g : games.values()) {
			if (g.getPlayers().contains(ply)) {
				return g;
			}
		}
		return null;
	}

	public void remove(Game game) {
		for (Iterator<Map.Entry<String, Game>> it = games.entrySet().iterator(); it
				.hasNext();) {
			Map.Entry<String, Game> me = it.next();
			if (me.getValue() == game) {
				log.debug("Removed game " + me.getKey());
				it.remove();
			}
		}
	}
}
