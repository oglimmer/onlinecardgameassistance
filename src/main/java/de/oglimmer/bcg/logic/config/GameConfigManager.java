package de.oglimmer.bcg.logic.config;

import de.oglimmer.bcg.logic.GameException;

public enum GameConfigManager {
	INSTANCE;

	public GameConfig getGameConfig(String gametype) {
		try {
			String clazz;
			switch (gametype) {
			case "swlcg":
				clazz = "de.oglimmer.bcg.logic.swlcg.SwlcgGameConfig";
				break;
			case "swccg":
				clazz = "de.oglimmer.bcg.logic.swccg.SwccgGameConfig";
				break;
			default:
				throw new GameException("No game with gametype=" + gametype);
			}
			return (GameConfig) Class.forName(clazz).newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			throw new GameException("No game with gametype=" + gametype, e);
		}
	}
}
