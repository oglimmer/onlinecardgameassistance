package de.oglimmer.bcg.logic.action;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.Player;

public class PreinitAction extends AbstractAction implements Action {

	@Override
	public void execute(Game game, final Player player, JSONObject parameters,
			ClientChannel cc) {

		int browserWidth = parameters.getInt("browserWidth");
		int browserHeight = parameters.getInt("browserHeight");
		player.setBrowserWidth(browserWidth);
		player.setBrowserHeight(browserHeight);

		if (game.getPlayers().isPlayersReady()) {
			Player otherPlayer = game.getPlayers().getOther(player);

			JSONObject op = new JSONObject();
			op.element("browserWidth", otherPlayer.getBrowserWidth());
			op.element("browserHeight", otherPlayer.getBrowserHeight());

			send(player, cc, "init", op);

			JSONObject op2 = new JSONObject();
			op2.element("browserWidth", player.getBrowserWidth());
			op2.element("browserHeight", player.getBrowserHeight());

			send(otherPlayer, cc, "init", op2);
		}
	}

}
