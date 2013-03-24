package de.oglimmer.bcg.logic;

import java.util.Collection;

import net.sf.json.JSONObject;

public class CommitForceCard extends Card {

	public CommitForceCard(Player player, String imageUrl) {
		super(player, null, imageUrl, imageUrl);
	}

	@Override
	protected void handleJSONPayloadCounter(JSONObject card) {
	}

	@Override
	protected void addMenu(Player player, JSONObject card,
			Collection<String> menu) {
	}

}
