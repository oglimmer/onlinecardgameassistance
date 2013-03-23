package de.oglimmer.bcg.logic.action;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.CardList;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.GameException;
import de.oglimmer.bcg.logic.JSONPayload;
import de.oglimmer.bcg.logic.Player;

public class TakeCardIntoHandAction extends AbstractAction implements Action {

	private void sendPlayer(Player player, ClientChannel cc, CardList cards,
			Card card, Game game) {

		List<Object[]> msg = new ArrayList<>();

		JSONObject cardJSON = card.toJSON(player, JSONPayload.BASE);
		// specify target area
		cardJSON.element("areaId", game.getBoard().getArea("hand", player)
				.getId());
		cardJSON.element("owner", true);
		cardJSON.element("infoText", "You took a card from " + cards.getName()
				+ " into the hand");
		msg.add(new Object[] { "createCard", cardJSON });

		checkDeckMinus(player, cards, msg);

		send(player, cc, msg);
	}

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String id = parameters.getString("id");
		String sourceType = parameters.getString("sourceType");

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

		if ("deck".equals(sourceType)) {
			sendPlayer(player, cc, cards, card, game);
			sendMessage(game, game.getPlayers().getOther(player), cc,
					"Opponent took a card from " + cards.getName()
							+ " into the hand");
		} else if ("card".equals(sourceType)) {
			JSONObject cardJSON = card.toJSON(player, JSONPayload.BASE);
			cardJSON.element("areaId", game.getBoard().getArea("hand", player)
					.getId());
			cardJSON.put("infoText", "You took a card back into hand");
			send(player, cc, "playCard", cardJSON);

			cardJSON = card.toJSON(player, JSONPayload.ID);
			cardJSON.element("infoText", "Opponent took card back into hand");
			send(game.getPlayers().getOther(player), cc, "remove", cardJSON);

		}
	}

}
