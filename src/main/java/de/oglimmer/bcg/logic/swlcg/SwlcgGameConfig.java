package de.oglimmer.bcg.logic.swlcg;

import java.io.InputStream;

import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.logic.config.BoardFactory;
import de.oglimmer.bcg.logic.config.CardsFactory;
import de.oglimmer.bcg.logic.config.DefaultBoardFactory;
import de.oglimmer.bcg.logic.config.GameConfig;

public class SwlcgGameConfig implements GameConfig {

	@Override
	public CardsFactory getCardsFactory(Game game, Player player,
			InputStream deckStream) {
		return new SwlcgCardsFactory(game, player, deckStream);
	}

	@Override
	public BoardFactory getBoardFactory() {
		return DefaultBoardFactory.getInstance();
	}

}
