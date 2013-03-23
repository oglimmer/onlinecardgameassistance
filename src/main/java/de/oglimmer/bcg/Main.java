package de.oglimmer.bcg;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String... args) throws InterruptedException {
		Main m = new Main();
		m.startUp();
		synchronized (m) {
			m.wait();
		}
	}

	private Server server;

	public void startUp() {
		WebSocket.DEBUG = false;
		server = new Server(new InetSocketAddress("127.0.0.1", 8082));
		server.start();
		log.info("Server started on port: " + server.getPort());
	}

	public void shutDown() {
		try {
			server.stop();
			log.info("Server stopped");
		} catch (IOException | InterruptedException e) {
			log.error(e.toString(), e);
		}
	}

}