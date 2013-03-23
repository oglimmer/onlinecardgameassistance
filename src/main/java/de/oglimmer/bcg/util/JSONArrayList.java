package de.oglimmer.bcg.util;

import java.util.ArrayList;

import de.oglimmer.bcg.logic.JSONPayload;
import de.oglimmer.bcg.logic.Player;

import net.sf.json.JSONArray;

/**
 * An ArrayList that can be converted to a JSON representation. Therefore all
 * elements must be able to transform themselves into JSON.
 * 
 * @author oli
 * 
 * @param <E>
 */
@SuppressWarnings("serial")
public class JSONArrayList<E> extends ArrayList<E> {

	public JSONArray toJsonArray(Player player) {
		return toJsonArray(player, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JSONArray toJsonArray(Player player, Check check) {
		JSONArray arr = new JSONArray();
		for (Object element : this) {
			if (element instanceof JSONTransformable) {
				JSONTransformable json = (JSONTransformable) element;
				if (check == null || check.isItemOkay(json)) {
					arr.add(json.toJSON(player, JSONPayload.BASE));
				}
			}
		}
		return arr;
	}

}
