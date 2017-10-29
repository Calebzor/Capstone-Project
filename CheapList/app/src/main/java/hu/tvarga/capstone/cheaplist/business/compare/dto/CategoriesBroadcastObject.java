package hu.tvarga.capstone.cheaplist.business.compare.dto;

import java.io.Serializable;
import java.util.List;

public class CategoriesBroadcastObject implements Serializable {

	private static final long serialVersionUID = -2455499628579734589L;
	private final List<String> categories;

	public CategoriesBroadcastObject(List<String> categories) {
		this.categories = categories;
	}

	public List<String> getCategories() {
		return categories;
	}

	@Override
	public String toString() {
		return "CategoriesBroadcastObject{" + "categories=" + categories + '}';
	}
}
