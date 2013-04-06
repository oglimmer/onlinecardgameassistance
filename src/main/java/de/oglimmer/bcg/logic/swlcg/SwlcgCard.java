package de.oglimmer.bcg.logic.swlcg;

import java.util.Collection;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.CardList;
import de.oglimmer.bcg.logic.Player;

public class SwlcgCard extends Card {

	public SwlcgCard(Player owner, CardList origin, String name,
			String imageUrl, String backImageUrl) {
		super(owner, origin, name, imageUrl, backImageUrl);
	}

	protected void addMenu(Player player, JSONObject card,
			Collection<String> menu) {
		String areaOfCard = player.getGame().getBoard().getCardList(this);
		if ("table".equals(areaOfCard)) {
			menu.add("Toggle highlight:toggleHighlight");
		}
		if (owner == null || owner == player) {
			if (!faceup) {
				if (!menu.isEmpty()) {
					menu.add("-");
				}
				menu.add("Face up:flipCard");
			}
			switch (areaOfCard) {
			case "hand":
				if (!menu.isEmpty()) {
					menu.add("-");
				}
				menu.add("Play card face up on table:handToTable:up");
				menu.add("Play card face down on table:handToTable:down");
				menu.add("-");
				menu.add("Put card on top of " + origin.getName()
						+ ":returnToDeck:origin_top");
				menu.add("Put card under " + origin.getName()
						+ ":returnToDeck:origin_bottom");
				break;
			case "table":
				if (!menu.isEmpty()) {
					menu.add("-");
				}
				menu.add("Rotate card:rotateCard");
				menu.add("-");
				menu.add("Put card on top of " + origin.getName()
						+ ":returnToDeck:origin_top");
				menu.add("Put card under " + origin.getName()
						+ ":returnToDeck:origin_bottom");
				menu.add("-");
				menu.add("Take back into hand:tableToHand");
				menu.add("-");
				menu.add("+1 Focus:modCounter:add-0");
				menu.add("-1 Focus:modCounter:sub-0");
				menu.add("+1 Damage:modCounter:add-1");
				menu.add("-1 Damage:modCounter:sub-1");
				menu.add("+1 Shield:modCounter:add-2");
				menu.add("-1 Shield:modCounter:sub-2");
				break;
			}
			if (!menu.isEmpty()) {
				menu.add("-");
			}
			menu.add("Discard card:returnToDeck:discard_top");
		}
		if ("table".equals(areaOfCard)) {
			if (!menu.isEmpty()) {
				menu.add("-");
			}
			menu.add("Bring to foreground:changeZIndex:up");
			menu.add("Bring to background:changeZIndex:down");
		}
	}
}
