package de.oglimmer.bcg.logic.swccg;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.logic.CardList;
import de.oglimmer.bcg.logic.CardsSet;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.logic.action.InfoBoxUpdater;

public class SwccgInfoBoxUpdater implements InfoBoxUpdater {

	@Override
	public void addInfoText(Player otherPlayer, JSONObject cardJSON) {

		CardsSet othcs = otherPlayer.getCardStacks();
		CardsSet pcs = otherPlayer.getGame().getPlayers().getOther(otherPlayer)
				.getCardStacks();

		addInfoText(othcs.getByName(CardList.LISTNAME_HAND).getCards().size(),
				pcs.getByName(SwccgCardDeck.DECKNAME_RESERVEDECK).getCards()
						.size(),
				othcs.getByName(SwccgCardDeck.DECKNAME_RESERVEDECK).getCards()
						.size(), pcs
						.getByName(SwccgCardDeck.DECKNAME_FORCEPILE).getCards()
						.size(),
				othcs.getByName(SwccgCardDeck.DECKNAME_FORCEPILE).getCards()
						.size(), pcs.getByName(SwccgCardDeck.DECKNAME_USEDPILE)
						.getCards().size(),
				othcs.getByName(SwccgCardDeck.DECKNAME_USEDPILE).getCards()
						.size(), pcs.getByName(SwccgCardDeck.DECKNAME_LOSTPILE)
						.getCards().size(),
				othcs.getByName(SwccgCardDeck.DECKNAME_LOSTPILE).getCards()
						.size(), cardJSON);
	}

	private void addInfoText(int playerHandCards, int yourReserve,
			int oppReserve, int yourForce, int oppForce, int yourUsed,
			int oppUsed, int yourLost, int oppLost, JSONObject cardJSON) {
		cardJSON.element("infoText", String.format("Opponent's hand: %s<br/>"
				+ "Your reserve: %d / Oppo reserve: %d<br/>"
				+ "Your force: %d / Oppo force: %d<br/>"
				+ "Your used: %d / Oppo used: %d<br/>"
				+ "Your lost: %d / Oppo lost: %d", playerHandCards,
				yourReserve, oppReserve, yourForce, oppForce, yourUsed,
				oppUsed, yourLost, oppLost));
	}
}
