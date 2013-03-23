package de.oglimmer.bcg.logic;

@SuppressWarnings("serial")
public class GameException extends RuntimeException {

	public GameException(String string) {
		super(string);
	}

	public GameException(String string, Exception e) {
		super(string, e);
	}

}
