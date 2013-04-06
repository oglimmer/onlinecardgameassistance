package de.oglimmer.bcg.logic.action;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.JSONPayload;
import de.oglimmer.bcg.logic.Player;

public class ToggleHighlightAction extends AbstractAction implements Action {

	private void send(Card card, Player player, ClientChannel cc) {
		JSONObject cardJSON = card.toJSON(player, JSONPayload.HIGHLIGHT);
		send(player, cc, "toggleHighlight", cardJSON);
	}

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String cardId = parameters.getString("entityId");
		Card card = player.getCard(cardId);

		boolean newStatus = !card.isHighlight();

		card.setHighlight(newStatus);

		send(card, player, cc);

		Player otherPlayer = game.getPlayers().getOther(player);
		send(card, otherPlayer, cc);
	}
}
