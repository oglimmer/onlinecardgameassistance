package de.oglimmer.bcg.logic.action;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.CardDeck;
import de.oglimmer.bcg.logic.CardList;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.Player;

public class SearchOperateOnResultAction extends AbstractAction {

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String deckId = parameters.getString("deckId");
		String operation = parameters.getString("operation");
		JSONArray value = parameters.getJSONArray("items");
		CardDeck cardDeck = (CardDeck) player.getCardListById(deckId);
		CardList targetDeck = player.getCardListById(operation);

		for (int i = 0; i < value.size(); i++) {
			String s = value.getString(i);
			Card card = player.getCard(s);
			if (targetDeck.getName().equals(CardList.LISTNAME_HAND)) {
				DeckToHandAction dtha = (DeckToHandAction) ActionFactory.INSTANCE
						.getAction("deckToHand");
				if (cardDeck.getCards().remove(card)) {
					dtha.moveCardToHand(game, player, cc, cardDeck, card);
				}
			} else if (targetDeck.getName().equals(CardList.LISTNAME_TABLE)) {
				DeckToTableAction dtha = (DeckToTableAction) ActionFactory.INSTANCE
						.getAction("deckToTable");
				if (cardDeck.getCards().remove(card)) {
					dtha.moveCardToTable(game, player, cc, cardDeck, card, true);
				}
			} else {
				DeckToDeckAction dtha = (DeckToDeckAction) ActionFactory.INSTANCE
						.getAction("deckToDeck");
				if (cardDeck.getCards().remove(card)) {
					dtha.moveCardToDeck(game, player, cc, card, cardDeck,
							(CardDeck) targetDeck, "bottom");
				}
			}
		}
	}

}
