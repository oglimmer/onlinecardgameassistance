package de.oglimmer.bcg.logic.swccg;

import de.oglimmer.bcg.logic.Side;

class SwccgSide implements Side {
	public static final Side DARK = new SwccgSide("dark");
	public static final Side LIGHT = new SwccgSide("light");

	private String side;

	public SwccgSide(String side) {
		this.side = side;
	}

	public String toString() {
		return side;
	}
}
