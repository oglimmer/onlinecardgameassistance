package de.oglimmer.bcg.util;

import de.oglimmer.bcg.logic.JSONPayload;
import de.oglimmer.bcg.logic.Player;

public interface JSONTransformable {

	Object toJSON(Player player, JSONPayload... payload);

}
