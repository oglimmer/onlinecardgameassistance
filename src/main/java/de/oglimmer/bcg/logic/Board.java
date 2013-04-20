package de.oglimmer.bcg.logic;

import net.sf.json.JSONArray;
import de.oglimmer.bcg.util.Check;
import de.oglimmer.bcg.util.JSONArrayList;

/**
 * 
 * 
 * @author oli
 * 
 */
public class Board {

	private JSONArrayList<BoardArea> areas;

	private Game game;

	public Board(Game game, JSONArrayList<BoardArea> areas) {
		this.game = game;
		this.areas = areas;
	}

	public boolean isCardDeckVisibleForPlayer(Player player, CardDeck cardDeck) {
		boolean ret = false;
		for (BoardArea ba : areas) {
			if (ba.isCardDeckVisibleForPlayer(player, cardDeck)) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	/**
	 * Get a cardlist where a certain card is located
	 * 
	 * @param card
	 * @return
	 */
	public CardList getCardListByCard(Card card) {
		for (Player p : game.getPlayers().getPlayers()) {
			for (CardList cl : p.getCardStacks().getCardLists()) {
				if (cl.getCards().contains(card)) {
					return cl;
				}
			}
		}
		throw new GameException("No card in game with id = " + card.getId());
	}

	/**
	 * Get a CardList by id (for all players)
	 * 
	 * @param name
	 * @return
	 */
	public CardList getCardListById(String id) {
		for (Player p : game.getPlayers().getPlayers()) {
			for (CardList cl : p.getCardStacks().getCardLists()) {
				if (cl.getId().equals(id)) {
					return cl;
				}
			}
		}
		throw new GameException("No cardlist in game with id = " + id);
	}

	public JSONArray toJSON(final Player player) {
		return areas.toJsonArray(player, new Check<BoardArea>() {
			@Override
			public boolean isItemOkay(BoardArea ba) {
				return ba.getVisibleFor().contains(player);
			}
		});
	}

}
