package de.oglimmer.bcg.logic.config;

import java.util.List;

import de.oglimmer.bcg.logic.Board;
import de.oglimmer.bcg.logic.BoardArea;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.logic.Players;
import de.oglimmer.bcg.util.JSONArrayList;

public abstract class DefaultBoardFactory implements BoardFactory {

	protected abstract void addCardListAssociations(BoardArea ba,
			Player player0, Player player1);

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

		addCardListAssociations(mainBa, player0, player1);

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
				.setCss("{\"backgroundColor\":\"black\",\"color\":\"white\", \"height\":\"17%\","
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
		infoBa.setCss("{\"backgroundColor\":\"black\",\"color\":\"white\", \"height\":\"7%\","
				+ "\"width\":\"30%\",\"overflow\":\"auto\",\"float\":\"left\", \"font-family\": \"Arial\", \"font-size\": \"10px\"}");
		areas.add(infoBa);
	}

	private void createPlayerHandArea(JSONArrayList<BoardArea> areas,
			Player player) {
		BoardArea boardArea = new BoardArea("hand");
		List<Player> visiList = boardArea.getVisibleFor();
		visiList.add(player);
		addCardListAssociations(boardArea, player, null);
		boardArea.setCss("{\"backgroundImage\": \"url(images/metal.jpg)\","
				+ "\"height\": \"24%\",\"width\":\"70%\",\"float\":\"left\"}");
		areas.add(boardArea);
	}
}