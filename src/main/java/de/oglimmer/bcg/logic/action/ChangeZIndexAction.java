package de.oglimmer.bcg.logic.action;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.CardDeck;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.JSONPayload;
import de.oglimmer.bcg.logic.Player;

public class ChangeZIndexAction extends AbstractAction implements Action {

	private void send(Card card, Player player, ClientChannel cc) {
		JSONObject cardJSON = card.toJSON(player, JSONPayload.BASE);
		send(player, cc, "changeZIndex", cardJSON);
	}

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String cardId = parameters.getString("entityId");
		String mode = parameters.getString("param");
		Card card = player.getCardStacks().getCard(cardId);

		int newZIndex = "down".equals(mode) ? getSmallest(card)
				: getHightest(card);

		card.setZIndex(newZIndex);

		send(card, player, cc);

		Player otherPlayer = game.getPlayers().getOther(player);
		send(card, otherPlayer, cc);
	}

	private int getHightest(Card card) {
		CardDeck cd = (CardDeck) card.getOrigin();
		int[] arr = cd.getZIndexBorders();
		arr[1]++;
		int ret = arr[1];
		return ret;
	}

	private int getSmallest(Card card) {
		CardDeck cd = (CardDeck) card.getOrigin();
		int[] arr = cd.getZIndexBorders();
		arr[0]--;
		int ret = arr[0];
		return ret;
	}
}
