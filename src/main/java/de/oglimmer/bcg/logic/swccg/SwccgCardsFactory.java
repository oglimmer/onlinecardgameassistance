package de.oglimmer.bcg.logic.swccg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.oglimmer.bcg.logic.CardList;
import de.oglimmer.bcg.logic.CardsSet;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.GameException;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.logic.config.AbstractCardsFactory;
import de.oglimmer.bcg.logic.config.CardsFactory;

class SwccgCardsFactory extends AbstractCardsFactory implements CardsFactory {

	private static final String DEFAULT_BACKGROUND_LIGHT = "misc/light-back.jpg";
	private static final String DEFAULT_BACKGROUND_DARK = "misc/dark-back.jpg";
	static final String EMPTY_CARDS_IMG = "misc/Star Wars LCG - 0000.jpg";
	private static final String[] REF_FILES = { "/Premiere.xml" };

	public SwccgCardsFactory(Game game, Player owner, InputStream deckStream) {
		super(game, owner, deckStream,
				(owner.getSide() == SwccgSide.DARK ? DEFAULT_BACKGROUND_DARK
						: DEFAULT_BACKGROUND_LIGHT));
	}

	public CardsSet createCardsSet() {
		try {
			createUniqueTable();
			cards.add(new CardList(SwccgCardDeck.LISTNAME_HAND));
			createReserveDeck();
			createLostPile();
			createForcePile();
			createUsedPile();
			return new CardsSet(game, owner, playerNo, cards);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new GameException("Failed to set JSON data", e);
		}
	}

	private synchronized CardList createUniqueTable() {
		CardList table = null;
		for (Player play : game.getPlayers().getPlayers()) {
			CardList tableList = play.getCardStacks().get(
					CardList.LISTNAME_TABLE);
			if (tableList != null) {
				table = tableList;
			}
		}
		if (table != null) {
			cards.add(table);
		} else {
			table = new CardList(CardList.LISTNAME_TABLE);
			cards.add(table);
		}
		return table;
	}

	private void createReserveDeck() throws ParserConfigurationException,
			SAXException, IOException {
		CardList commandDeck = new SwccgCardDeck(
				SwccgCardDeck.DECKNAME_RESERVEDECK,
				SwccgCardDeck.DECKDESC_RESERVEDECK, owner, 72,
				95 + playerNo * 300, false);
		addCards(commandDeck, getImageUrls("Deck"), SwccgCard.class);
		Collections.shuffle(commandDeck.getCards());
		cards.add(commandDeck);
	}

	private void createLostPile() throws ParserConfigurationException,
			SAXException, IOException {
		CardList commandDeck = new SwccgCardDeck(
				SwccgCardDeck.DECKNAME_LOSTPILE,
				SwccgCardDeck.DECKDESC_LOSTPILE, owner, 172,
				95 + playerNo * 300, true);
		cards.add(commandDeck);
	}

	private void createForcePile() throws ParserConfigurationException,
			SAXException, IOException {
		CardList commandDeck = new SwccgCardDeck(
				SwccgCardDeck.DECKNAME_FORCEPILE,
				SwccgCardDeck.DECKDESC_FORCEPILE, owner, 7,
				95 + playerNo * 300, false);
		cards.add(commandDeck);
	}

	private void createUsedPile() throws ParserConfigurationException,
			SAXException, IOException {
		CardList commandDeck = new SwccgCardDeck(
				SwccgCardDeck.DECKNAME_USEDPILE,
				SwccgCardDeck.DECKDESC_USEDPILE, owner, 71, 5 + playerNo * 300,
				false);
		cards.add(commandDeck);
	}

	@Override
	protected String[] getRefFiles() {
		return REF_FILES;
	}

}
