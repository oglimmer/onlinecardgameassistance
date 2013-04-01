package de.oglimmer.bcg.logic.action;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.CardDeck;
import de.oglimmer.bcg.logic.CardList;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.GameException;
import de.oglimmer.bcg.logic.JSONPayload;
import de.oglimmer.bcg.logic.Player;

public class TakeCardIntoHandAction extends AbstractAction implements Action {

	private void sendDeckPlayer(Player player, ClientChannel cc,
			CardList cards, Card card, Game game) {

		List<Object[]> msg = new ArrayList<>();

		JSONObject cardJSON = card.toJSON(player, JSONPayload.BASE);
		// specify target area
		cardJSON.element("areaId", "hand");
		cardJSON.element("moveable", true);
		player.processMessage(cardJSON,
				"You took a card from " + cards.getName() + " into the hand");
		msg.add(new Object[] { "createCard", cardJSON });

		checkDeckMinus(player, cards, msg);

		send(player, cc, msg);
	}

	private void sendDeckOther(Game game, ClientChannel cc, CardList cards,
			Player player, Player otherPlayer) {
		List<Object[]> msg = new ArrayList<>();
		if (CardDeck.DECKNAME_DISCARD.equals(cards.getName())) {
			checkDeckMinus(otherPlayer, cards, msg);
		}
		addMessage(game, otherPlayer, cc, msg, "Opponent took a card from "
				+ cards.getName() + " into the hand");

		addInfoText(player, msg);

		send(otherPlayer, cc, msg);
	}

	private void sendCardPlayer(Player player, ClientChannel cc, Card card) {
		JSONObject cardJSON = card.toJSON(player, JSONPayload.BASE);
		cardJSON.element("areaId", "hand");
		player.processMessage(cardJSON, "You took a card back into hand");
		send(player, cc, "playCard", cardJSON);
	}

	private void sendCardOther(Player player, ClientChannel cc, Card card,
			Player otherPlayer) {
		JSONObject cardJSON = card.toJSON(player, JSONPayload.ID);
		otherPlayer.processMessage(cardJSON,
				"Opponent took card back into hand");
		addInfoText(player, cardJSON);
		send(otherPlayer, cc, "remove", cardJSON);
	}

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String id = parameters.getString("entityId");
		String sourceType = parameters.getString("param");

		CardList cards;
		Card card;
		if ("deck".equals(sourceType)) {
			// id is a deck. take top card.
			cards = player.getCardListById(id);
			card = cards.getCards().remove(0);
		} else if ("card".equals(sourceType)) {
			// id is a card. get deck through card
			cards = player.getCardListByCardId(id);
			card = player.getCard(id);
			cards.getCards().remove(card);
		} else {
			throw new GameException("Unexpected sourceType=" + sourceType);
		}
		player.getCardStacks().get("hand").getCards().add(card);
		card.setFaceup(true);
		card.setX(200);
		card.setY(20);

		Player otherPlayer = game.getPlayers().getOther(player);
		if ("deck".equals(sourceType)) {
			sendDeckPlayer(player, cc, cards, card, game);
			sendDeckOther(game, cc, cards, player, otherPlayer);
		} else if ("card".equals(sourceType)) {
			sendCardPlayer(player, cc, card);
			sendCardOther(player, cc, card, otherPlayer);
		}
	}

}
