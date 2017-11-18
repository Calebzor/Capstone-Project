package hu.tvarga.cheaplist.business.compare.settings.dto;

import java.util.List;

import hu.tvarga.cheaplist.dao.UserCategoryFilterListItem;

public class UserCategoryFilterListItemForDB {

	public List<UserCategoryFilterListItem> userCategoryFilterListItems;

	public UserCategoryFilterListItemForDB(
			List<UserCategoryFilterListItem> userCategoryFilterListItems) {
		this.userCategoryFilterListItems = userCategoryFilterListItems;
	}
}
