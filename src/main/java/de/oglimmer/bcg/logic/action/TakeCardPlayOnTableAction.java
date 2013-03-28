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

public class TakeCardPlayOnTableAction extends AbstractAction implements Action {

	private void send(Card card, Game game, Player player, ClientChannel cc,
			String text, boolean owner, CardList cards) {

		List<Object[]> msg = new ArrayList<>();

		JSONObject cardJSON = card.toJSON(player, JSONPayload.BASE);
		cardJSON.element("areaId", "table");
		cardJSON.element("owner", owner);
		player.processMessage(cardJSON, text);
		msg.add(new Object[] { "createCard", cardJSON });

		if (owner || CardDeck.DECKNAME_DISCARD.equals(cards.getName())) {
			checkDeckMinus(player, cards, msg);
		}

		send(player, cc, msg);
	}

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String deckId = parameters.getString("deckId");
		boolean faceUp = "up".equals(parameters.getString("faceup"));
		CardList cards = player.getCardListById(deckId);

		Card card = cards.getCards().remove(0);
		player.getCardStacks().get("table").getCards().add(card);
		card.setFaceup(faceUp);

		String txt = "You played a card face " + (faceUp ? "up" : "down")
				+ " from " + cards.getName() + " directly to the table";
		send(card, game, player, cc, txt, true, cards);

		txt = "Opponent played a card face " + (faceUp ? "up" : "down")
				+ " from " + cards.getName() + " directly to the table";
		send(card, game, game.getPlayers().getOther(player), cc, txt, false,
				cards);
	}

}
