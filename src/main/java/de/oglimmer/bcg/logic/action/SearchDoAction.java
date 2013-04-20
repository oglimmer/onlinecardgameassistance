package de.oglimmer.bcg.logic.action;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.CardDeck;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.logic.config.SearchCategory;

public class SearchDoAction extends AbstractAction {

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {
		String deckId = parameters.getString("deckId");
		String category = parameters.getString("category");
		String value = parameters.getString("value");

		String textAdd, textAdd2;
		CardDeck cardDeck = (CardDeck) player.getCardStacks().getById(deckId);
		if (cardDeck == null) {
			Player deckOwner = game.getPlayers().getOther(player);
			cardDeck = (CardDeck) deckOwner.getCardStacks().getById(deckId);
			textAdd = "<span style='color:red'>your</span>";
			textAdd2 = "opponent's";
		} else {
			textAdd = "his";
			textAdd2 = "your";
		}

		List<JSONObject> data = new ArrayList<>();
		SearchCategory sc = game.getSearchCategories().getByName(category);
		if (sc.getType() == SearchCategory.Type.NUM) {
			int num = Integer.parseInt(value);
			for (int i = 0; i < num; i++) {
				Card c = cardDeck.getCards().get(i);
				JSONObject cardJSON = new JSONObject();
				cardJSON.element("id", c.getId());
				cardJSON.element("imageUrl", c.getImageUrl());
				data.add(cardJSON);
			}
		} else {

			for (Card c : cardDeck.getCards()) {
				String catValueForCard = c.getProps().get(category);

				if (catValueForCard.matches("(?i)" + value)) {
					JSONObject cardJSON = new JSONObject();
					cardJSON.element("id", c.getId());
					cardJSON.element("imageUrl", c.getImageUrl());
					data.add(cardJSON);
				}
			}
		}

		List<Object[]> msg = new ArrayList<>();
		msg.add(new Object[] { "searchResult", data });

		addMessage(player, cc, msg,
				"You searched " + textAdd2 + " " + cardDeck.getDescription()
						+ " for " + category + " = `" + value + "`");

		send(player, cc, msg);
		sendMessage(
				game.getPlayers().getOther(player),
				cc,
				"Opponent searched " + textAdd + " "
						+ cardDeck.getDescription() + " for " + category
						+ " = `" + value + "`");
	}
}
