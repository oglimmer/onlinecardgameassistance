package de.oglimmer.bcg.com;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.GameManager;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.logic.action.Action;
import de.oglimmer.bcg.logic.action.ActionFactory;
import de.oglimmer.bcg.logic.action.PingAction;

public class ActionMessage implements Runnable {

	private static final Logger log = LoggerFactory
			.getLogger(ActionMessage.class);

	private Player player;
	private Game game;
	private ClientChannel clientChannel;
	private Action action;
	private JSONObject param;

	public static ActionMessage getInstance(JSONObject jsonInput,
			ClientChannel clientChannel) {
		ActionMessage am = new ActionMessage();
		am.action = ActionFactory.INSTANCE.getAction(jsonInput
				.getString(ComConst.REQ_ACTION_ID));
		am.game = GameManager.INSTANCE.getGame(jsonInput
				.getString(ComConst.REQ_GAME_ID));
		am.player = am.game.getPlayers().getPlayer(
				jsonInput.getString(ComConst.REQ_PLAYER_ID));
		am.param = jsonInput;
		am.clientChannel = clientChannel;

		if (!(am.action instanceof PingAction)) {
			log.debug("got action:" + jsonInput);
		}

		return am;
	}

	public Player getPlayer() {
		return player;
	}

	public Game getGame() {
		return game;
	}

	@Override
	public void run() {
		try {
			action.execute(game, player, param, clientChannel);
		} catch (RuntimeException e) {
			log.error("Failed to run action " + action.getClass().getName(), e);
		}
	}

}
