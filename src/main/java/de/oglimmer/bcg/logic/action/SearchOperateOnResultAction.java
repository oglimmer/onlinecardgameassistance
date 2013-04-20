package de.oglimmer.bcg.logic.action;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.CardDeck;
import de.oglimmer.bcg.logic.CardList;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.GameException;
import de.oglimmer.bcg.logic.Player;

public class SearchOperateOnResultAction extends AbstractAction {

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String deckId = parameters.getString("deckId");
		String operation = parameters.getString("operation");
		JSONArray value = parameters.getJSONArray("items");

		Player deckOwner = player;

		CardDeck sourceDeck = (CardDeck) player.getCardStacks().getById(deckId);
		CardList targetDeck = player.getCardStacks().getById(operation);

		if (sourceDeck == null/* targetDeck might be not null if it is the table */) {
			deckOwner = game.getPlayers().getOther(player);
			sourceDeck = (CardDeck) deckOwner.getCardStacks().getById(deckId);
			targetDeck = deckOwner.getCardStacks().getById(operation);
		}

		if (sourceDeck == null || targetDeck == null) {
			throw new GameException("Error with deckIds");
		}

		for (int i = 0; i < value.size(); i++) {
			String s = value.getString(i);
			Card card = deckOwner.getCardStacks().getCard(s);

			if (targetDeck.getName().equals(CardList.LISTNAME_HAND)) {
				DeckToHandAction dtha = (DeckToHandAction) ActionFactory.INSTANCE
						.getAction("deckToHand");
				if (sourceDeck.getCards().remove(card)) {
					dtha.moveCardToHand(game, deckOwner, cc, sourceDeck, card);
				}
			}

			else if (targetDeck.getName().equals(CardList.LISTNAME_TABLE)) {
				DeckToTableAction dtha = (DeckToTableAction) ActionFactory.INSTANCE
						.getAction("deckToTable");
				if (sourceDeck.getCards().remove(card)) {
					dtha.moveCardToTable(game, deckOwner, cc, sourceDeck, card,
							true);
				}
			}

			else {
				DeckToDeckAction dtha = (DeckToDeckAction) ActionFactory.INSTANCE
						.getAction("deckToDeck");
				if (sourceDeck.getCards().remove(card)) {
					dtha.moveCardToDeck(game, deckOwner, cc, card, sourceDeck,
							(CardDeck) targetDeck, "bottom");
				}
			}
		}
	}

}
