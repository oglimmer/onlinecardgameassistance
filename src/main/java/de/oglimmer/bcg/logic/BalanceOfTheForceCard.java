package de.oglimmer.bcg.logic;

import java.util.Collection;

import net.sf.json.JSONObject;

public class BalanceOfTheForceCard extends Card {

	public BalanceOfTheForceCard() {
		super(null, null, "balance_light.jpg", "balance_dark.jpg");
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
