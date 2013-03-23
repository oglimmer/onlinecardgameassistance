package de.oglimmer.bcg.logic;

import java.util.Collection;

import net.sf.json.JSONObject;

public class AffiliationCard extends Card {

	public AffiliationCard(Player owner, CardList cl, String imageUrl,
			String backImageUrl) {
		super(owner, cl, imageUrl, backImageUrl);
	}

	@Override
	protected void addMenu(Player player, JSONObject card,
			Collection<String> menu) {
		if (isOwner(player)) {
			menu.add("+1 Resource:modCounter:add-0");
			menu.add("-1 Resource:modCounter:sub-0");
		}
	}
}
