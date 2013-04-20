package de.oglimmer.bcg.logic;

import java.util.Iterator;

import net.sf.json.JSONArray;

import de.oglimmer.bcg.logic.config.SearchCategory;
import de.oglimmer.bcg.util.JSONArrayList;

public class SearchCategories implements Iterable<SearchCategory> {

	private JSONArrayList<SearchCategory> searchCategories;

	public SearchCategories(JSONArrayList<SearchCategory> searchCategories) {
		this.searchCategories = searchCategories;
	}

	public JSONArrayList<SearchCategory> getSearchCategories() {
		return searchCategories;
	}

	public SearchCategory getByName(String name) {
		SearchCategory ret = null;
		for (SearchCategory sc : searchCategories) {
			if (sc.getName().equals(name)) {
				ret = sc;
			}
		}
		return ret;
	}

	@Override
	public Iterator<SearchCategory> iterator() {
		return searchCategories.iterator();
	}

	public JSONArray toJsonArray(Player player) {
		return searchCategories.toJsonArray(player);
	}
}
