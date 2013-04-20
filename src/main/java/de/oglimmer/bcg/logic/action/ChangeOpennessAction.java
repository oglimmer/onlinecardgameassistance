package de.oglimmer.bcg.logic.action;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.CardDeck;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.Player;

public class ChangeOpennessAction extends AbstractAction {

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String deckId = parameters.getString("entityId");
		CardDeck cards = (CardDeck) player.getCardStacks().getById(deckId);

		cards.setOpenCardList(!cards.isOpenCardList());

		JSONObject cardJSON = cards.toJSON(player);
		player.processMessage(cardJSON, "You made " + cards.getDescription()
				+ " face " + (cards.isOpenCardList() ? "up" : "down"));
		send(player, cc, "updateImage", cardJSON);

		Player oppPlayer = game.getPlayers().getOther(player);
		cardJSON = cards.toJSON(oppPlayer);
		oppPlayer.processMessage(
				cardJSON,
				"Opponent made " + cards.getDescription() + " face "
						+ (cards.isOpenCardList() ? "up" : "down"));
		send(oppPlayer, cc, "updateImage", cardJSON);
	}
}
