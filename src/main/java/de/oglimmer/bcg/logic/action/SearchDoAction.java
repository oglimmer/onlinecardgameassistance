package de.oglimmer.bcg.logic.action;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.CardDeck;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.Player;

public class SearchDoAction extends AbstractAction {

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {
		String deckId = parameters.getString("deckId");
		String category = parameters.getString("category");
		String value = parameters.getString("value");
		CardDeck cardDeck = (CardDeck) player.getCardListById(deckId);

		List<JSONObject> data = new ArrayList<>();
		for (Card c : cardDeck.getCards()) {
			String catValueForCard = c.getProps().get(category);

			if (catValueForCard.matches("(?i)" + value)) {
				JSONObject cardJSON = new JSONObject();
				cardJSON.element("id", c.getId());
				cardJSON.element("imageUrl", c.getImageUrl());
				data.add(cardJSON);
			}
		}

		List<Object[]> msg = new ArrayList<>();
		msg.add(new Object[] { "searchResult", data });

		addMessage(player, cc, msg,
				"You searched the " + cardDeck.getDescription() + " for "
						+ category + " = `" + value+"`");

		send(player, cc, msg);
		sendMessage(game.getPlayers().getOther(player), cc,
				"Opponent searched the " + cardDeck.getDescription() + " for "
						+ category + " = `" + value+"`");
	}
}
