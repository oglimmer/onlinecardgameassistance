package de.oglimmer.bcg.logic.action;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.CardsSet;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.JSONPayload;
import de.oglimmer.bcg.logic.Player;

public class PlayCardOnTableAction extends AbstractAction implements Action {

	private JSONObject send(Game game, Player player, Card card) {
		JSONObject cardJSON = card.toJSON(player, JSONPayload.BASE);
		cardJSON.element("areaId", game.getBoard().getArea("table").getId());
		return cardJSON;
	}

	private void sendPlayer(Game game, Player player, ClientChannel cc,
			Card card, boolean faceUp) {
		JSONObject cardJSON = send(game, player, card);
		cardJSON.put("infoText", "You played a card face "
				+ (faceUp ? "up" : "down") + " from hand to table");
		send(player, cc, "playCard", cardJSON);
	}

	private void sendOtherPlayer(Game game, Player otherPlayer,
			ClientChannel cc, Card card, boolean faceUp, int playerHandCards) {
		JSONObject cardJSON = send(game, otherPlayer, card);
		cardJSON.element("owner", false);
		cardJSON.element("infoText", "Opponent played a card face "
				+ (faceUp ? "up" : "down") + " from hand(" + playerHandCards
				+ ") to table");

		send(otherPlayer, cc, "createCard", cardJSON);
	}

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String cardId = parameters.getString("cardId");
		boolean faceUp = "up".equals(parameters.getString("faceup"));
		Card card = player.getCard(cardId);
		CardsSet cardStacks = player.getCardStacks();
		cardStacks.get("hand").getCards().remove(card);
		cardStacks.get("table").getCards().add(card);
		card.setFaceup(faceUp);

		sendPlayer(game, player, cc, card, faceUp);

		sendOtherPlayer(game, game.getPlayers().getOther(player), cc, card,
				faceUp, cardStacks.get("hand").getCards().size());
	}

}
