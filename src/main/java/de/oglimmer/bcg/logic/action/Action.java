package de.oglimmer.bcg.logic.action;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.Player;

public interface Action {

	void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc);

}
