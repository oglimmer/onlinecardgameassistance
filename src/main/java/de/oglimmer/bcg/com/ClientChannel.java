package de.oglimmer.bcg.com;

import net.sf.json.JSONArray;
import de.oglimmer.bcg.logic.Player;

public interface ClientChannel {

	void send(Player p, JSONArray message);

}
