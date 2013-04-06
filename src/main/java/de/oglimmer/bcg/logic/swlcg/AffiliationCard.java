package de.oglimmer.bcg.logic.swlcg;

import java.util.Collection;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.logic.CardList;
import de.oglimmer.bcg.logic.Player;

public class AffiliationCard extends SwlcgCard {

	public AffiliationCard(Player owner, CardList cl, String name,
			String imageUrl, String backImageUrl) {
		super(owner, cl, "Affilication", imageUrl, backImageUrl);
	}

	@Override
	protected void addMenu(Player player, JSONObject card,
			Collection<String> menu) {
		if (isOwner(player)) {
			menu.add("+1 Focus:modCounter:add-0");
			menu.add("-1 Focus:modCounter:sub-0");
			menu.add("-");
			menu.add("Global refresh:refresh");
		}
	}
}
