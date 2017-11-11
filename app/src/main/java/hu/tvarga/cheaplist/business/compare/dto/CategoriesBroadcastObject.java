package hu.tvarga.cheaplist.business.compare.dto;

import java.io.Serializable;
import java.util.List;

import hu.tvarga.cheaplist.dao.ItemCategory;

public class CategoriesBroadcastObject implements Serializable {

	private static final long serialVersionUID = -2455499628579734589L;
	private final List<ItemCategory> categories;

	public CategoriesBroadcastObject(List<ItemCategory> categories) {
		this.categories = categories;
	}

	public List<ItemCategory> getCategories() {
		return categories;
	}

	@Override
	public String toString() {
		return "CategoriesBroadcastObject{" + "categories=" + categories + '}';
	}
}
