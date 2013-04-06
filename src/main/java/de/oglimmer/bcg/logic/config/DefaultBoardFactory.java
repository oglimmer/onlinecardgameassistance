package de.oglimmer.bcg.logic.config;

import java.util.List;

import de.oglimmer.bcg.logic.Board;
import de.oglimmer.bcg.logic.BoardArea;
import de.oglimmer.bcg.logic.CardDeck;
import de.oglimmer.bcg.logic.CardsSet;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.logic.Players;
import de.oglimmer.bcg.util.JSONArrayList;

public class DefaultBoardFactory implements BoardFactory {

	private static final DefaultBoardFactory singleton = new DefaultBoardFactory();

	public static DefaultBoardFactory getInstance() {
		return singleton;
	}

	public Board createBoard(GameConfig gameConfig, Game game) {
		Players players = game.getPlayers();
		Player player0 = players.getPlayer(0);
		Player player1 = players.getPlayer(1);

		JSONArrayList<BoardArea> areas = new JSONArrayList<>();

		createTableArea(areas, player0, player1);
		createPlayerHandArea(areas, player0);
		createPlayerHandArea(areas, player1);
		createInfoArea(areas, player0, player1);
		createMessageArea(areas, player0, player1);

		return new Board(game, areas);
	}

	private void createTableArea(JSONArrayList<BoardArea> areas,
			Player player0, Player player1) {
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

	private void createMessageArea(JSONArrayList<BoardArea> areas,
			Player player0, Player player1) {
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

	private void createInfoArea(JSONArrayList<BoardArea> areas, Player player0,
			Player player1) {
		List<Player> allPlayers;
		BoardArea infoBa = new BoardArea("info");
		allPlayers = infoBa.getVisibleFor();
		allPlayers.add(player1);
		allPlayers.add(player0);
		infoBa.setCss("{\"backgroundColor\":\"black\",\"color\":\"white\", \"height\":\"4%\","
				+ "\"width\":\"30%\",\"overflow\":\"auto\",\"float\":\"left\", \"font-family\": \"Arial\", \"font-size\": \"10px\"}");
		areas.add(infoBa);
	}

	private void createPlayerHandArea(JSONArrayList<BoardArea> areas,
			Player player) {
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