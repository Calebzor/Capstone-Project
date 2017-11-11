package hu.tvarga.cheaplist.business.compare.settings;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import hu.tvarga.cheaplist.R;
import hu.tvarga.cheaplist.business.UserService;
import hu.tvarga.cheaplist.business.compare.CompareService;
import hu.tvarga.cheaplist.dao.ItemCategory;
import hu.tvarga.cheaplist.dao.UserCategoryFilterListItem;
import hu.tvarga.cheaplist.ui.compare.settings.CompareSettingsCategoryHolder;

public class CompareSettingsAdapter extends RecyclerView.Adapter<CompareSettingsCategoryHolder> {

	private final UserService userService;
	private final CompareService compareService;

	CompareSettingsAdapter(UserService userService, CompareService compareService) {
		this.userService = userService;
		this.compareService = compareService;
	}

	@Override
	public CompareSettingsCategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View viewHolder = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.category_filter_list_item, parent, false);
		return new CompareSettingsCategoryHolder(viewHolder);
	}

	@Override
	public void onBindViewHolder(CompareSettingsCategoryHolder holder, int position) {
		final List<UserCategoryFilterListItem> categoriesFilterForUser =
				userService.getCategoriesFilterForUser();
		List<ItemCategory> categories = compareService.getCategories();
		final ItemCategory category = categories.get(position);
		boolean filterSelected = false;

		for (UserCategoryFilterListItem filterListItem : categoriesFilterForUser) {
			if (filterListItem.category == category && filterListItem.checked) {
				filterSelected = true;
				break;
			}
		}

		holder.bind(category, filterSelected,
				getOnClickListener(categoriesFilterForUser, category));
	}

	@NonNull
	private View.OnClickListener getOnClickListener(
			final List<UserCategoryFilterListItem> categoriesFilterForUser,
			final ItemCategory category) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				for (UserCategoryFilterListItem filterListItem : categoriesFilterForUser) {
					if (filterListItem.category == category) {
						filterListItem.checked = !filterListItem.checked;
						break;
					}
				}
				ensureCategoriesFilterIsFilledIfItWasEmpty(categoriesFilterForUser, category);
				userService.setCategoriesFilterForUser(categoriesFilterForUser);
			}
		};
	}

	private void ensureCategoriesFilterIsFilledIfItWasEmpty(
			List<UserCategoryFilterListItem> categoriesFilterForUser, ItemCategory category) {
		if (categoriesFilterForUser.isEmpty()) {
			for (ItemCategory categoryInList : compareService.getCategories()) {
				UserCategoryFilterListItem filterListItem = new UserCategoryFilterListItem();
				filterListItem.category = categoryInList;
				filterListItem.checked = categoryInList.equals(category);
				categoriesFilterForUser.add(filterListItem);
			}
		}
	}

	@Override
	public int getItemCount() {
		return compareService.getCategories().size();
	}
}
