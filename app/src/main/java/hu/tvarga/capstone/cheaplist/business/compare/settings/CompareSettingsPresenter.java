package hu.tvarga.capstone.cheaplist.business.compare.settings;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.business.UserService;
import hu.tvarga.capstone.cheaplist.business.compare.CompareService;
import hu.tvarga.capstone.cheaplist.business.compare.settings.dto.CompareSettingsFilterChangedBroadcastObject;
import hu.tvarga.capstone.cheaplist.dao.ItemCategory;
import hu.tvarga.capstone.cheaplist.dao.UserCategoryFilterListItem;
import hu.tvarga.capstone.cheaplist.ui.compare.settings.CompareSettingsCategoryHolder;
import hu.tvarga.capstone.cheaplist.utility.eventbus.Event;

public class CompareSettingsPresenter implements CompareSettingsContract.Presenter {

	private final CompareService compareService;
	private final UserService userService;
	private final Event event;

	private RecyclerView.Adapter<CompareSettingsCategoryHolder> adapter;

	@Inject
	public CompareSettingsPresenter(CompareService compareService, UserService userService,
			Event event) {
		this.compareService = compareService;
		this.userService = userService;
		this.event = event;
	}

	@Override
	public void onStart(CompareSettingsContract.View view) {
		event.register(this);
	}

	@Override
	public void onStop() {
		event.unregister(this);
	}

	@Subscribe
	public void handleCompareSettingsFilterChangedBroadcastObject(
			CompareSettingsFilterChangedBroadcastObject object) {
		filterChanged(object);
	}

	@Override
	public RecyclerView.Adapter<CompareSettingsCategoryHolder> getCategoriesFilterForUserAdapter() {
		if (adapter == null) {
			adapter = new RecyclerView.Adapter<CompareSettingsCategoryHolder>() {
				@Override
				public CompareSettingsCategoryHolder onCreateViewHolder(ViewGroup parent,
						int viewType) {
					View viewHolder = LayoutInflater.from(parent.getContext()).inflate(
							R.layout.category_filter_list_item, parent, false);
					return new CompareSettingsCategoryHolder(viewHolder);
				}

				@Override
				public void onBindViewHolder(CompareSettingsCategoryHolder holder, int position) {
					List<UserCategoryFilterListItem> categoriesFilterForUser =
							userService.getCategoriesFilterForUser();
					final ItemCategory category = compareService.getCategories().get(position);
					boolean filterSelected = false;

					for (UserCategoryFilterListItem filterListItem : categoriesFilterForUser) {
						if (filterListItem.category == category && filterListItem.checked) {
							filterSelected = true;
							break;
						}
					}

					holder.bind(category, filterSelected, new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							List<UserCategoryFilterListItem> newFilter = new ArrayList<>();
							for (ItemCategory categoryInList : compareService.getCategories()) {
								UserCategoryFilterListItem filterListItem =
										new UserCategoryFilterListItem();
								filterListItem.category = categoryInList;
								filterListItem.checked = categoryInList.equals(category);
								newFilter.add(filterListItem);
							}
							userService.setCategoriesFilterForUser(newFilter);
						}
					});
				}

				@Override
				public int getItemCount() {
					return compareService.getCategories().size();
				}
			};
		}
		return adapter;
	}

	@SuppressWarnings("squid:S3398")
	private void filterChanged(final CompareSettingsFilterChangedBroadcastObject object) {

		for (UserCategoryFilterListItem filterListItem : object.getNewFilter()) {
			if (filterListItem.checked) {
				compareService.setCategory(filterListItem.category);
				break;
			}
		}
		if (object.getOldFilter().isEmpty()) {
			adapter.notifyDataSetChanged();
		}
		else {
			DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
				@Override
				public int getOldListSize() {
					return object.getOldFilter().size();
				}

				@Override
				public int getNewListSize() {
					return object.getNewFilter().size();
				}

				@Override
				public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
					return object.getOldFilter().get(oldItemPosition).category ==
							object.getNewFilter().get(newItemPosition).category;
				}

				@Override
				public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
					UserCategoryFilterListItem oldItem = object.getOldFilter().get(oldItemPosition);
					UserCategoryFilterListItem newItem = object.getNewFilter().get(newItemPosition);
					return oldItem.category == newItem.category &&
							oldItem.checked == newItem.checked;
				}
			});
			result.dispatchUpdatesTo(adapter);
		}

		compareService.getData();
	}
}
