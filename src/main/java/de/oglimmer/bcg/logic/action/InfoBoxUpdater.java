package de.oglimmer.bcg.logic.action;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.logic.Player;

public interface InfoBoxUpdater {

	void addInfoText(Player otherPlayer, JSONObject cardJSON);

}
