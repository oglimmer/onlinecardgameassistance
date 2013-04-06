package de.oglimmer.bcg.logic;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.oglimmer.bcg.logic.config.GameConfig;

public class Players {

	private Game game;

	private GameConfig gameConfig;

	private List<Player> players = new ArrayList<>();

	public Players(GameConfig gameConfig, Game game) {
		this.gameConfig = gameConfig;
		this.game = game;
	}

	public synchronized Player createPlayer(String key, Side side,
			InputStream deckStream) {
		Player p = new Player(gameConfig, players.size(), key, game, side,
				deckStream);
		players.add(p);
		return p;
	}

	public Player getPlayer(String playerId) {
		for (Player p : players) {
			if (p.getId().equals(playerId)) {
				return p;
			}
		}
		throw new GameException("No player with id=" + playerId);
	}

	public Player getPlayer(int idx) {
		return players.get(idx);
	}

	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * This method assumes that there are only 2 players! BAD
	 * 
	 * @param player
	 * @return
	 */
	public Player getOther(Player player) {
		Player p1 = players.get(0);
		Player p2 = players.get(1);
		return player == p1 ? p2 : p1;
	}

	public boolean isPlayersReady() {
		return players.size() == 2;
	}

	public boolean contains(Player ply) {
		for (Player p : players) {
			if (p == ply) {
				return true;
			}
		}
		return false;
	}

	public boolean isNobodyConnected() {
		return getCurrentlyConnectec() == 0;
	}

	public int getCurrentlyConnectec() {
		int ret = 0;
		for (Player p : players) {
			if (p.isConnected()) {
				ret++;
			}
		}
		return ret;
	}

}
