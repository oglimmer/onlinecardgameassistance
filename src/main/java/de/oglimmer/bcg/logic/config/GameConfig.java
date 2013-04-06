package de.oglimmer.bcg.logic.config;

import java.io.InputStream;

import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.Player;

public interface GameConfig {

	CardsFactory getCardsFactory(Game game, Player player,
			InputStream deckStream);

	BoardFactory getBoardFactory();

}
