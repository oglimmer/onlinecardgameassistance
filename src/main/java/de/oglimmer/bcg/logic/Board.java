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

	public String getCardList(Card card) {
		for (Player p : game.getPlayers().getPlayers()) {
			for (CardList cl : p.getCardStacks().getCardLists()) {
				if (cl.getCards().contains(card)) {
					return cl.getName();
				}
			}
		}
		throw new GameException("No card in game with id = " + card.getId());
	}

	public CardList getCardList(String id) {
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
