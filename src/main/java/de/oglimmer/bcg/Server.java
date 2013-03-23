package de.oglimmer.bcg;

import java.net.InetSocketAddress;
import java.nio.channels.NotYetConnectedException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oglimmer.bcg.com.ActionMessage;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.com.ComConst;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.GameException;
import de.oglimmer.bcg.logic.GameManager;
import de.oglimmer.bcg.logic.Player;

public class Server extends WebSocketServer implements ClientChannel {

	private static final Logger log = LoggerFactory.getLogger(Server.class);

	private Map<Player, WebSocket> commMap = new HashMap<>();
	private ExecutorService executorService = Executors.newFixedThreadPool(30);

	public Server(InetSocketAddress isa) {
		super(isa);
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		// no code here
		log.debug("Con opened from " + conn.getRemoteSocketAddress().toString());
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		log.debug("onClose " + code + " from webSocketMap " + reason);
		synchronized (commMap) {
			Player toRemove = null;
			for (Map.Entry<Player, WebSocket> en : commMap.entrySet()) {
				if (en.getValue() == conn) {
					toRemove = en.getKey();
				}
			}
			if (toRemove != null) {
				toRemove.setConnected(false);
				log.debug("removed player " + toRemove + " from webSocketMap");
				commMap.remove(toRemove);
				sendPlayerLeftMsg(toRemove);
			}
		}
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		try {
			JSONObject jsonInput = JSONObject.fromObject(message);
			ActionMessage am = ActionMessage.getInstance(jsonInput, this);
			if (!commMap.containsKey(am.getPlayer())) {
				synchronized (commMap) {
					log.debug("added player " + am.getPlayer()
							+ " to webSocketMap");
					commMap.put(am.getPlayer(), conn);
				}
				am.getPlayer().setConnected(true);
				sendPlayerJoinedMsg(am);
			}
			executorService.submit(am);
		} catch (RuntimeException e) {
			log.error(e.toString(), e);
			handleExcpetion(conn, e);
		}
	}

	public void handleExcpetion(WebSocket conn, Exception e) {
		try {
			JSONArray arr = new JSONArray();
			JSONObject error = new JSONObject();
			error.element(ComConst.RO_ERROR, e.toString());
			arr.add(error);
			conn.send(arr.toString());
		} catch (NotYetConnectedException | IllegalArgumentException
				| JSONException e1) {
			log.error(e1.toString(), e1);
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		log.error("Exception caught", ex);
	}

	@Override
	public void send(Player p, JSONArray message) {
		try {
			String str = message.toString();
			log.debug("response to " + p.getNo() + ": " + str);
			WebSocket ws = commMap.get(p);
			if (ws == null) {
				throw new GameException("Player " + p
						+ " is not registered with a WebSocket");
			}
			ws.send(str);
		} catch (NotYetConnectedException | IllegalArgumentException e) {
			log.error(e.toString(), e);
		}
	}

	private void sendPlayerLeftMsg(Player toRemove) {
		Game game = GameManager.INSTANCE.getGame(toRemove);
		if (game != null) {
			if (!game.checkForRemoval()) {
				JSONArray msg = new JSONArray();
				JSONObject m = new JSONObject();
				JSONObject mO = new JSONObject();
				m.element("infoText", "Other player left");
				mO.element("message", m);
				msg.add(mO);
				send(game.getPlayers().getOther(toRemove), msg);
			}
		}
	}

	private void sendPlayerJoinedMsg(ActionMessage am) {
		if (am.getGame().getPlayers().isPlayersReady()) {
			JSONArray jsonArr = new JSONArray();
			JSONObject outerMsg = new JSONObject();
			JSONObject msg = new JSONObject();
			outerMsg.element("infoText", "Other player joined");
			msg.element("message", outerMsg);
			jsonArr.add(msg);
			Player otherPlayer = am.getGame().getPlayers()
					.getOther(am.getPlayer());
			if (otherPlayer.isConnected()) {
				send(otherPlayer, jsonArr);
			}
		}
	}
}