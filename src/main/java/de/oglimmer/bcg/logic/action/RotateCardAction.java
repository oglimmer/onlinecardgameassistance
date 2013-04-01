package de.oglimmer.bcg.logic.action;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.Player;

public class RotateCardAction extends AbstractAction implements Action {

	private void send(String cardId, Player player, ClientChannel cc,
			String text) {
		JSONObject cardJSON = new JSONObject();
		cardJSON.element("cardId", cardId);
		player.processMessage(cardJSON, text);

		send(player, cc, "rotateCard", cardJSON);
	}

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String cardId = parameters.getString("entityId");
		send(cardId, player, cc, "You rotated a card");

		Player otherPlayer = game.getPlayers().getOther(player);
		send(cardId, otherPlayer, cc, "Opponent rotated a card");
	}
}
