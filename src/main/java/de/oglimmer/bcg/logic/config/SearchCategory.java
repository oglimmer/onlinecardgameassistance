package de.oglimmer.bcg.logic.config;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.logic.JSONPayload;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.util.JSONArrayList;
import de.oglimmer.bcg.util.JSONTransformable;

public class SearchCategory implements JSONTransformable {

	public enum Type {
		TEXT, LIST, NUM;
	}

	private String name;

	private Type type;

	private Map<Player, JSONArrayList<String>> values = new HashMap<>();

	public SearchCategory(String name, Type type) {
		super();
		this.name = name;
		this.type = type;
	}

	public SearchCategory(String name, Type type, String[] values) {
		this(name, type);
		JSONArrayList<String> arr = new JSONArrayList<>();
		for (String v : values) {
			arr.add(v);
		}
		this.values.put(null, arr);
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public JSONArrayList<String> getValues(Player p) {
		if (values.size() == 1) {
			// if there is only one entry in the map, we haven't saved ther per
			// player, so use "null" as the key
			p = null;
		}
		return values.get(p);
	}

	public void setValues(JSONArrayList<String> values, Player p) {
		this.values.put(p, values);
	}

	@Override
	public JSONObject toJSON(Player player, JSONPayload... payload) {
		JSONObject ret = new JSONObject();
		ret.element("name", name);
		if (!values.isEmpty()) {
			ret.element("values", getValues(player).toJsonArray(player));
		}
		return ret;
	}

}
