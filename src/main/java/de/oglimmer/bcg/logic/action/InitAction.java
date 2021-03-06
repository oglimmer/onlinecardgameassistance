package de.oglimmer.bcg.logic.action;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.JSONPayload;
import de.oglimmer.bcg.logic.Player;

public class InitAction extends AbstractAction implements Action {

	private void createTableCards(Game game, Player player, List<Object[]> msg) {
		for (Card card : player.getCardStacks().getByName("table").getCards()) {
			JSONObject json = card.toJSON(player, JSONPayload.BASE,
					JSONPayload.COUNTER, JSONPayload.HIGHLIGHT,
					JSONPayload.GRADE);
			json.element("areaId", "table");
			json.element("moveable", card.isOwner(player));
			msg.add(new Object[] { "createCard", json });
		}
	}

	private void createHandCards(Game game, Player player, List<Object[]> msg) {
		for (Card card : player.getCardStacks().getByName("hand").getCards()) {
			JSONObject json = card.toJSON(player, JSONPayload.BASE);
			json.element("areaId", "hand");
			json.element("moveable", true);
			msg.add(new Object[] { "createCard", json });
		}
	}

	private void createMessages(final Player player, List<Object[]> msg) {
		for (String txt : player.getMessages()) {
			JSONObject cardJSON = new JSONObject();
			cardJSON.element("messageItem", txt);
			msg.add(new Object[] { "message", cardJSON });
		}
		addInfoText(player.getGame().getPlayers().getOther(player), msg);
	}

	@Override
	public void execute(Game game, final Player player, JSONObject parameters,
			ClientChannel cc) {

		JSONArray arr = game.getBoard().toJSON(player);
		List<Object[]> msg = new ArrayList<>();
		msg.add(new Object[] { "createDivs", arr });

		createTableCards(game, player, msg);
		createHandCards(game, player, msg);
		createMessages(player, msg);

		send(player, cc, msg);
	}

}
