package de.oglimmer.bcg.logic.swlcg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.CardList;
import de.oglimmer.bcg.logic.CardsSet;
import de.oglimmer.bcg.logic.Game;
import de.oglimmer.bcg.logic.GameException;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.logic.config.AbstractCardsFactory;
import de.oglimmer.bcg.logic.config.CardsFactory;

public class SwlcgCardsFactory extends AbstractCardsFactory implements
		CardsFactory {

	private static final String COMMITED_FORCE_LIGHT = "Star Wars LCG Light - 0000b.jpg";
	private static final String COMMITED_FORCE_DARK = "Star Wars LCG Dark - 0000b.jpg";
	private static final String DEFAULT_BACKGROUND_LIGHT = "Star Wars LCG Light - 0001.jpg";
	private static final String DEFAULT_BACKGROUND_DARK = "Star Wars LCG Dark - 0001.jpg";
	static final String EMPTY_CARDS_IMG = "Star Wars LCG - 0000.jpg";
	private static final String[] REF_FILES = { "/Core.xml",
			"/Desolation-Of-Hoth.xml" };

	private String forceImageUrl;

	public SwlcgCardsFactory(Game game, Player owner, InputStream deckStream) {
		super(game, owner, deckStream,
				(owner.getSide() == SwlcgSide.DARK ? DEFAULT_BACKGROUND_DARK
						: DEFAULT_BACKGROUND_LIGHT));
		this.forceImageUrl = (owner.getSide() == SwlcgSide.DARK ? COMMITED_FORCE_DARK
				: COMMITED_FORCE_LIGHT);
	}

	public CardsSet createCardsSet() {
		try {
			createUniqueTable();
			cards.put("hand", new CardList("hand"));
			createObjectiveDeck();
			createCommandDeck();
			handleAffiliationDeck();
			createDiscardDeck();
			return new CardsSet(game, owner, playerNo, cards);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new GameException("Failed to set JSON data", e);
		}
	}

	private synchronized void createUniqueTable() {
		CardList table = null;
		for (Player play : game.getPlayers().getPlayers()) {
			CardList tableList = play.getCardStacks().get("table");
			if (tableList != null) {
				table = tableList;
			}
		}
		if (table != null) {
			cards.put("table", table);
		} else {
			cards.put("table", new CardList("table"));
			Card balanceCard = new BalanceOfTheForceCard();
			balanceCard.setFaceup(true);
			balanceCard.setX(10);
			balanceCard.setY(298);
			cards.get("table").getCards().add(balanceCard);

			Card deathStarDialCard = new DeathStarDialCard();
			deathStarDialCard.setFaceup(true);
			deathStarDialCard.setX(5);
			deathStarDialCard.setY(364);
			cards.get("table").getCards().add(deathStarDialCard);
		}
	}

	private void createDiscardDeck() {
		int discardDeckPos = 100 + 100 * playerNo;
		CardList discardDeck = new SwlcgCardDeck(
				SwlcgCardDeck.DECKNAME_DISCARD, owner, 10, discardDeckPos, true);
		cards.put("discard", discardDeck);
	}

	private void handleAffiliationDeck() throws ParserConfigurationException,
			SAXException, IOException {
		CardList affiliationDeck = new CardList("affiliation");
		addCards(affiliationDeck, getImageUrls("Affiliation"),
				AffiliationCard.class);
		// cards.put("affiliation", affiliationDeck);
		// special case: affiliationDeck has only one card and that one goes
		// directly to the table
		Card affiCard = affiliationDeck.getCards().get(0);
		affiCard.setFaceup(true);
		affiCard.setX(affiCard.getX() + 200 * playerNo);
		cards.get("table").getCards().add(affiCard);

		for (int i = 0; i < 3; i++) {
			Card force = new CommitForceCard(owner, forceImageUrl);
			force.setFaceup(true);
			force.setY(200);
			force.setX(force.getX() + 200 * playerNo + i * 20);
			cards.get("table").getCards().add(force);
		}
	}

	private void createCommandDeck() throws ParserConfigurationException,
			SAXException, IOException {
		CardList commandDeck = new SwlcgCardDeck(
				SwlcgCardDeck.DECKNAME_COMMAND, owner, 10, 100, false);
		addCards(commandDeck, getImageUrls("Command Deck"), SwlcgCard.class);
		Collections.shuffle(commandDeck.getCards());
		cards.put("command", commandDeck);
	}

	private void createObjectiveDeck() throws ParserConfigurationException,
			SAXException, IOException {
		CardList objectiveDeck = new SwlcgCardDeck(
				SwlcgCardDeck.DECKNAME_OBJECTIVE, owner, 10, 10, false);
		addCards(objectiveDeck, getImageUrls("Objective Deck"), SwlcgCard.class);
		Collections.shuffle(objectiveDeck.getCards());
		cards.put("objective", objectiveDeck);
	}

	@Override
	protected String[] getRefFiles() {
		return REF_FILES;
	}

}
