package de.oglimmer.bcg.logic.swlcg;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.logic.action.InfoBoxUpdater;

class SwlcgInfoBoxUpdater implements InfoBoxUpdater {

	@Override
	public void addInfoText(Player otherPlayer, JSONObject cardJSON) {
		addInfoText(otherPlayer.getCardStacks().get("hand").getCards().size(),
				otherPlayer.getGame().getPlayers().getOther(otherPlayer)
						.getCardStacks().get("lostobjectives").getCards()
						.size(),
				otherPlayer.getCardStacks().get("lostobjectives").getCards()
						.size(), cardJSON);
	}

	private void addInfoText(int playerHandCards, int yourLostObj,
			int oppLostObj, JSONObject cardJSON) {
		cardJSON.element(
				"infoText",
				String.format(
						"Opponent's hand: %s<br/>Your lost obj: %s / Oppo lost obj: %s",
						playerHandCards, yourLostObj, oppLostObj));
	}

}
