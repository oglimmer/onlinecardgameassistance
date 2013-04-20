package de.oglimmer.bcg.logic.action;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.JSONPayload;
import de.oglimmer.bcg.logic.Player;

public class FlipCardAction extends AbstractAction implements Action {

	private void send(Card card, Player player, ClientChannel cc, String text) {
		JSONObject cardJSON = card.toJSON(player, JSONPayload.BASE);
		player.processMessage(cardJSON, text);
		send(player, cc, "updateImage", cardJSON);
	}

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String cardId = parameters.getString("entityId");
		Card card = player.getCardStacks().getCard(cardId);

		boolean newStatus = !card.isFaceup();

		card.setFaceup(newStatus);

		send(card, player, cc, "You turned " + card.getName() + " face "
				+ (newStatus ? "up" : "down"));

		Player otherPlayer = game.getPlayers().getOther(player);
		send(card, otherPlayer, cc, "Opponent turned " + card.getName()
				+ " face " + (newStatus ? "up" : "down"));
	}
}
