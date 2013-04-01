package de.oglimmer.bcg.logic.action;

import java.util.Collections;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.CardList;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.Player;

public class ShuffleAction extends AbstractAction implements Action {

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String deckId = parameters.getString("entityId");
		CardList cards = player.getCardListById(deckId);

		Collections.shuffle(cards.getCards());

		String txt = " shuffled " + cards.getName();

		sendMessage(game, player, cc, "You" + txt);
		sendMessage(game, game.getPlayers().getOther(player), cc, "Opponent"
				+ txt);
	}

}
