package de.oglimmer.bcg.logic.action;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.Player;

public class PreinitAction extends AbstractAction implements Action {

	@Override
	public void execute(Game game, final Player player, JSONObject parameters,
			ClientChannel cc) {

		if (game.getPlayers().isPlayersReady()) {
			send(player, cc, "init", true);
			send(game.getPlayers().getOther(player), cc, "init", true);
		}
	}

}
