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
 * Take a card from a deck (maybe face down as command/objective, or face up as
 * discard) into hand.
 * 
 * 
 * @author oli
 * 
 */
public class DeckToHandAction extends AbstractAction implements Action {

	private void sendDeckOwner(Player player, ClientChannel cc, CardDeck cards,
			Card card, Game game) {

		List<Object[]> msg = new ArrayList<>();

		JSONObject cardJSON = card.toJSON(player, JSONPayload.BASE);
		// specify target area
		cardJSON.element("areaId", "hand");
		cardJSON.element("moveable", true);
		player.processMessage(cardJSON, "You took " + card.getName() + " from "
				+ cards.getDescription() + " into the hand");
		msg.add(new Object[] { "createCard", cardJSON });

		checkDeckMinus(player, cards, msg);

		send(player, cc, msg);
	}

	private void sendDeckOpponent(Game game, ClientChannel cc, CardDeck cards,
			Player player, Player otherPlayer, Card card, boolean oldFaceup) {
		List<Object[]> msg = new ArrayList<>();
		String txt;
		if (cards.isOpenCardList()) {
			checkDeckMinus(otherPlayer, cards, msg);
			txt = "Opponent took " + (oldFaceup ? card.getName() : "a card")
					+ " from " + cards.getDescription() + " into the hand";
		} else {
			txt = "Opponent took a card from " + cards.getDescription()
					+ " into the hand";
		}

		addMessage(otherPlayer, cc, msg, txt);

		addInfoText(player, msg);

		send(otherPlayer, cc, msg);
	}

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String deckId = parameters.getString("entityId");

		CardDeck cards = (CardDeck) player.getCardListById(deckId);
		Card card = cards.getCards().remove(0);
		moveCardToHand(game, player, cc, cards, card);
	}

	public void moveCardToHand(Game game, Player player, ClientChannel cc,
			CardDeck cards, Card card) {
		player.getCardStacks().get(CardList.LISTNAME_HAND).getCards().add(card);

		boolean oldFaceup = card.isFaceup();
		card.setFaceup(true);
		card.setX(200);
		card.setY(20);

		Player otherPlayer = game.getPlayers().getOther(player);

		sendDeckOwner(player, cc, cards, card, game);
		sendDeckOpponent(game, cc, cards, player, otherPlayer, card, oldFaceup);
	}

}
