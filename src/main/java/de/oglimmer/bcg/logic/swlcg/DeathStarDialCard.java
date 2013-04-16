package de.oglimmer.bcg.logic.swlcg;

import java.util.Collection;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.logic.Player;

public class DeathStarDialCard extends SwlcgObjectiveCard {

	public DeathStarDialCard() {
		super(null, null, "Death Star Dial Card", "death-star-dial.png",
				"death-star-dial.png", null);
	}

	@Override
	protected void addMenu(Player player, JSONObject card,
			Collection<String> menu) {
		menu.add("Change counter +1:modCounter:add-0");
		menu.add("Change counter -1:modCounter:sub-0");
	}

	@Override
	protected void handleJSONPayloadCounter(JSONObject card) {
		card.element("counter0", getCounter(0));
	}

	@Override
	protected void handleJSONPayloadBase(Player player, JSONObject card) {
		super.handleJSONPayloadBase(player, card);
		card.element("counterPosX", "68");
		card.element("counterPosY", "18");
	}

}
