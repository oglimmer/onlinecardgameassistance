package de.oglimmer.bcg.logic.action;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.CardsSet;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.JSONPayload;
import de.oglimmer.bcg.logic.Player;

/**
 * Plays a card face up/down on the table.
 * 
 * @author oli
 * 
 */
public class HandToTableAction extends AbstractAction implements Action {

	private JSONObject send(Game game, Player player, Card card) {
		JSONObject cardJSON = card.toJSON(player, JSONPayload.BASE);
		cardJSON.element("areaId", "table");
		return cardJSON;
	}

	private void sendPlayer(Game game, Player player, ClientChannel cc,
			Card card, boolean faceUp) {
		JSONObject cardJSON = send(game, player, card);
		player.processMessage(cardJSON, "You played " + card.getName()
				+ " face " + (faceUp ? "up" : "down") + " from hand to table");
		send(player, cc, "playCard", cardJSON);
	}

	private void sendOtherPlayer(Game game, Player otherPlayer,
			ClientChannel cc, Card card, boolean faceUp, int playerHandCards) {
		JSONObject cardJSON = send(game, otherPlayer, card);
		cardJSON.element("moveable", false);
		otherPlayer.processMessage(cardJSON, "Opponent played "
				+ (faceUp ? card.getName() : "a card") + " face "
				+ (faceUp ? "up" : "down") + " from hand to table");
		addInfoText(playerHandCards, cardJSON);

		send(otherPlayer, cc, "createCard", cardJSON);
	}

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String cardId = parameters.getString("entityId");
		boolean faceUp = "up".equals(parameters.getString("param"));
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
