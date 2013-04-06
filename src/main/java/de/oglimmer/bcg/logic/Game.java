package de.oglimmer.bcg.logic;

import java.util.Date;

import de.oglimmer.bcg.logic.config.GameConfig;
import de.oglimmer.bcg.util.RandomString;

public class Game {

	private String id;
	private Players players;
	private Board board;
	private String name;
	private Date created;
	private Date lastAccess;
	private GameConfig gameConfig;

	public Game(GameConfig gameConfig) {
		this.id = RandomString.getRandomStringASCII(8);
		this.name = RandomString.getReadableString(4);
		this.players = new Players(gameConfig, this);
		this.created = new Date();
		this.lastAccess = this.created;
		this.gameConfig = gameConfig;
	}

	public void createBoard() {
		if (board == null) {
			this.board = gameConfig.getBoardFactory().createBoard(gameConfig, this);
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

	public String getName() {
		return name;
	}

	public Date getCreated() {
		return created;
	}

	public Date getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}

}
