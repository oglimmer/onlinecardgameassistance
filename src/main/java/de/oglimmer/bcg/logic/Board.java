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

	public String getCardList(Card card) {
		for (Player p : game.getPlayers().getPlayers()) {
			for (CardList cl : p.getCardStacks().getCardStacks().values()) {
				if (cl.getCards().contains(card)) {
					return cl.getName();
				}
			}
		}
		throw new GameException("No card in game with id = " + card.getId());
	}

	private void init() {
		Players players = game.getPlayers();
		Player player0 = players.getPlayer(0);
		Player player1 = players.getPlayer(1);

		createTableArea(player0, player1);
		createPlayerHandArea(player0);
		createPlayerHandArea(player1);
		createInfoArea(player0, player1);
		createMessageArea(player0, player1);
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

	private void createMessageArea(Player player0, Player player1) {
		List<Player> allPlayers;
		BoardArea messageBA = new BoardArea("messages");
		allPlayers = messageBA.getVisibleFor();
		allPlayers.add(player1);
		allPlayers.add(player0);
		messageBA
				.setCss("{\"backgroundColor\":\"black\",\"color\":\"white\", \"height\":\"20%\","
						+ "\"width\":\"30%\",\"overflow\":\"auto\",\"float\":\"left\"}");
		areas.add(messageBA);
	}

	private void createInfoArea(Player player0, Player player1) {
		List<Player> allPlayers;
		BoardArea infoBa = new BoardArea("info");
		allPlayers = infoBa.getVisibleFor();
		allPlayers.add(player1);
		allPlayers.add(player0);
		infoBa.setCss("{\"backgroundColor\":\"black\",\"color\":\"white\", \"height\":\"4%\","
				+ "\"width\":\"30%\",\"overflow\":\"auto\",\"float\":\"left\", \"font-family\": \"Arial\", \"font-size\": \"10px\"}");
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
