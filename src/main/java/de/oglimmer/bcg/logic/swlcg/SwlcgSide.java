package de.oglimmer.bcg.logic.swlcg;

import de.oglimmer.bcg.logic.Side;

class SwlcgSide implements Side {

	public static final Side DARK = new SwlcgSide("dark");
	public static final Side LIGHT = new SwlcgSide("light");

	private String side;

	public SwlcgSide(String side) {
		this.side = side;
	}

	public String toString() {
		return side;
	}
}
