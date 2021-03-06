package de.oglimmer.bcg.logic.action;

import java.util.ArrayList;
import java.util.Collection;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.CardDeck;
import de.oglimmer.bcg.logic.CardList;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.Player;

public class SearchStartAction extends AbstractAction {

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {
		String deckId = parameters.getString("entityId");

		Player deckOwner = player;
		CardDeck cardDeck = (CardDeck) player.getCardStacks().getById(deckId);
		if (cardDeck == null) {
			deckOwner = game.getPlayers().getOther(player);
			cardDeck = (CardDeck) deckOwner.getCardStacks().getById(deckId);
		}

		JSONObject obj = new JSONObject();
		JSONArray data = game.getSearchCategories().toJsonArray(
				deckOwner == player ? player : null);
		obj.element("searchCategories", data);
		obj.element("deckId", cardDeck.getId());
		obj.element("targets", getTargets(cardDeck, deckOwner));

		send(player, cc, "searchStart", obj);
	}

	private Collection<String[]> getTargets(CardDeck cardDeck, Player player) {
		Collection<String[]> ret = new ArrayList<>();
		for (CardList cl : player.getCardStacks().getCardLists()) {
			if (cl != cardDeck) {
				ret.add(new String[] { cl.getId(), cl.getName() });
			}
		}
		return ret;
	}
}
