package de.oglimmer.bcg.logic.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum GameConfigManager {
	INSTANCE;

	private final Logger log = LoggerFactory.getLogger(GameConfigManager.class);

	private GameConfigManager() {
		try {
			instances.put(
					"swlcg",
					(GameConfig) Class.forName(
							"de.oglimmer.bcg.logic.swlcg.SwlcgGameConfig")
							.newInstance());
			instances.put(
					"swccg",
					(GameConfig) Class.forName(
							"de.oglimmer.bcg.logic.swccg.SwccgGameConfig")
							.newInstance());
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			log.error("Failed to init GameConfigManager", e);
		}
	}

	private Map<String, GameConfig> instances = new HashMap<>();

	public GameConfig getGameConfig(String gametype) {
		return instances.get(gametype);
	}
}
