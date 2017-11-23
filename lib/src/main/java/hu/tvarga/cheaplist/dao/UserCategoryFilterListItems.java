package hu.tvarga.cheaplist.dao;

import com.google.cloud.firestore.annotation.Exclude;

import java.util.List;

public class UserCategoryFilterListItems {

	@Exclude
	public static final String FIELD_NAME = "userCategoryFilterListItems";

	private List<UserCategoryFilterListItem> userCategoryFilterListItems;

	public UserCategoryFilterListItems(
			List<UserCategoryFilterListItem> userCategoryFilterListItems) {
		this.userCategoryFilterListItems = userCategoryFilterListItems;
	}
}
