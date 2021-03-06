package de.oglimmer.bcg.logic.action;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.JSONPayload;
import de.oglimmer.bcg.logic.Player;

public class RotateCardAction extends AbstractAction implements Action {

	private void send(Card card, Player player, ClientChannel cc, int grade) {
		JSONObject cardJSON = card.toJSON(player, JSONPayload.ID,
				JSONPayload.GRADE);
		send(player, cc, "rotateCard", cardJSON);
	}

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String cardId = parameters.getString("entityId");
		int grade = parameters.getInt("param");

		Card card = player.getCardStacks().getCard(cardId);
		card.setGrade(grade);

		send(card, player, cc, grade);

		Player otherPlayer = game.getPlayers().getOther(player);
		send(card, otherPlayer, cc, grade);
	}
}
