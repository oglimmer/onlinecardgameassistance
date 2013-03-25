package de.oglimmer.bcg.logic;

import java.util.List;

import de.oglimmer.bcg.util.JSONArrayList;

/**
 * This class is a complete mess.
 * 
 * @author oli
 * 
 */
public class Board {

	private JSONArrayList<BoardArea> areas = new JSONArrayList<>();

	private Game game;

	public Board(Game game) {
		this.game = game;
		init();
	}

	public JSONArrayList<BoardArea> getAreas() {
		return areas;
	}

	public BoardArea getArea(String name) {
		if (name.equals("hand")) {
			throw new GameException(
					"Use BoardArea getArea(String name, Player player) for name==hand");
		}
		for (BoardArea ba : areas) {
			if (ba.getName().equals(name)) {
				return ba;
			}
		}
		throw new GameException("No area with name " + name);
	}

	public BoardArea getArea(String name, Player player) {
		for (BoardArea ba : areas) {
			if (ba.getName().equals(name)
					&& ba.getVisibleFor().contains(player)) {
				return ba;
			}
		}
		throw new GameException("No area with name " + name
				+ " visible for player " + player);
	}

	public String getArea(Card card) {
		for (Player p : game.getPlayers().getPlayers()) {
			for (CardList cl : p.getCardStacks().getCardStacks().values()) {
				if (cl.getCards().contains(card)) {
					return cl.getName();
				}
			}
		}
		throw new GameException("No card in game.");
	}

	private void init() {
		Players players = game.getPlayers();
		Player player0 = players.getPlayer(0);
		Player player1 = players.getPlayer(1);

		createTableArea(player0, player1);
		createPlayerHandArea(player0);
		createPlayerHandArea(player1);
		createInfoArea(player0, player1);
	}

	private void createTableArea(Player player0, Player player1) {
		BoardArea mainBa = new BoardArea("table");
		List<Player> allPlayers = mainBa.getVisibleFor();

		allPlayers.add(player1);
		allPlayers.add(player0);

		CardsSet cardStacksPlayer = player0.getCardStacks();
		mainBa.addCardDeck((CardDeck) cardStacksPlayer.get("discard"));

		cardStacksPlayer = player1.getCardStacks();
		mainBa.addCardDeck((CardDeck) cardStacksPlayer.get("discard"));

		mainBa.setCss("{\"backgroundImage\": \"url(images/wood.jpg)\",\"height\": \"76%\"}");
		areas.add(mainBa);
	}

	private void createInfoArea(Player player0, Player player1) {
		List<Player> allPlayers;
		BoardArea infoBa = new BoardArea("info");
		allPlayers = infoBa.getVisibleFor();
		allPlayers.add(player1);
		allPlayers.add(player0);
		infoBa.setCss("{\"backgroundColor\":\"black\",\"color\":\"white\", \"height\":\"24%\","
				+ "\"width\":\"30%\",\"overflow\":\"auto\",\"float\":\"left\"}");
		areas.add(infoBa);
	}

	private void createPlayerHandArea(Player player) {
		BoardArea boardArea = new BoardArea("hand");
		List<Player> visiList = boardArea.getVisibleFor();
		visiList.add(player);
		CardsSet cardStacksPlayer = player.getCardStacks();
		boardArea.addCardDeck((CardDeck) cardStacksPlayer.get("command"));
		boardArea.addCardDeck((CardDeck) cardStacksPlayer.get("objective"));
		boardArea.setCss("{\"backgroundImage\": \"url(images/metal.jpg)\","
				+ "\"height\": \"24%\",\"width\":\"70%\",\"float\":\"left\"}");
		areas.add(boardArea);
	}

}
