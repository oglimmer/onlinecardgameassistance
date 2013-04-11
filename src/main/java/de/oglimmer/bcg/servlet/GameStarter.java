package de.oglimmer.bcg.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import com.fourspaces.couchdb.Document;

import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.GameException;
import de.oglimmer.bcg.logic.GameManager;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.logic.Side;
import de.oglimmer.bcg.logic.config.GameConfig;
import de.oglimmer.bcg.logic.config.GameConfigManager;

public class GameStarter {

	private HttpServletRequest req;

	private Game game;

	private Player player;

	public GameStarter(HttpServletRequest req) {
		this.req = req;
	}

	public String getGameId() {
		return game.getId();
	}

	public String getGameName() {
		return game.getName();
	}

	public String getPlayerId() {
		return player.getId();
	}

	public void startGame() throws MalformedURLException, IOException {
		String deckId = req.getParameter("deckId");
		String gameId = req.getParameter("gameId");

		GameConfig gameConfig;
		if (gameId == null) {
			Document doc = ServletUtil.getDocFromSession(req);
			if (doc != null
					&& Authentication.INSTANCE.checkForAuthorizedUser(doc)) {

				String gametype = req.getParameter("gametype");
				gameConfig = GameConfigManager.INSTANCE.getGameConfig(gametype);
				game = GameManager.INSTANCE.createGame(gameConfig);
			} else {
				throw new GameException("User not authorized to create a game!");
			}
		} else {
			game = GameManager.INSTANCE.getGame(gameId);
			gameConfig = game.getGameConfig();
		}

		String email = (String) req.getSession().getAttribute("email");
		Side side = gameConfig.determineDeckSide(req, deckId);
		InputStream deckStream = gameConfig.getDeckStream(deckId);
		player = game.getPlayers().createPlayer(email, side, deckStream);

		if (game.getPlayers().isPlayersReady()) {
			game.createBoard();
		}
	}

}
