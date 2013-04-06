package de.oglimmer.bcg.logic.config;

import de.oglimmer.bcg.logic.Board;
import de.oglimmer.bcg.logic.Game;

public interface BoardFactory {

	Board createBoard(GameConfig gameConfig, Game game);

}
