package de.oglimmer.bcg.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import de.oglimmer.bcg.Main;

public class GameContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		Main.INSTANCE.startUp();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Main.INSTANCE.shutDown();
	}

}