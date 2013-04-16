package de.oglimmer.bcg.logic.config;

import java.io.InputStream;

import de.oglimmer.bcg.logic.CardsSet;
import de.oglimmer.bcg.logic.Player;

public interface CardsFactory {

	CardsSet createCardsSet(Player player, InputStream deckStream);

}
