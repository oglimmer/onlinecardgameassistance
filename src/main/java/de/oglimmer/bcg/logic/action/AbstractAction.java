package de.oglimmer.bcg.logic.action;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.CardDeck;
import de.oglimmer.bcg.logic.CardList;
import de.oglimmer.bcg.logic.Player;

public abstract class AbstractAction implements Action {

	protected void checkDeckMinus(Player player, CardList cardList,
			List<Object[]> msg) {
		CardDeck cd = (CardDeck) cardList;
		if ((cardList.getCards().isEmpty() && !cd.isOpenCardList())
				|| cd.isOpenCardList()) {
			JSONObject deckJSON = ((CardDeck) cardList).toJSON(player);
			msg.add(new Object[] { "updateImage", deckJSON });
		}
	}

	protected void checkDeckPlus(Player player, CardList cardList,
			List<Object[]> msg) {
		CardDeck cd = (CardDeck) cardList;
		if ((cardList.getCards().size() == 1 && !cd.isOpenCardList())
				|| cd.isOpenCardList()) {
			JSONObject deckJSON = cd.toJSON(player);
			msg.add(new Object[] { "updateImage", deckJSON });
		}
	}

	// --------------------------------------------------

	protected void addInfoText(final Player otherPlayer, List<Object[]> msg) {
		JSONObject cardJSON = new JSONObject();
		addInfoText(otherPlayer, cardJSON);
		msg.add(new Object[] { "info", cardJSON });
	}

	protected void addInfoText(Player otherPlayer, JSONObject cardJSON) {
		otherPlayer.getGame().getGameConfig().getInfoBoxUpdater()
				.addInfoText(otherPlayer, cardJSON);
	}

	// --------------------------------------------------

	protected void sendMessage(Player player, ClientChannel cc, String text) {

		List<Object[]> msg = new ArrayList<>();
		addMessage(player, cc, msg, text);
		send(player, cc, msg);
	}

	protected void addMessage(Player player, ClientChannel cc,
			List<Object[]> msg, String text) {

		JSONObject cardJSON = new JSONObject();
		player.processMessage(cardJSON, text);
		msg.add(new Object[] { "message", cardJSON });
	}

	// --------------------------------------------------

	protected void send(Player player, ClientChannel cc, String handlerName,
			Object data) {

		JSONObject message = new JSONObject();
		message.element(handlerName, data);

		JSONArray messages = new JSONArray();
		messages.add(message);

		cc.send(player, messages);
	}

	protected void send(Player player, ClientChannel cc,
			List<Object[]> subMessages) {

		JSONArray messages = new JSONArray();
		for (Object[] subMsg : subMessages) {
			JSONObject message = new JSONObject();
			String handlerName = (String) subMsg[0];
			Object data = (Object) subMsg[1];
			message.element(handlerName, data);
			messages.add(message);
		}
		cc.send(player, messages);
	}

}
