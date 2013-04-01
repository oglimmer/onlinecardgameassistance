package de.oglimmer.bcg.logic.action;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.CardList;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.logic.UIElement;

public class MoveCardAction extends AbstractAction implements Action {

	private void send(ClientChannel cc, String cardId, Player player, int xPos,
			int yPos) {
		JSONObject cardJSON = new JSONObject();
		cardJSON.element("id", cardId);
		cardJSON.element("xPos", xPos);
		cardJSON.element("yPos", yPos);

		send(player, cc, "moveCard", cardJSON);
	}

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String id = parameters.getString("id");
		UIElement uiEle = player.getUIElement(id);

		int xPos = parameters.getInt("xPos");
		int yPos = parameters.getInt("yPos");

		uiEle.setX(xPos);
		uiEle.setY(yPos);

		Player otherPlayer = game.getPlayers().getOther(player);
		if (uiEle instanceof CardList
				|| player.getCardListByCardId(id).getName().equals("table")) {
			send(cc, id, otherPlayer, xPos, yPos);
		}
	}

}
