package de.oglimmer.bcg.logic.action;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.CardDeck;
import de.oglimmer.bcg.logic.CardsSet;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.JSONPayload;
import de.oglimmer.bcg.logic.Player;

public class DiscardAction extends AbstractAction implements Action {

	private void send(Player player, ClientChannel cc, Card card,
			CardDeck discardDeck, String text) {
		List<Object[]> msg = new ArrayList<>();
		JSONObject cardJSON = card.toJSON(player, JSONPayload.BASE);
		// cardList from where the card will be removed
		cardJSON.element("discardId", discardDeck.getId());
		cardJSON.element("infoText", text);
		msg.add(new Object[] { "discard", cardJSON });

		checkDeckPlus(player, discardDeck, msg);

		send(player, cc, msg);
	}

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String cardId = parameters.getString("cardId");
		Card card = player.getCard(cardId);
		CardsSet cardStacks = player.getCardStacks();

		// remove card from its current cardList
		player.getCardListByCardId(cardId).getCards().remove(card);

		CardDeck discardDeck = (CardDeck) cardStacks.get("discard");
		discardDeck.getCards().add(0, card);
		card.setFaceup(true);
		card.setX(200);
		card.setY(20);

		send(player, cc, card, discardDeck, "Your discarded a card");
		send(game.getPlayers().getOther(player), cc, card, discardDeck,
				"Opponent discarded a card");

	}

}
