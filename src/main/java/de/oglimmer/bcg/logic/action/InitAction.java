package de.oglimmer.bcg.logic.action;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.BoardArea;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.JSONPayload;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.util.Check;
import de.oglimmer.bcg.util.JSONArrayList;

public class InitAction extends AbstractAction implements Action {

	private void createTableCards(Game game, Player player, List<Object[]> msg) {
		for (Card card : player.getCardStacks().get("table").getCards()) {
			JSONObject json = card.toJSON(player, JSONPayload.BASE,
					JSONPayload.COUNTER);
			json.element("areaId", "table");
			json.element("owner", card.isOwner(player));
			msg.add(new Object[] { "createCard", json });
		}
	}

	private void createHandCards(Game game, Player player, List<Object[]> msg) {
		for (Card card : player.getCardStacks().get("hand").getCards()) {
			JSONObject json = card.toJSON(player, JSONPayload.BASE);
			json.element("areaId", "hand");
			json.element("owner", true);
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

		JSONArrayList<BoardArea> areas = game.getBoard().getAreas();
		JSONArray arr = areas.toJsonArray(player, new Check<BoardArea>() {
			@Override
			public boolean isItemOkay(BoardArea ba) {
				return ba.getVisibleFor().contains(player);
			}
		});
		List<Object[]> msg = new ArrayList<>();
		msg.add(new Object[] { "createDivs", arr });

		createTableCards(game, player, msg);
		createHandCards(game, player, msg);
		createMessages(player, msg);

		send(player, cc, msg);
	}

}
