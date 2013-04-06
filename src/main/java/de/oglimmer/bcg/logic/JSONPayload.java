package de.oglimmer.bcg.logic;

public enum JSONPayload {

	BASE, COUNTER, ID, HIGHLIGHT;

	public boolean in(JSONPayload[] array) {
		for (JSONPayload aPayload : array) {
			if (aPayload == this) {
				return true;
			}
		}
		return false;
	}

}
