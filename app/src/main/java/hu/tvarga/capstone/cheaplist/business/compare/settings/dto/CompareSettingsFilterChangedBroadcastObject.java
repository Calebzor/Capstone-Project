package hu.tvarga.capstone.cheaplist.business.compare.settings.dto;

import java.util.List;

import hu.tvarga.capstone.cheaplist.dao.UserCategoryFilterListItem;

public class CompareSettingsFilterChangedBroadcastObject {

	private List<UserCategoryFilterListItem> oldFilter;
	private List<UserCategoryFilterListItem> newFilter;

	public CompareSettingsFilterChangedBroadcastObject(List<UserCategoryFilterListItem> oldFilter,
			List<UserCategoryFilterListItem> newFilter) {
		this.oldFilter = oldFilter;
		this.newFilter = newFilter;
	}

	public List<UserCategoryFilterListItem> getOldFilter() {
		return oldFilter;
	}

	public List<UserCategoryFilterListItem> getNewFilter() {
		return newFilter;
	}

	@Override
	public String toString() {
		return "CompareSettingsFilterChangedBroadcastObject{" + "oldFilter=" + oldFilter +
				", newFilter=" + newFilter + '}';
	}
}
