package de.oglimmer.bcg.logic.action;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.CardDeck;
import de.oglimmer.bcg.logic.CardList;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.JSONPayload;
import de.oglimmer.bcg.logic.Player;

/**
 * Takes the top card of a cardlist (com,obj,discard) and plays it (face
 * up/down) on the table.
 * 
 * @author oli
 * 
 */
public class DeckToTableAction extends AbstractAction implements Action {

	private void send(Card card, Game game, Player player, ClientChannel cc,
			String text, boolean owner, CardDeck cards) {

		List<Object[]> msg = new ArrayList<>();

		JSONObject cardJSON = card.toJSON(player, JSONPayload.BASE);
		cardJSON.element("areaId", "table");
		cardJSON.element("moveable", owner);
		player.processMessage(cardJSON, text);
		msg.add(new Object[] { "createCard", cardJSON });

		if (owner || cards.isOpenCardList()) {
			checkDeckMinus(player, cards, msg);
		}

		send(player, cc, msg);
	}

	private void sendOpponent(Game game, Player player, ClientChannel cc,
			boolean faceUp, CardDeck cards, Card card) {
		String txt = "Opponent played " + (faceUp ? card.getName() : "a card")
				+ " face " + (faceUp ? "up" : "down") + " from "
				+ cards.getDescription() + " directly to the table";
		send(card, game, game.getPlayers().getOther(player), cc, txt, false,
				cards);
	}

	private void sendOwner(Game game, Player player, ClientChannel cc,
			boolean faceUp, CardDeck cards, Card card) {
		String txt = "You played " + (faceUp ? card.getName() : "a card")
				+ " face " + (faceUp ? "up" : "down") + " from "
				+ cards.getDescription() + " directly to the table";
		send(card, game, player, cc, txt, true, cards);
	}

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String deckId = parameters.getString("entityId");
		boolean faceUp = "up".equals(parameters.getString("param"));
		CardDeck cards = (CardDeck) player.getCardListById(deckId);

		Card card = cards.getCards().remove(0);
		moveCardToTable(game, player, cc, cards, card, faceUp);
	}

	public void moveCardToTable(Game game, Player player, ClientChannel cc,
			CardDeck cards, Card card, boolean faceUp) {
		player.getCardStacks().get(CardList.LISTNAME_TABLE).getCards()
				.add(card);
		card.setFaceup(faceUp);

		sendOwner(game, player, cc, faceUp, cards, card);

		sendOpponent(game, player, cc, faceUp, cards, card);

	}
}
