package de.oglimmer.bcg.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oglimmer.bcg.logic.config.GameConfig;

public enum GameManager {
	INSTANCE;

	private static final Logger log = LoggerFactory
			.getLogger(GameManager.class);

	private Map<String, Game> games = new HashMap<>();

	private GameManager() {
	}

	public synchronized Game getGame(String id) {
		Game game = games.get(id);
		if (game == null) {
			throw new GameException("There is no game with id='" + id + "'");
		}
		game.setLastAccess(new Date());
		return game;
	}

	public synchronized Game createGame(GameConfig gameConfig) {
		Game game = new Game(gameConfig);
		games.put(game.getId(), game);
		return game;
	}

	public synchronized Collection<Game> getOpenGames() {
		Collection<Game> ret = new ArrayList<>();
		for (Iterator<Game> it = games.values().iterator(); it.hasNext();) {
			Game g = it.next();
			// remove games which are orphaned for more than a day
			if (System.currentTimeMillis() - g.getLastAccess().getTime() > 1000 * 60 * 60 * 24
					&& g.getPlayers().isNobodyConnected()) {
				log.debug("Removed game " + g);
				it.remove();
			} else if (!g.getPlayers().isPlayersReady()) {
				ret.add(g);
			}
		}
		return ret;
	}

	public synchronized Collection<Game> getRunningGames() {
		Collection<Game> ret = new ArrayList<>();
		for (Game g : games.values()) {
			if (g.getPlayers().isPlayersReady()) {
				ret.add(g);
			}
		}
		return ret;
	}

	public synchronized Game getGame(Player ply) {
		for (Game g : games.values()) {
			if (g.getPlayers().contains(ply)) {
				return g;
			}
		}
		return null;
	}

	public synchronized Collection<Player> getGamesRegistered(String playerKey) {
		Collection<Player> ret = new ArrayList<>();
		for (Game g : games.values()) {
			for (Player p : g.getPlayers().getPlayers()) {
				if (p.getKey().equals(playerKey)) {
					ret.add(p);
				}
			}
		}
		return ret;
	}

	public synchronized void remove(Game game) {
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
