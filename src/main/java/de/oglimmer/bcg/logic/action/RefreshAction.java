package de.oglimmer.bcg.logic.action;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.CardList;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.JSONPayload;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.logic.swlcg.DeathStarDialCard;

/**
 * SWLCG action
 * 
 * @author oli
 * 
 */
public class RefreshAction extends AbstractAction {

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		List<Object[]> msgPly = new ArrayList<>();
		List<Object[]> msgOpp = new ArrayList<>();
		for (Card card : player.getCardStacks()
				.getByName(CardList.LISTNAME_TABLE).getCards()) {
			if (!(card instanceof DeathStarDialCard) && card.isOwner(player)) {
				if (card.getCounter(0) > 0) {
					card.modCounter(-1, 0);

					JSONObject cardJSON = card.toJSON(player,
							JSONPayload.COUNTER);
					player.processMessage(cardJSON, "Decreased Focus on "
							+ card.getName());
					msgPly.add(new Object[] { "modCounter", cardJSON });
					cardJSON = card.toJSON(player, JSONPayload.COUNTER);
					player.processMessage(cardJSON, "Decreased Focus on "
							+ card.getName());
					msgOpp.add(new Object[] { "modCounter", cardJSON });
				}
				if (card.getCounter(2) > 0) {
					card.modCounter(-card.getCounter(2), 2);

					JSONObject cardJSON = card.toJSON(player,
							JSONPayload.COUNTER);
					player.processMessage(cardJSON,
							"Shield to 0 on " + card.getName());
					msgPly.add(new Object[] { "modCounter", cardJSON });
					cardJSON = card.toJSON(player, JSONPayload.COUNTER);
					player.processMessage(cardJSON,
							"Shield to 0 on " + card.getName());
					msgOpp.add(new Object[] { "modCounter", cardJSON });
				}
			}
		}

		if (!msgPly.isEmpty()) {
			send(player, cc, msgPly);
		}

		if (!msgOpp.isEmpty()) {
			send(game.getPlayers().getOther(player), cc, msgOpp);
		}
	}

}
