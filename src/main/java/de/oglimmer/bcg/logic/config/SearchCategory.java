package de.oglimmer.bcg.logic.config;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import de.oglimmer.bcg.logic.JSONPayload;
import de.oglimmer.bcg.logic.Player;
import de.oglimmer.bcg.util.JSONArrayList;
import de.oglimmer.bcg.util.JSONTransformable;

public class SearchCategory implements JSONTransformable {

	private String name;

	private String type;

	private Map<Player, JSONArrayList<String>> values = new HashMap<>();

	public SearchCategory(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public JSONArrayList<String> getValues(Player p) {
		return values.get(p);
	}

	public void setValues(JSONArrayList<String> values, Player p) {
		this.values.put(p, values);
	}

	@Override
	public JSONObject toJSON(Player player, JSONPayload... payload) {
		JSONObject ret = new JSONObject();
		ret.element("name", name);
		ret.element("type", type);
		if (!values.isEmpty()) {
			ret.element("values", values.get(player).toJsonArray(player));
		}
		return ret;
	}

}
