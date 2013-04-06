package de.oglimmer.bcg.logic.action;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.JSONPayload;
import de.oglimmer.bcg.logic.Player;

public class ModCounterAction extends AbstractAction {

	private static final String[] DESC = { "Focus", "Damage", "Shield" };

	private void sendPlayer(Player player, ClientChannel cc, Card card,
			boolean add, int pos) {
		JSONObject cardJSON = card.toJSON(player, JSONPayload.COUNTER);
		player.processMessage(cardJSON, (add ? "Increased" : "Decreased") + " "
				+ DESC[pos] + " on " + card.getName());
		send(player, cc, "modCounter", cardJSON);
	}

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String cardId = parameters.getString("entityId");
		String mode[] = parameters.getString("param").split("-");
		boolean add = "add".equals(mode[0]);
		int pos = Integer.parseInt(mode[1]);
		Card card = player.getCard(cardId);

		if (card.getCounter(pos) != 0 || add) {
			card.modCounter(add ? 1 : -1, pos);

			sendPlayer(player, cc, card, add, pos);

			sendPlayer(game.getPlayers().getOther(player), cc, card, add, pos);
		}
	}

}
