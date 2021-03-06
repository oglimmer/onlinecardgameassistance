package de.oglimmer.bcg.logic.swlcg;

import java.util.Collection;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.logic.Player;

class BalanceOfTheForceCard extends SwlcgObjectiveCard {

	public BalanceOfTheForceCard() {
		super(null, null, "Balance of the Force Card", "balance_light.jpg",
				"balance_dark.jpg", null);
	}

	@Override
	protected void handleJSONPayloadCounter(JSONObject card) {
	}

	@Override
	protected void addMenu(Player player, JSONObject card,
			Collection<String> menu) {
		if (!isFaceup()) {
			menu.add("Face up:flipCard");
		} else {
			menu.add("Face down:flipCard");
		}
	}
}
