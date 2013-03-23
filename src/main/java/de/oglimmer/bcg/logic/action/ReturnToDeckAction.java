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

public class ReturnToDeckAction extends AbstractAction implements Action {

	private void send(Player player, ClientChannel cc, Card card,
			CardDeck discardDeck, String text, boolean owner) {
		List<Object[]> msg = new ArrayList<>();

		JSONObject cardJSON = card.toJSON(player, JSONPayload.ID);
		cardJSON.element("infoText", text);
		msg.add(new Object[] { "remove", cardJSON });

		if (owner) {
			checkDeckPlus(player, discardDeck, msg);
		}

		send(player, cc, msg);
	}

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String cardId = parameters.getString("cardId");
		String location = parameters.getString("location");
		Card card = player.getCard(cardId);

		// remove card from its current cardList
		CardList oldCardList = player.getCardListByCardId(cardId);
		oldCardList.getCards().remove(card);

		CardDeck originDeck = (CardDeck) card.getOrigin();
		if ("top".equals(location)) {
			originDeck.getCards().add(0, card);
		} else {
			originDeck.getCards().add(card);
		}
		card.setFaceup(false);
		card.setX(200);
		card.setY(20);

		send(player, cc, card, originDeck, "Your returned a card to the "
				+ location + " of the " + originDeck.getName(), true);

		Player otherPlayer = game.getPlayers().getOther(player);
		String txt = "Opponent returned a card to the " + location + " of the "
				+ originDeck.getName();
		if (oldCardList.getName().equals("hand")) {
			sendMessage(game, otherPlayer, cc, txt);
		} else {
			send(otherPlayer, cc, card, originDeck, txt, false);
		}

	}
}
