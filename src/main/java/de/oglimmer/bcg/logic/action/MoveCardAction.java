package de.oglimmer.bcg.logic.action;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.CardList;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.Player;

public class MoveCardAction extends AbstractAction implements Action {

	private void send(ClientChannel cc, String cardId, Player player, int xPos,
			int yPos) {
		JSONObject cardJSON = new JSONObject();
		cardJSON.element("cardId", cardId);
		cardJSON.element("xPos", xPos);
		cardJSON.element("yPos", yPos);

		send(player, cc, "moveCard", cardJSON);
	}

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String cardId = parameters.getString("cardId");
		Card card = player.getCard(cardId);

		int xPos = parameters.getInt("xPos");
		int yPos = parameters.getInt("yPos");

		card.setX(xPos);
		card.setY(yPos);

		Player otherPlayer = game.getPlayers().getOther(player);
		CardList cl = player.getCardListByCardId(cardId);
		if (cl.getName().equals("table")) {
			send(cc, cardId, otherPlayer, xPos, yPos);
		}
	}

}
