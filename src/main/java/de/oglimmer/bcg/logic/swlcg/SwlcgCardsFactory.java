package de.oglimmer.bcg.logic.swlcg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.oglimmer.bcg.logic.Card;
import de.oglimmer.bcg.logic.CardList;
import de.oglimmer.bcg.logic.CardsSet;
import de.oglimmer.bcg.logic.GameException;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.logic.config.AbstractCardsFactory;
import de.oglimmer.bcg.logic.config.CardsFactory;
import de.oglimmer.bcg.logic.config.OctgnCardsFactory;

class SwlcgCardsFactory extends OctgnCardsFactory implements CardsFactory {

	private static final String COMMITED_FORCE_LIGHT = "Star Wars LCG Light - 0000b.jpg";
	private static final String COMMITED_FORCE_DARK = "Star Wars LCG Dark - 0000b.jpg";
	private static final String DEFAULT_BACKGROUND_LIGHT = "Star Wars LCG Light - 0001.jpg";
	private static final String DEFAULT_BACKGROUND_DARK = "Star Wars LCG Dark - 0001.jpg";
	static final String EMPTY_CARDS_IMG = "Star Wars LCG - 0000.jpg";
	private static final String[] REF_FILES = { "/Core.xml",
			"/Desolation-Of-Hoth.xml" };

	private class Data extends AbstractCardsFactory.Data {
		public Data(Player owner, InputStream deckStream,
				String cardBackgroundUrl, String forceImageUrl) {
			super(owner, deckStream, cardBackgroundUrl);
			this.forceImageUrl = forceImageUrl;
		}

		public String forceImageUrl;
	}

	public SwlcgCardsFactory() {
		super();
	}

	@Override
	public CardsSet createCardsSet(Player player, InputStream deckStream) {
		try {
			Data data = new Data(
					player,
					deckStream,
					(player.getSide() == SwlcgSide.DARK ? DEFAULT_BACKGROUND_DARK
							: DEFAULT_BACKGROUND_LIGHT),
					player.getSide() == SwlcgSide.DARK ? COMMITED_FORCE_DARK
							: COMMITED_FORCE_LIGHT);

			CardList table = createUniqueTable(data);
			data.cards.add(new CardList(SwlcgCardDeck.LISTNAME_HAND));
			createObjectiveDeck(data);
			createCommandDeck(data);
			handleAffiliationDeck(table, data);
			createDiscardDeck(data);
			createLostobjectivesDeck(data);
			return new CardsSet(data.owner.getGame(), data.owner,
					data.playerNo, data.cards);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new GameException("Failed to set JSON data", e);
		}
	}

	private synchronized CardList createUniqueTable(Data data) {
		CardList table = null;
		for (Player play : data.owner.getGame().getPlayers().getPlayers()) {
			CardList tableList = play.getCardStacks().getByName(
					CardList.LISTNAME_TABLE);
			if (tableList != null) {
				table = tableList;
			}
		}
		if (table != null) {
			data.cards.add(table);
		} else {
			table = new CardList(CardList.LISTNAME_TABLE);
			data.cards.add(table);
			Card balanceCard = new BalanceOfTheForceCard();
			balanceCard.setFaceup(true);
			balanceCard.setX(10);
			balanceCard.setY(298);
			table.getCards().add(balanceCard);

			Card deathStarDialCard = new DeathStarDialCard();
			deathStarDialCard.setFaceup(true);
			deathStarDialCard.setX(5);
			deathStarDialCard.setY(364);
			table.getCards().add(deathStarDialCard);
		}
		return table;
	}

	private void createDiscardDeck(Data data) {
		int discardDeckPos = 100 + 100 * data.playerNo;
		CardList discardDeck = new SwlcgCardDeck(
				SwlcgCardDeck.DECKNAME_DISCARD, SwlcgCardDeck.DECKDESC_DISCARD,
				data.owner, 10, discardDeckPos, true);
		data.cards.add(discardDeck);
	}

	private void createLostobjectivesDeck(Data data) {
		CardList lostObjectivesDeck = new SwlcgCardDeck(
				SwlcgCardDeck.DECKNAME_LOSTOBJECTIVES,
				SwlcgCardDeck.DECKDESC_LOSTOBJECTIVES, data.owner, 0, 0, true);
		data.cards.add(lostObjectivesDeck);
	}

	private void handleAffiliationDeck(CardList table, Data data)
			throws ParserConfigurationException, SAXException, IOException {
		CardList affiliationDeck = new SwlcgCardDeck(
				SwlcgCardDeck.DECKNAME_AFFILIATION, "", data.owner, 0, 0, false);
		addCards(affiliationDeck, getImageUrls("Affiliation", data),
				AffiliationCard.class, data);

		// special case: affiliationDeck has only one card and that one goes
		// directly to the table
		Card affiCard = affiliationDeck.getCards().get(0);
		affiCard.setFaceup(true);
		affiCard.setX(affiCard.getX() + 200 * data.playerNo);
		table.getCards().add(affiCard);

		for (int i = 0; i < 3; i++) {
			Card force = new CommitForceCard(data.owner, data.forceImageUrl);
			force.setFaceup(true);
			force.setY(200);
			force.setX(force.getX() + 200 * data.playerNo + i * 20);
			table.getCards().add(force);
		}
	}

	private void createCommandDeck(Data data)
			throws ParserConfigurationException, SAXException, IOException {
		CardList commandDeck = new SwlcgCardDeck(
				SwlcgCardDeck.DECKNAME_COMMAND, SwlcgCardDeck.DECKDESC_COMMAND,
				data.owner, 10, 100, false);
		addCards(commandDeck, getImageUrls("Command Deck", data),
				SwlcgCommandCard.class, data);
		Collections.shuffle(commandDeck.getCards());
		data.cards.add(commandDeck);
	}

	private void createObjectiveDeck(Data data)
			throws ParserConfigurationException, SAXException, IOException {
		CardList objectiveDeck = new SwlcgCardDeck(
				SwlcgCardDeck.DECKNAME_OBJECTIVE,
				SwlcgCardDeck.DECKDESC_OBJECTIVE, data.owner, 10, 10, false);
		addCards(objectiveDeck, getImageUrls("Objective Deck", data),
				SwlcgObjectiveCard.class, data);
		Collections.shuffle(objectiveDeck.getCards());
		data.cards.add(objectiveDeck);
	}

	@Override
	protected String getCardnameFromFilename(String cardSetFileName,
			String cardFileName) {
		StringBuilder tmp = new StringBuilder();
		String cardSetName = cardSetFileName.substring(1).toLowerCase();
		cardSetName = cardSetName.substring(0, cardSetName.indexOf("."));
		// remove the set name, card number and file extension from the card
		// file name
		for (int j = cardFileName.length() - 1; j > 0; j--) {
			if (cardFileName.substring(j).startsWith(cardSetName)) {
				tmp.append(cardFileName.substring(0, j - 1));
			}
		}
		// make it nicely readable (remove hyphens and make Camelcase
		StringBuilder cardName = new StringBuilder();
		for (String s : tmp.toString().split("-")) {
			if (cardName.length() > 0) {
				cardName.append(' ');
			}
			cardName.append(s.substring(0, 1).toUpperCase() + s.substring(1));
		}
		return cardName.toString();
	}

	@Override
	protected String[] getRefFiles() {
		return REF_FILES;
	}

}
