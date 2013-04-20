package de.oglimmer.bcg.logic.action;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.CardDeck;
import de.oglimmer.bcg.logic.CardList;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.JSONPayload;
import de.oglimmer.bcg.logic.Player;

/**
 * Returns a card to a deck (com,obj,discard)
 * 
 * @author oli
 * 
 */
public class ReturnToDeckAction extends AbstractAction implements Action {

	private void send(Player targetPlayer, ClientChannel cc, Card card,
			CardDeck targetDeck, String text) {
		List<Object[]> msg = new ArrayList<>();

		// main msg
		JSONObject cardJSON = card.toJSON(targetPlayer, JSONPayload.ID);
		targetPlayer.processMessage(cardJSON, text);
		msg.add(new Object[] { "remove", cardJSON });

		if (targetDeck.isVisibleTo(targetPlayer)) {
			// the target deck needs to be updated if it is my own deck or an
			// open deck
			checkDeckPlus(targetPlayer, targetDeck, msg);
		}

		// the opponent needs to be updated for the owners hand cards
		// but both players might need an update on the info text
		Player otherPlayer = targetPlayer.getGame().getPlayers()
				.getOther(targetPlayer);
		addInfoText(otherPlayer, cardJSON);

		send(targetPlayer, cc, msg);
	}

	private void sendOpponent(Game game, Player player, ClientChannel cc,
			String location, Card card, CardList oldCardList,
			CardDeck targetDeck) {
		Player opponent = game.getPlayers().getOther(player);

		if (CardList.LISTNAME_HAND.equals(oldCardList.getName())
				&& !targetDeck.isVisibleTo(player)) {
			// if the card was in the hand and it doesn't go to an open deck =>
			// just send a message to the opponent
			String txt = "Opponent put a card to the " + location + " of the "
					+ targetDeck.getDescription();
			List<Object[]> msg = new ArrayList<>();
			addMessage(opponent, cc, msg, txt);
			addInfoText(player, msg);

			send(opponent, cc, msg);
		} else {
			// card was open or goes to an open deck => send full card update
			String txt = "Opponent put "
					+ (card.isFaceup() ? card.getName() : "a card")
					+ " to the " + location + " of the "
					+ targetDeck.getDescription();

			send(opponent, cc, card, targetDeck, txt);
		}
	}

	private void sendOwner(Player owner, ClientChannel cc, String location,
			Card card, CardDeck targetDeck) {
		send(owner,
				cc,
				card,
				targetDeck,
				"You put " + (card.isFaceup() ? card.getName() : "a card")
						+ " to the " + location + " of the "
						+ targetDeck.getDescription());
	}

	private CardDeck returnToTargetDeck(String deckName, String location,
			Card card, CardList currentCardList, Player player) {

		currentCardList.getCards().remove(card);

		CardDeck targetDeck = (CardDeck) ("origin".equals(deckName) ? card
				.getOrigin() : player.getCardStacks().getByName(deckName));
		assert targetDeck != null : deckName + " not found";

		if ("top".equals(location)) {
			targetDeck.getCards().add(0, card);
		} else {
			targetDeck.getCards().add(card);
		}
		card.setFaceup(targetDeck.isOpenCardList());
		card.setX(200);
		card.setY(20);
		return targetDeck;
	}

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String cardId = parameters.getString("entityId");
		String[] params = parameters.getString("param").split("_");
		String deckName = params[0];
		String location = params[1];

		Card card = player.getCardStacks().getCard(cardId);
		CardList currentCardList = player.getCardStacks().getByCardId(cardId);

		CardDeck targetDeck = returnToTargetDeck(deckName, location, card,
				currentCardList, player);

		sendOwner(player, cc, location, card, targetDeck);
		sendOpponent(game, player, cc, location, card, currentCardList,
				targetDeck);
	}
}
