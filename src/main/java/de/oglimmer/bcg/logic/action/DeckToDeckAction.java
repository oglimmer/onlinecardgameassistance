package de.oglimmer.bcg.logic.action;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.com.ClientChannel;
import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.CardDeck;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.Player;

public class DeckToDeckAction extends AbstractAction {

	private void send(Player targetPlayer, ClientChannel cc,
			CardDeck sourceDeck, CardDeck targetDeck, String text) {
		List<Object[]> msg = new ArrayList<>();

		addMessage(targetPlayer, cc, msg, text);
		addInfoText(targetPlayer.getGame().getPlayers().getOther(targetPlayer),
				msg);
		if (sourceDeck.isVisibleTo(targetPlayer)) {
			// the target deck needs be be checked for an update if it is
			// visible to the target player
			checkDeckMinus(targetPlayer, sourceDeck, msg);
		}
		if (targetDeck.isVisibleTo(targetPlayer)) {
			// the target deck needs be be checked for an update if it is
			// visible to the target player
			checkDeckPlus(targetPlayer, targetDeck, msg);
		}

		send(targetPlayer, cc, msg);
	}

	private void sendOpponent(Game game, Player player, ClientChannel cc,
			String location, CardDeck sourceDeck, CardDeck targetDeck) {
		Player opponent = game.getPlayers().getOther(player);

		// card was open or goes to an open deck => send full card update
		String txt = "Opponent put a card to the " + location + " of the "
				+ targetDeck.getDescription();

		send(opponent, cc, sourceDeck, targetDeck, txt);

	}

	private void sendOwner(Player owner, ClientChannel cc, String location,
			CardDeck sourceDeck, CardDeck targetDeck) {
		send(owner, cc, sourceDeck, targetDeck, "You put a card to the "
				+ location + " of the " + targetDeck.getDescription());
	}

	private void returnToTargetDeck(Card card, CardDeck targetDeck,
			String location, Player player) {

		if ("top".equals(location)) {
			targetDeck.getCards().add(0, card);
		} else if ("bottom".equals(location)) {
			targetDeck.getCards().add(card);
		}
		card.setFaceup(targetDeck.isOpenCardList());
		card.setX(200);
		card.setY(20);
	}

	@Override
	public void execute(Game game, Player player, JSONObject parameters,
			ClientChannel cc) {

		String deckId = parameters.getString("entityId");
		String[] params = parameters.getString("param").split("_");
		String targetDeckName = params[0];
		String location = params[1];

		CardDeck sourceDeck = (CardDeck) player.getCardStacks().getById(deckId);
		CardDeck targetDeck = (CardDeck) player.getCardStacks().getByName(
				targetDeckName);

		moveCardToDeck(game, player, cc, sourceDeck, targetDeck, location);
	}

	public void moveCardToDeck(Game game, Player player, ClientChannel cc,
			CardDeck sourceDeck, CardDeck targetDeck, String location) {
		if ("all".equals(location)) {
			while (!sourceDeck.getCards().isEmpty()) {
				Card card = sourceDeck.getCards().remove(0);
				returnToTargetDeck(card, targetDeck, "bottom", player);
			}
		} else {
			Card card = sourceDeck.getCards().remove(0);
			returnToTargetDeck(card, targetDeck, location, player);
		}

		sendOwner(player, cc, location, sourceDeck, targetDeck);
		sendOpponent(game, player, cc, location, sourceDeck, targetDeck);
	}

	public void moveCardToDeck(Game game, Player player, ClientChannel cc,
			Card card, CardDeck sourceDeck, CardDeck targetDeck, String location) {

		returnToTargetDeck(card, targetDeck, location, player);

		sendOwner(player, cc, location, sourceDeck, targetDeck);
		sendOpponent(game, player, cc, location, sourceDeck, targetDeck);
	}

}