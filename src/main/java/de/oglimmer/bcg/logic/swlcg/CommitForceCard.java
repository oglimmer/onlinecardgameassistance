package de.oglimmer.bcg.logic.swlcg;

import java.util.Collection;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.logic.Player;

public class CommitForceCard extends SwlcgCard {

	public CommitForceCard(Player player, String imageUrl) {
		super(player, null, "Committed to the Force card", imageUrl, imageUrl);
	}

	@Override
	protected void handleJSONPayloadCounter(JSONObject card) {
	}

	@Override
	protected void addMenu(Player player, JSONObject card,
			Collection<String> menu) {
	}

}
