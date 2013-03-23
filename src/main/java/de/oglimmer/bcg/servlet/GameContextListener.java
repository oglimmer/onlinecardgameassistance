package de.oglimmer.bcg.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import de.oglimmer.bcg.Main;

public class GameContextListener implements ServletContextListener {

	private Main main;

	public GameContextListener() {
		main = new Main();
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		main.startUp();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		main.shutDown();
		// GameManager.getInstance().clear();
	}

}