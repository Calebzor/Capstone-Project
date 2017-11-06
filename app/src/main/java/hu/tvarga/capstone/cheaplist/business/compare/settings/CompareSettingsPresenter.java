package hu.tvarga.capstone.cheaplist.business.compare.settings;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import hu.tvarga.capstone.cheaplist.R;
import hu.tvarga.capstone.cheaplist.business.UserService;
import hu.tvarga.capstone.cheaplist.business.compare.CompareService;
import hu.tvarga.capstone.cheaplist.business.compare.settings.dto.CompareSettingsFilterChangedBroadcastObject;
import hu.tvarga.capstone.cheaplist.ui.compare.settings.CompareSettingsCategoryHolder;
import hu.tvarga.capstone.cheaplist.utility.EventBusWrapper;

public class CompareSettingsPresenter implements CompareSettingsContract.Presenter {

	private final CompareService compareService;
	private final UserService userService;
	private final EventBusWrapper eventBusWrapper;

	private RecyclerView.Adapter<CompareSettingsCategoryHolder> adapter;

	@Inject
	public CompareSettingsPresenter(CompareService compareService, UserService userService,
			EventBusWrapper eventBusWrapper) {
		this.compareService = compareService;
		this.userService = userService;
		this.eventBusWrapper = eventBusWrapper;
	}

	@Override
	public void onStart(CompareSettingsContract.View view) {
		eventBusWrapper.getDefault().register(this);
	}

	@Override
	public void onStop() {
		eventBusWrapper.getDefault().unregister(this);
	}

	@Subscribe
	public void handleCompareSettingsFilterChangedBroadcastObject(
			CompareSettingsFilterChangedBroadcastObject object) {
		filterChanged();
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
					Map<String, Boolean> categoriesFilterForUser =
							userService.getCategoriesFilterForUser();
					final String category = compareService.getCategories().get(position);
					boolean filterSelected = false;

					if (categoriesFilterForUser.containsKey(category)) {
						filterSelected = categoriesFilterForUser.get(category);
					}
					holder.bind(category, filterSelected, new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							Map<String, Boolean> newFilter = new HashMap<>();
							for (String categoryInList : compareService.getCategories()) {
								newFilter.put(categoryInList, categoryInList.equals(category));
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
	private void filterChanged() {
		adapter.notifyDataSetChanged();
		Map<String, Boolean> categoriesFilterForUser = userService.getCategoriesFilterForUser();
		Set<Map.Entry<String, Boolean>> entries = categoriesFilterForUser.entrySet();

		for (Map.Entry<String, Boolean> entry : entries) {
			if (entry.getValue()) {
				compareService.setCategory(entry.getKey());
				break;
			}
		}
		compareService.getData();
	}
}
