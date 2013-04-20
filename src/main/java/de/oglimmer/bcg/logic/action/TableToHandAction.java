package de.oglimmer.bcg.logic.action;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.JSONPayload;
import de.oglimmer.bcg.logic.Player;

public class TableToHandAction extends AbstractAction implements Action {

	private void sendCardOwner(Player player, ClientChannel cc, Card card) {
		JSONObject cardJSON = card.toJSON(player, JSONPayload.BASE);
		cardJSON.element("areaId", "hand");
		player.processMessage(cardJSON, "You took " + card.getName()
				+ " back into hand");
		send(player, cc, "playCard", cardJSON);
	}

	private void sendCardOpponent(Player player, ClientChannel cc, Card card,
			Player otherPlayer, boolean oldFaceup) {
		JSONObject cardJSON = card.toJSON(player, JSONPayload.ID);
		otherPlayer.processMessage(cardJSON, "Opponent took "
				+ (oldFaceup ? card.getName() : "a card") + " back into hand");
		addInfoText(player, cardJSON);
		send(otherPlayer, cc, "remove", cardJSON);
	}

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String cardId = parameters.getString("entityId");

		Card card = player.getCardStacks().getCard(cardId);
		player.getCardStacks().getByName("table").getCards().remove(card);

		player.getCardStacks().getByName("hand").getCards().add(card);
		boolean oldFaceup = card.isFaceup();
		card.setFaceup(true);
		card.setX(200);
		card.setY(20);

		Player otherPlayer = game.getPlayers().getOther(player);

		sendCardOwner(player, cc, card);
		sendCardOpponent(player, cc, card, otherPlayer, oldFaceup);
	}

}
