package de.oglimmer.bcg.logic;

import de.oglimmer.bcg.util.RandomString;

public class Game {

	private String id;
	private Players players;
	private Board board;

	public Game() {
		this.id = RandomString.getRandomStringASCII(8);
		this.players = new Players(this);
	}

	public void createBoard() {
		if (board == null) {
			this.board = new Board(this);
		}
	}

	public Players getPlayers() {
		return players;
	}

	public String getId() {
		return id;
	}

	public Board getBoard() {
		return board;
	}

	public boolean checkForRemoval() {
		// not all players are created
		if (!getPlayers().isPlayersReady()) {
			GameManager.INSTANCE.remove(this);
			return true;
		}
		// no player is currently connected
		if (!getPlayers().getPlayer(0).isConnected()
				&& !getPlayers().getPlayer(1).isConnected()) {
			GameManager.INSTANCE.remove(this);
			return true;
		}
		return false;
	}

}
